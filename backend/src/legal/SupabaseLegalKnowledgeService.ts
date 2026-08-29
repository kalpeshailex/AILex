// Real LegalKnowledgeService implementation, backed by the document_chunks
// table (see backend/legal_kb_schema.sql / legal_kb_seed.sql, seeded from
// the user-provided Mumbai_Legal_KB_All_Domains_Populated.xlsx research
// pass). Queries through the caller's own request-scoped Supabase client
// (see src/lib/supabase.ts) -- RLS on these tables allows any authenticated
// user to SELECT, but never mutate, this reference data.
//
// No pgvector/embeddings yet -- the corpus is small (a few dozen chunks per
// domain), so a plain domain+jurisdiction filter plus a lightweight keyword
// relevance pass is enough for now (07_RAG.md: "keep the implementation
// simple... allow hybrid retrieval later"). GeminiEmbeddingProvider is ready
// to plug in here once the corpus grows enough to need it.
import { SupabaseClient } from "@supabase/supabase-js";
import { LegalKnowledgeService } from "./LegalKnowledgeService";
import { LegalEvidence, LegalKnowledgeQuery, VerificationStatus } from "../schemas/types";

const RELEVANT_JURISDICTIONS = ["India", "Maharashtra", "Mumbai"];
const USABLE_VERIFICATION_STATUSES = ["verified", "pending_review"];
const MAX_RESULTS = 6;
const MIN_TOKEN_LENGTH = 3;

interface DocumentChunkRow {
  chunk_id: string;
  content: string;
  domain: string;
  jurisdiction: string;
  authority: string | null;
  effective_date: string | null;
  verification_status: string;
  source_url: string | null;
  legal_sources: { title: string; source_type: string; official_url: string | null; last_verified_at: string | null } | null;
  law_sections: { law_short_name: string | null; section_number: string | null } | null;
}

export class SupabaseLegalKnowledgeService implements LegalKnowledgeService {
  constructor(private readonly supabase: SupabaseClient) {}

  async retrieve(query: LegalKnowledgeQuery): Promise<LegalEvidence[]> {
    const { data, error } = await this.supabase
      .from("document_chunks")
      .select(
        `chunk_id, content, domain, jurisdiction, authority, effective_date, verification_status, source_url,
         legal_sources ( title, source_type, official_url, last_verified_at ),
         law_sections ( law_short_name, section_number )`
      )
      .eq("domain", query.domain.toLowerCase())
      .in("jurisdiction", RELEVANT_JURISDICTIONS)
      .in("verification_status", USABLE_VERIFICATION_STATUSES);

    if (error || !data) return [];

    const evidence = (data as unknown as DocumentChunkRow[]).map(mapRow);
    return rankByRelevance(evidence, query.scenario).slice(0, MAX_RESULTS);
  }
}

function mapRow(row: DocumentChunkRow): LegalEvidence {
  const source = row.legal_sources;
  const section = row.law_sections;
  const sectionReference = section?.section_number
    ? `${section.law_short_name ?? ""} s.${section.section_number}`.trim()
    : null;

  return {
    source_id: row.chunk_id,
    title: source?.title ?? row.authority ?? "Untitled source",
    source_type: source?.source_type ?? "reference",
    issuing_authority: row.authority ?? source?.title ?? "",
    jurisdiction: row.jurisdiction,
    effective_date: row.effective_date,
    verification_status: row.verification_status as VerificationStatus,
    section_reference: sectionReference,
    content: row.content,
    official_url: row.source_url ?? source?.official_url ?? null,
    last_verified_at: source?.last_verified_at ?? null,
  };
}

/**
 * Keyword overlap between the classifier's scenario label (e.g. "UPI_FRAUD")
 * and each chunk's content -- not semantic search, just enough to prefer the
 * chunks that are actually about this scenario over other chunks in the same
 * domain. Falls back to the full domain set if nothing scores (still legal,
 * verified-or-pending content for the right domain -- better than nothing).
 */
function rankByRelevance(evidence: LegalEvidence[], scenario?: string): LegalEvidence[] {
  if (!scenario) return evidence;
  const tokens = scenario
    .toLowerCase()
    .split(/[^a-z0-9]+/)
    .filter((t) => t.length >= MIN_TOKEN_LENGTH);
  if (tokens.length === 0) return evidence;

  const scored = evidence.map((e) => {
    const haystack = e.content.toLowerCase();
    const score = tokens.reduce((acc, t) => acc + (haystack.includes(t) ? 1 : 0), 0);
    return { evidence: e, score };
  });

  const matched = scored.filter((s) => s.score > 0);
  const chosen = matched.length > 0 ? matched : scored;
  return chosen.sort((a, b) => b.score - a.score).map((s) => s.evidence);
}

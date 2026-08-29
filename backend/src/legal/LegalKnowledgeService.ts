// Legal Knowledge Service abstraction (02_ARCHITECTURE.md "Legal boundary",
// 07_RAG.md, 08_LEGAL_KNOWLEDGE.md). AIOrchestrator must depend only on this
// interface -- it must NEVER query pgvector/Supabase tables directly (see
// the task's "Legal Knowledge Boundary" section).
//
//   AI Orchestrator -> LegalKnowledgeService -> Supabase PostgreSQL + pgvector
//
// There is no real legal corpus or pgvector retrieval implementation yet
// (see backend/README.md and 19_PROJECT_STATUS.md). This file also ships
// MockLegalKnowledgeService, which is explicitly a development/testing
// stand-in: by default it returns zero results for every query, so the
// pipeline's honest "I don't have enough verified information to answer
// this reliably" fallback is what actually runs end to end until a real
// implementation is wired up. It is NOT a substitute legal corpus, and
// must never be deployed as if it were production legal knowledge.
import { LegalEvidence, LegalKnowledgeQuery } from "../schemas/types";

export interface LegalKnowledgeService {
  retrieve(query: LegalKnowledgeQuery): Promise<LegalEvidence[]>;
}

export class MockLegalKnowledgeService implements LegalKnowledgeService {
  /**
   * Test-only fixture evidence, injected explicitly by callers (never
   * shipped with real-looking content by default). Each fixture is matched
   * against a query by domain/jurisdiction/scenario/authority, mimicking the
   * filtering a real implementation would do, but without vector search.
   */
  constructor(private readonly fixtures: LegalEvidence[] = []) {}

  async retrieve(query: LegalKnowledgeQuery): Promise<LegalEvidence[]> {
    return this.fixtures.filter((evidence) => {
      if (evidence.verification_status !== "verified") return false;
      if (evidence.jurisdiction !== query.jurisdiction && evidence.jurisdiction !== "ANY") return false;
      return true;
    });
  }
}

// Citation Validator (task's "Citation Validator" section, 07_RAG.md
// "Citation"). Pure, deterministic logic -- no AI call. Validates every
// entry in FinalResponse.legal_basis against the legal evidence actually
// retrieved for this request, and only then populates FinalResponse.citations.
//
// Known limitation (flagged, not hidden): this validates the structured
// legal_basis list, which is the only claim-to-source mapping FinalResponse
// carries today. The free-text rights/obligations/authority_powers/
// what_may_apply bullets are generated under the same evidence-grounded
// system prompt but are not individually traceable to a specific source_id
// yet -- see the final report's "Known limitations".
import {
  CitationCheckResult,
  CitationRef,
  CitationValidationResult,
  FinalResponse,
  LegalClaim,
  LegalEvidence,
  VerificationStatus,
} from "../schemas/types";

const USABLE_UNQUALIFIED: VerificationStatus[] = ["verified"];
const USABLE_QUALIFIED: VerificationStatus[] = ["pending_review", "draft"];
// outdated / superseded / rejected are never usable, however similar the retrieval match was.

export function validateCitations(
  response: FinalResponse,
  evidenceUsed: LegalEvidence[]
): CitationValidationResult {
  const evidenceById = new Map(evidenceUsed.map((e) => [e.source_id, e]));
  const checks: CitationCheckResult[] = [];
  const survivingClaims: LegalClaim[] = [];
  const citations: CitationRef[] = [];
  const seenCitationIds = new Set<string>();
  let wasModified = false;

  for (const claim of response.legal_basis) {
    const evidence = claim.source_id ? evidenceById.get(claim.source_id) : undefined;
    const check = checkClaim(claim, evidence);
    checks.push(check);

    if (check.support === "UNSUPPORTED") {
      wasModified = true;
      continue; // remove the claim entirely
    }

    if (check.support === "PARTIALLY_SUPPORTED" && evidence) {
      survivingClaims.push({
        ...claim,
        claim: `${claim.claim} (this source is under review / not yet fully verified -- confirm against the official source before relying on it)`,
      });
      wasModified = true;
    } else {
      survivingClaims.push(claim);
    }

    if (evidence && !seenCitationIds.has(evidence.source_id)) {
      seenCitationIds.add(evidence.source_id);
      citations.push({
        source_id: evidence.source_id,
        title: evidence.title,
        section_reference: evidence.section_reference,
        official_url: evidence.official_url,
        jurisdiction: evidence.jurisdiction,
        effective_date: evidence.effective_date,
      });
    }
  }

  return {
    response: { ...response, legal_basis: survivingClaims, citations },
    checks,
    wasModified,
  };
}

function checkClaim(claim: LegalClaim, evidence: LegalEvidence | undefined): CitationCheckResult {
  const base = {
    claim: claim.claim,
    source_id: claim.source_id,
    section_reference: claim.section_reference,
  };

  if (!evidence) {
    return { ...base, jurisdiction: null, effective_date: null, verification_status: null, support: "UNSUPPORTED" };
  }

  const status = evidence.verification_status;
  const support = USABLE_UNQUALIFIED.includes(status)
    ? "SUPPORTED"
    : USABLE_QUALIFIED.includes(status)
      ? "PARTIALLY_SUPPORTED"
      : "UNSUPPORTED";

  return {
    ...base,
    jurisdiction: evidence.jurisdiction,
    effective_date: evidence.effective_date,
    verification_status: status,
    support,
  };
}

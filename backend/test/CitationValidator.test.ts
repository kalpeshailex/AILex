import { describe, expect, it } from "vitest";
import { validateCitations } from "../src/validation/CitationValidator";
import { FinalResponse, LegalEvidence } from "../src/schemas/types";

function baseResponse(overrides: Partial<FinalResponse> = {}): FinalResponse {
  return {
    summary: "summary",
    situation: "situation",
    what_may_apply: [],
    rights: [],
    obligations: [],
    authority_powers: [],
    actions: [],
    avoid: [],
    preserve: [],
    legal_basis: [],
    escalation: [],
    citations: [],
    needs_follow_up: false,
    next_question: null,
    ...overrides,
  };
}

const verifiedEvidence: LegalEvidence = {
  source_id: "src-verified",
  title: "Verified rule",
  source_type: "statute",
  issuing_authority: "Government of Maharashtra",
  jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
  effective_date: "2024-01-01",
  verification_status: "verified",
  section_reference: "Section 5",
  content: "...",
  official_url: "https://example.gov.in/rule",
  last_verified_at: "2026-01-01",
};

const pendingEvidence: LegalEvidence = { ...verifiedEvidence, source_id: "src-pending", verification_status: "pending_review" };
const supersededEvidence: LegalEvidence = { ...verifiedEvidence, source_id: "src-superseded", verification_status: "superseded" };

describe("CitationValidator", () => {
  it("marks a claim backed by a verified source as SUPPORTED and includes it in citations", () => {
    const response = baseResponse({
      legal_basis: [{ claim: "Helmets are required.", source_id: "src-verified", section_reference: "Section 5" }],
    });

    const result = validateCitations(response, [verifiedEvidence]);
    expect(result.checks[0].support).toBe("SUPPORTED");
    expect(result.response.legal_basis).toHaveLength(1);
    expect(result.response.citations).toHaveLength(1);
    expect(result.response.citations[0].source_id).toBe("src-verified");
    expect(result.wasModified).toBe(false);
  });

  it("removes a claim with no matching source as UNSUPPORTED -- never fabricates a citation", () => {
    const response = baseResponse({
      legal_basis: [{ claim: "Made up legal claim.", source_id: "does-not-exist", section_reference: null }],
    });

    const result = validateCitations(response, [verifiedEvidence]);
    expect(result.checks[0].support).toBe("UNSUPPORTED");
    expect(result.response.legal_basis).toHaveLength(0);
    expect(result.response.citations).toHaveLength(0);
    expect(result.wasModified).toBe(true);
  });

  it("removes a claim backed by a superseded source even though the source_id matched (similarity is not enough)", () => {
    const response = baseResponse({
      legal_basis: [{ claim: "Old rule.", source_id: "src-superseded", section_reference: null }],
    });

    const result = validateCitations(response, [supersededEvidence]);
    expect(result.checks[0].support).toBe("UNSUPPORTED");
    expect(result.response.legal_basis).toHaveLength(0);
  });

  it("qualifies (does not silently pass) a claim backed by a pending-review source as PARTIALLY_SUPPORTED", () => {
    const response = baseResponse({
      legal_basis: [{ claim: "Possibly-applicable rule.", source_id: "src-pending", section_reference: null }],
    });

    const result = validateCitations(response, [pendingEvidence]);
    expect(result.checks[0].support).toBe("PARTIALLY_SUPPORTED");
    expect(result.response.legal_basis).toHaveLength(1);
    expect(result.response.legal_basis[0].claim).toMatch(/not yet fully verified/i);
    expect(result.response.citations).toHaveLength(1);
    expect(result.wasModified).toBe(true);
  });

  it("a claim with no source_id at all is UNSUPPORTED", () => {
    const response = baseResponse({
      legal_basis: [{ claim: "Unsourced claim.", source_id: null, section_reference: null }],
    });

    const result = validateCitations(response, [verifiedEvidence]);
    expect(result.checks[0].support).toBe("UNSUPPORTED");
    expect(result.response.legal_basis).toHaveLength(0);
  });

  it("deduplicates citations for multiple claims sharing one source", () => {
    const response = baseResponse({
      legal_basis: [
        { claim: "Claim A.", source_id: "src-verified", section_reference: null },
        { claim: "Claim B.", source_id: "src-verified", section_reference: null },
      ],
    });

    const result = validateCitations(response, [verifiedEvidence]);
    expect(result.response.legal_basis).toHaveLength(2);
    expect(result.response.citations).toHaveLength(1);
  });
});

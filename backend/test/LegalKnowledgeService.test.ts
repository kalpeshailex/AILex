import { describe, expect, it } from "vitest";
import { MockLegalKnowledgeService } from "../src/legal/LegalKnowledgeService";
import { LegalEvidence } from "../src/schemas/types";

const JURISDICTION = "Mumbai Metropolitan Region, Maharashtra";

describe("LegalKnowledgeService (mock/dev implementation)", () => {
  it("returns nothing by default -- there is no real legal corpus yet", async () => {
    const service = new MockLegalKnowledgeService();
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: JURISDICTION });
    expect(result).toEqual([]);
  });

  it("returns explicitly-injected verified fixtures matching the jurisdiction", async () => {
    const fixture: LegalEvidence = {
      source_id: "fixture-1",
      title: "[TEST FIXTURE] Helmet rule",
      source_type: "statute",
      issuing_authority: "Government of Maharashtra",
      jurisdiction: JURISDICTION,
      effective_date: "2020-01-01",
      verification_status: "verified",
      section_reference: "Section 129",
      content: "Test fixture content, not real legal text.",
      official_url: "https://example.invalid/test-fixture",
      last_verified_at: "2026-01-01",
    };
    const service = new MockLegalKnowledgeService([fixture]);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: JURISDICTION });
    expect(result).toEqual([fixture]);
  });

  it("never returns a fixture for a different jurisdiction", async () => {
    const fixture: LegalEvidence = {
      source_id: "fixture-1",
      title: "[TEST FIXTURE] Helmet rule",
      source_type: "statute",
      issuing_authority: "Government of Maharashtra",
      jurisdiction: "Delhi",
      effective_date: "2020-01-01",
      verification_status: "verified",
      section_reference: "Section 129",
      content: "Test fixture content, not real legal text.",
      official_url: "https://example.invalid/test-fixture",
      last_verified_at: "2026-01-01",
    };
    const service = new MockLegalKnowledgeService([fixture]);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: JURISDICTION });
    expect(result).toEqual([]);
  });

  it("never returns an unverified fixture, even if injected", async () => {
    const fixture: LegalEvidence = {
      source_id: "fixture-1",
      title: "[TEST FIXTURE] Draft rule",
      source_type: "statute",
      issuing_authority: "Government of Maharashtra",
      jurisdiction: JURISDICTION,
      effective_date: "2020-01-01",
      verification_status: "draft",
      section_reference: null,
      content: "Draft, not yet verified.",
      official_url: null,
      last_verified_at: null,
    };
    const service = new MockLegalKnowledgeService([fixture]);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: JURISDICTION });
    expect(result).toEqual([]);
  });
});

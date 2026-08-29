import { describe, expect, it } from "vitest";
import { SupabaseLegalKnowledgeService } from "../src/legal/SupabaseLegalKnowledgeService";
import { fakeSupabaseClient } from "./support/FakeSupabaseClient";

const helmetChunk = {
  chunk_id: "chunk_helmet",
  content: "Riding without a helmet under the Motor Vehicles Act...",
  domain: "traffic",
  jurisdiction: "Maharashtra",
  authority: "Maharashtra Transport Department",
  effective_date: "2020-01-01",
  verification_status: "verified",
  source_url: null,
  legal_sources: { title: "Motor Vehicles Act, 1988", source_type: "act", official_url: "https://example.gov.in/mv-act", last_verified_at: "2026-01-01" },
  law_sections: { law_short_name: "MV Act", section_number: "129" },
};

const seatbeltChunk = {
  chunk_id: "chunk_seatbelt",
  content: "Not wearing a seatbelt while driving a four-wheeler...",
  domain: "traffic",
  jurisdiction: "India",
  authority: "Ministry of Road Transport",
  effective_date: "2019-01-01",
  verification_status: "pending_review",
  source_url: "https://example.gov.in/seatbelt",
  legal_sources: null,
  law_sections: null,
};

describe("SupabaseLegalKnowledgeService", () => {
  it("maps document_chunks rows (with joined source/section) to LegalEvidence", async () => {
    const client = fakeSupabaseClient({ document_chunks: { data: [helmetChunk], error: null } });
    const service = new SupabaseLegalKnowledgeService(client);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: "Mumbai Metropolitan Region, Maharashtra" });

    expect(result).toHaveLength(1);
    expect(result[0]).toMatchObject({
      source_id: "chunk_helmet",
      title: "Motor Vehicles Act, 1988",
      section_reference: "MV Act s.129",
      verification_status: "verified",
      official_url: "https://example.gov.in/mv-act",
    });
  });

  it("falls back to authority/source_url when legal_sources join is absent", async () => {
    const client = fakeSupabaseClient({ document_chunks: { data: [seatbeltChunk], error: null } });
    const service = new SupabaseLegalKnowledgeService(client);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: "Mumbai Metropolitan Region, Maharashtra" });

    expect(result[0].title).toBe("Ministry of Road Transport");
    expect(result[0].official_url).toBe("https://example.gov.in/seatbelt");
    expect(result[0].section_reference).toBeNull();
  });

  it("ranks chunks matching the scenario's keywords ahead of unrelated ones in the same domain", async () => {
    const client = fakeSupabaseClient({ document_chunks: { data: [seatbeltChunk, helmetChunk], error: null } });
    const service = new SupabaseLegalKnowledgeService(client);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: "x", scenario: "HELMET" });

    expect(result[0].source_id).toBe("chunk_helmet");
  });

  it("falls back to the full domain set when no chunk matches the scenario keywords", async () => {
    const client = fakeSupabaseClient({ document_chunks: { data: [seatbeltChunk, helmetChunk], error: null } });
    const service = new SupabaseLegalKnowledgeService(client);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: "x", scenario: "UNRELATED_SCENARIO_XYZ" });

    expect(result).toHaveLength(2);
  });

  it("returns an empty array on a query error rather than throwing", async () => {
    const client = fakeSupabaseClient({ document_chunks: { data: null, error: { message: "boom" } } });
    const service = new SupabaseLegalKnowledgeService(client);
    const result = await service.retrieve({ domain: "TRAFFIC", jurisdiction: "x" });
    expect(result).toEqual([]);
  });
});

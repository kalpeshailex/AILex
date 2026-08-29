import { describe, expect, it } from "vitest";
import { extractContext, mergeContext } from "../src/context/ContextExtractor";
import { FakeAIProvider } from "./support/FakeAIProvider";
import { ExtractedContext } from "../src/schemas/types";

describe("ContextExtractor", () => {
  it("extracts only what the model stated and computes known_facts", async () => {
    const ai = new FakeAIProvider().queueStructured({
      what_happened: "Police stopped the user.",
      when: null,
      where: null,
      authority_or_person: "Police",
      actions_taken: null,
      documents_mentioned: [],
      immediate_risk: null,
      user_objective: null,
      missing_material_facts: ["What reason did they give?"],
    });

    const result = await extractContext(ai, { message: "Police stopped me.", domain: "POLICE", scenario: "STOP" });
    expect(result.what_happened).toBe("Police stopped the user.");
    expect(result.authority_or_person).toBe("Police");
    expect(result.known_facts.some((f) => f.field === "authority_or_person" && f.source === "user_stated")).toBe(true);
    expect(result.missing_material_facts).toEqual(["What reason did they give?"]);
  });

  it("does not repeat an established fact as missing across turns (the classic 'Police stopped me' example)", () => {
    const previous: ExtractedContext = {
      what_happened: "Police stopped the user.",
      when: null,
      where: null,
      authority_or_person: "Police",
      actions_taken: null,
      documents_mentioned: [],
      immediate_risk: null,
      user_objective: null,
      known_facts: [{ field: "authority_or_person", value: "Police", source: "user_stated" }],
      missing_material_facts: ["What reason did they give?"],
    };

    const merged = mergeContext(previous, {
      what_happened: "Police said the user was driving without a helmet.",
      when: null,
      where: null,
      authority_or_person: null, // not restated this turn
      actions_taken: null,
      documents_mentioned: [],
      immediate_risk: null,
      user_objective: null,
      missing_material_facts: ["Did they issue a challan?"],
    });

    // authority_or_person carried forward, not lost, and not re-flagged as missing.
    expect(merged.authority_or_person).toBe("Police");
    expect(merged.what_happened).toBe("Police said the user was driving without a helmet.");
    expect(merged.missing_material_facts).toEqual(["Did they issue a challan?"]);
    const carried = merged.known_facts.find((f) => f.field === "authority_or_person");
    expect(carried?.source).toBe("carried_forward");
  });

  it("deduplicates documents_mentioned across turns", () => {
    const previous: ExtractedContext = {
      what_happened: null,
      when: null,
      where: null,
      authority_or_person: null,
      actions_taken: null,
      documents_mentioned: ["challan copy"],
      immediate_risk: null,
      user_objective: null,
      known_facts: [],
      missing_material_facts: [],
    };

    const merged = mergeContext(previous, {
      what_happened: null,
      when: null,
      where: null,
      authority_or_person: null,
      actions_taken: null,
      documents_mentioned: ["challan copy", "licence"],
      immediate_risk: null,
      user_objective: null,
      missing_material_facts: [],
    });

    expect(merged.documents_mentioned).toEqual(["challan copy", "licence"]);
  });

  it("never invents a value the model returned as null", () => {
    const merged = mergeContext(null, {
      what_happened: "Police stopped the user.",
      when: null,
      where: null,
      authority_or_person: null,
      actions_taken: null,
      documents_mentioned: [],
      immediate_risk: null,
      user_objective: null,
      missing_material_facts: [],
    });
    expect(merged.where).toBeNull();
    expect(merged.known_facts.some((f) => f.field === "where")).toBe(false);
  });
});

import { describe, expect, it } from "vitest";
import { classifySituation } from "../src/classification/SituationClassifier";
import { FakeAIProvider } from "./support/FakeAIProvider";

describe("SituationClassifier", () => {
  it("passes through a confident, in-scope classification unchanged", async () => {
    const ai = new FakeAIProvider().queueStructured({
      domain: "TRAFFIC",
      scenario: "HELMET",
      jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
      confidence: 0.9,
      clarification_required: false,
    });

    const result = await classifySituation(ai, { message: "I got a challan for not wearing a helmet." });
    expect(result.domain).toBe("TRAFFIC");
    expect(result.clarification_required).toBe(false);
  });

  it("forces clarification when the model itself returns UNKNOWN (ambiguous scenario)", async () => {
    const ai = new FakeAIProvider().queueStructured({
      domain: "UNKNOWN",
      scenario: "UNCLEAR",
      jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
      confidence: 0.3,
      clarification_required: true,
    });

    const result = await classifySituation(ai, { message: "Something happened, not sure what to do." });
    expect(result.domain).toBe("UNKNOWN");
    expect(result.clarification_required).toBe(true);
    expect(result.clarification_question).toBeTruthy();
  });

  it("coerces a domain outside the 5 approved V1 domains to UNKNOWN and forces clarification", async () => {
    // Simulates a model hallucinating an out-of-scope domain (e.g. HOUSING) --
    // the deterministic guard must not trust it.
    const ai = new FakeAIProvider().queueStructured({
      domain: "HOUSING",
      scenario: "RENT_DISPUTE",
      jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
      confidence: 0.8,
      clarification_required: false,
    });

    const result = await classifySituation(ai, { message: "My landlord is not returning my deposit." });
    expect(result.domain).toBe("UNKNOWN");
    expect(result.clarification_required).toBe(true);
  });

  it("forces clarification for unsupported jurisdiction even if the model is confident", async () => {
    const ai = new FakeAIProvider().queueStructured({
      domain: "POLICE",
      scenario: "STOP",
      jurisdiction: "UNSUPPORTED",
      confidence: 0.95,
      clarification_required: false,
    });

    const result = await classifySituation(ai, { message: "Police stopped me in Delhi." });
    expect(result.clarification_required).toBe(true);
  });

  it("forces clarification when confidence is low even if the model didn't ask for it", async () => {
    const ai = new FakeAIProvider().queueStructured({
      domain: "CYBER",
      scenario: "UPI_FRAUD",
      jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
      confidence: 0.2,
      clarification_required: false,
    });

    const result = await classifySituation(ai, { message: "Something about money, not sure." });
    expect(result.clarification_required).toBe(true);
  });

  it("clamps an out-of-range confidence value into [0,1]", async () => {
    const ai = new FakeAIProvider().queueStructured({
      domain: "POLICE",
      scenario: "STOP",
      jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
      confidence: 5,
      clarification_required: false,
    });

    const result = await classifySituation(ai, { message: "Police stopped me." });
    expect(result.confidence).toBe(1);
  });

  it("passes prior domain/scenario context into the prompt", async () => {
    const ai = new FakeAIProvider().queueStructured({
      domain: "TRAFFIC",
      scenario: "HELMET",
      jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
      confidence: 0.9,
      clarification_required: false,
    });

    await classifySituation(ai, {
      message: "They said I wasn't wearing a helmet.",
      priorDomain: "TRAFFIC",
      priorScenario: "STOP",
    });

    expect(ai.calls[0].prompt).toContain("TRAFFIC");
    expect(ai.calls[0].prompt).toContain("STOP");
  });
});

import { describe, expect, it } from "vitest";
import { planActions } from "../src/planning/ActionPlanner";
import { FakeAIProvider } from "./support/FakeAIProvider";
import { ExtractedContext, LegalEvidence, RiskAssessment } from "../src/schemas/types";

const emptyContext: ExtractedContext = {
  what_happened: "Police stopped the user for a helmet check.",
  when: null,
  where: null,
  authority_or_person: "Traffic police",
  actions_taken: null,
  documents_mentioned: [],
  immediate_risk: null,
  user_objective: "Understand what to do next.",
  known_facts: [],
  missing_material_facts: [],
};

const lowRisk: RiskAssessment = {
  level: "LOW",
  reasons: [],
  immediate_safety_required: false,
  prohibited_actions: [],
  escalation_recommended: false,
};

describe("ActionPlanner", () => {
  it("never calls the AI provider when there is no verified evidence -- returns a safe generic plan instead", async () => {
    const ai = new FakeAIProvider();
    const plan = await planActions(ai, {
      domain: "TRAFFIC",
      scenario: "HELMET",
      context: emptyContext,
      risk: lowRisk,
      evidence: [],
      authorities: [],
    });

    expect(ai.calls.length).toBe(0);
    expect(plan.steps.every((s) => s.grounded_in.length === 0)).toBe(true);
    expect(plan.steps.length).toBeGreaterThan(0);
  });

  it("adds a professional-help step when escalation is recommended", async () => {
    const ai = new FakeAIProvider();
    const highRisk: RiskAssessment = { ...lowRisk, level: "HIGH", escalation_recommended: true, prohibited_actions: ["Do not confront."] };
    const plan = await planActions(ai, {
      domain: "POLICE",
      scenario: "ARREST",
      context: emptyContext,
      risk: highRisk,
      evidence: [],
      authorities: [],
    });

    expect(plan.avoid).toContain("Do not confront.");
    expect(plan.steps.some((s) => s.step.toLowerCase().includes("professional"))).toBe(true);
  });

  it("uses the AI provider when verified evidence is present, and strips any fabricated source_id", async () => {
    const evidence: LegalEvidence[] = [
      {
        source_id: "src-1",
        title: "Motor Vehicles Act helmet rule",
        source_type: "statute",
        issuing_authority: "Government of Maharashtra",
        jurisdiction: "Mumbai Metropolitan Region, Maharashtra",
        effective_date: "2020-01-01",
        verification_status: "verified",
        section_reference: "Section 129",
        content: "Riders must wear a protective helmet.",
        official_url: "https://example.gov.in/mv-act",
        last_verified_at: "2026-01-01",
      },
    ];

    const ai = new FakeAIProvider().queueStructured({
      steps: [
        { step: "You may be required to wear a helmet under Section 129.", grounded_in: ["src-1"] },
        { step: "Note the challan number if issued.", grounded_in: [] },
        { step: "This step cites a source that was never retrieved.", grounded_in: ["src-does-not-exist"] },
      ],
      avoid: ["Do not argue with the officer."],
      preserve: ["Challan copy if issued."],
    });

    const plan = await planActions(ai, {
      domain: "TRAFFIC",
      scenario: "HELMET",
      context: emptyContext,
      risk: lowRisk,
      evidence,
      authorities: [],
    });

    expect(ai.calls.length).toBe(1);
    expect(plan.steps[0].grounded_in).toEqual(["src-1"]);
    expect(plan.steps[2].grounded_in).toEqual([]); // fabricated source_id stripped
  });
});

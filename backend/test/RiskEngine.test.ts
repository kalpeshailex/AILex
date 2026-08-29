import { describe, expect, it } from "vitest";
import { assessRisk } from "../src/risk/RiskEngine";

describe("RiskEngine", () => {
  it("treats a routine police stop as LOW risk", () => {
    const result = assessRisk("Police stopped me.", null);
    expect(result.level).toBe("LOW");
    expect(result.immediate_safety_required).toBe(false);
    expect(result.escalation_recommended).toBe(false);
  });

  it("treats an arrest threat as HIGH risk", () => {
    const result = assessRisk("Police are threatening to arrest me.", null);
    expect(result.level).toBe("HIGH");
    expect(result.escalation_recommended).toBe(true);
    expect(result.reasons.length).toBeGreaterThan(0);
  });

  it("treats a plain traffic challan as LOW risk", () => {
    const result = assessRisk("I got a traffic challan.", null);
    expect(result.level).toBe("LOW");
  });

  it("treats a TC cash demand as MEDIUM, not HIGH -- flagged but not a safety emergency", () => {
    const result = assessRisk("The TC is asking for cash.", null);
    expect(result.level).toBe("MEDIUM");
  });

  it("treats an active UPI fraud (money already taken) as HIGH risk", () => {
    const result = assessRisk("Someone took money from my UPI.", null);
    expect(result.level).toBe("HIGH");
    expect(result.escalation_recommended).toBe(true);
  });

  it("treats natural spoken phrasing of the same fraud as HIGH risk, not just clinical wording", () => {
    // Found live via the app's voice-input flow -- VoiceDemo's transcript
    // didn't match any HIGH pattern until this phrasing was added.
    const result = assessRisk(
      "Someone called saying they were from my bank and forty thousand rupees has gone out of my account through UPI.",
      null
    );
    expect(result.level).toBe("HIGH");
  });

  it("treats a compromised OTP as HIGH risk", () => {
    const result = assessRisk("Someone has my OTP.", null);
    expect(result.level).toBe("HIGH");
  });

  it("treats active violence as CRITICAL and requires immediate safety", () => {
    const result = assessRisk("He is hitting me right now, help.", null);
    expect(result.level).toBe("CRITICAL");
    expect(result.immediate_safety_required).toBe(true);
    expect(result.prohibited_actions.length).toBeGreaterThan(0);
  });

  it("treats sextortion/blackmail as CRITICAL", () => {
    const result = assessRisk("He is blackmailing me and threatening to leak my photo.", null);
    expect(result.level).toBe("CRITICAL");
  });

  it("never lets CRITICAL be downgraded even if a LOW-risk phrase also appears", () => {
    const result = assessRisk("I got a traffic challan but also he is hitting me right now, help.", null);
    expect(result.level).toBe("CRITICAL");
  });

  it("also considers extracted context, not just the raw message", () => {
    const context = {
      what_happened: null,
      when: null,
      where: null,
      authority_or_person: null,
      actions_taken: null,
      documents_mentioned: [],
      immediate_risk: "He said he will arrest me if I don't pay.",
      user_objective: null,
      known_facts: [],
      missing_material_facts: [],
    };
    const result = assessRisk("What should I do?", context);
    expect(result.level).toBe("HIGH");
  });

  it("prohibited_actions is empty for LOW risk", () => {
    const result = assessRisk("I got a traffic challan.", null);
    expect(result.prohibited_actions).toEqual([]);
  });
});

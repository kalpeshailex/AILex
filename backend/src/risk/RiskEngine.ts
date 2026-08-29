// Risk Engine (task's "Risk Engine" section, 15_DECISIONS.md D009 spirit:
// deterministic rules, not LLM judgment, decide safety-critical risk level).
// Pure keyword/pattern matching over the raw user message and extracted
// context -- no AI call, no network call, fully unit-testable and fully
// auditable. The orchestrator must run this BEFORE trusting any LLM-authored
// content, and nothing downstream may override a CRITICAL/HIGH level.
import { ExtractedContext, RiskAssessment, RiskLevel } from "../schemas/types";

interface RiskRule {
  level: RiskLevel;
  reason: string;
  patterns: RegExp[];
}

// Ordered CRITICAL -> LOW; the highest-level matching rule wins. Patterns are
// deliberately simple substring/regex matches on English text -- see Known
// limitations in the final report re: Hindi/Marathi coverage.
const RULES: RiskRule[] = [
  {
    level: "CRITICAL",
    reason: "Message indicates immediate physical danger or violence in progress.",
    patterns: [
      /\b(hitting|beating|attack(ing)?|assault(ing)?)\s+me\b/i,
      /\b(help|danger|unsafe)\b.*\b(now|right now|urgent(ly)?)\b/i,
      /\bin danger\b/i,
      /\bhe(’|')?s? (going to|gonna) (hit|hurt|kill)/i,
      /\bcan(’|')?t breathe\b/i,
    ],
  },
  {
    level: "CRITICAL",
    reason: "Message indicates sexual violence, sextortion, or blackmail with threats.",
    patterns: [
      /\bsextortion\b/i,
      /\bblackmail(ing)?\b/i,
      /\bthreatening to (leak|share|post|upload)\b.*(photo|video|image|nude)/i,
      /\bsexual(ly)? (assault|abuse|harass)/i,
      /\bmolest/i,
    ],
  },
  {
    level: "HIGH",
    reason: "Message indicates arrest, detention, or a serious criminal accusation.",
    patterns: [
      /\barrest(ed|ing)?\b/i,
      /\bdetain(ed|ing)?\b/i,
      /\btaken? to (the )?(station|lock ?up)\b/i,
      /\bfil(ed|ing) an? fir\b/i,
      /\bcharged with\b/i,
    ],
  },
  {
    level: "HIGH",
    reason: "Message indicates serious police misconduct or physical assault.",
    patterns: [
      /\b(police|officer|constable)\b.*\b(hit|beat|assault|slap)/i,
      /\bassault(ed)?\b/i,
      /\bmisconduct\b/i,
      /\bcustod(y|ial) (violence|death)\b/i,
    ],
  },
  {
    level: "HIGH",
    reason: "Message indicates ongoing/active financial fraud where quick action matters.",
    patterns: [
      /\b(money|amount|rs\.?|rupees|₹)\s*\d/i,
      /\b(upi|bank|card)\b.*\bfraud\b/i,
      /\bfraud\b.*\b(upi|bank|card)\b/i,
      /\b(debited|deducted|transferred|stolen|took|taken)\b.*\b(money|amount|from my (account|upi|bank|card))\b/i,
      /\bmoney\b.*\b(was )?(debited|deducted|transferred|stolen|taken)\b/i,
      // Natural spoken/typed phrasing for money loss doesn't always use a
      // clinical verb like "debited" -- caught live via voice input testing
      // ("...forty thousand rupees has gone out of my account through UPI"
      // matched none of the patterns above).
      /\b(money|amount|rupees|rs\.?|₹)\b.*\b(gone out|went out|withdrawn|missing)\b/i,
      /\b(gone out|went out|withdrawn)\b.*\b(account|upi|bank)\b/i,
      /\bunauthoriz(ed|ation) transaction/i,
      /\botp\b.*\b(shared|gave|leaked|used)\b/i,
      /\bsomeone (has|got|took|knows)\s+my\s+(otp|pin|password|cvv|upi pin)\b/i,
    ],
  },
  {
    level: "MEDIUM",
    reason: "Message reports a cash/bribe demand from an authority -- not immediately dangerous but worth flagging.",
    patterns: [/\b(asking|wants?)\s+(for\s+)?(cash|money|a bribe)\b/i, /\bbribe\b/i],
  },
  {
    level: "HIGH",
    reason: "Message indicates a serious, time-sensitive legal deadline.",
    patterns: [/\bdeadline\b.*\b(today|tomorrow|hours?)\b/i, /\bcourt date\b.*\b(today|tomorrow)\b/i],
  },
  {
    level: "HIGH",
    reason: "Message indicates evidence at risk of imminent destruction.",
    patterns: [/\b(deleting|destroying|erasing)\b.*\bevidence\b/i, /\babout to delete\b/i],
  },
  {
    level: "MEDIUM",
    reason: "Message reports a past assault, harassment, or threat without immediacy.",
    patterns: [/\bharass(ed|ment|ing)\b/i, /\bthreat(ened|ening)?\b/i, /\bassault\b/i],
  },
  {
    level: "MEDIUM",
    reason: "Message reports an in-progress cyber incident (account compromise, scam) without indication of ongoing financial loss.",
    patterns: [/\b(hacked|compromised)\b/i, /\bscam(med)?\b/i, /\bphishing\b/i],
  },
];

/** Immediate-safety phrasing must be avoided in any generated response for CRITICAL/HIGH cases -- see SafetyValidator. */
const PROHIBITED_ACTIONS_BY_LEVEL: Record<RiskLevel, string[]> = {
  CRITICAL: [
    "Do not encourage confrontation with any person or authority.",
    "Do not suggest evading law enforcement.",
    "Do not suggest secret recording or surveillance.",
    "Do not delay recommending immediate safety/professional help.",
  ],
  HIGH: [
    "Do not encourage confrontation.",
    "Do not suggest evading lawful process.",
    "Do not guarantee an outcome.",
  ],
  MEDIUM: ["Do not guarantee an outcome.", "Do not encourage confrontation."],
  LOW: [],
};

export function assessRisk(rawMessage: string, context: ExtractedContext | null): RiskAssessment {
  const haystack = [rawMessage, context?.what_happened, context?.immediate_risk, context?.actions_taken]
    .filter((v): v is string => typeof v === "string")
    .join("\n");

  const matchedReasons: string[] = [];
  let highestLevel: RiskLevel = "LOW";

  for (const rule of RULES) {
    if (rule.patterns.some((p) => p.test(haystack))) {
      matchedReasons.push(rule.reason);
      if (levelRank(rule.level) > levelRank(highestLevel)) {
        highestLevel = rule.level;
      }
    }
  }

  return {
    level: highestLevel,
    reasons: matchedReasons,
    immediate_safety_required: highestLevel === "CRITICAL",
    prohibited_actions: PROHIBITED_ACTIONS_BY_LEVEL[highestLevel],
    escalation_recommended: highestLevel === "HIGH" || highestLevel === "CRITICAL",
  };
}

function levelRank(level: RiskLevel): number {
  return { LOW: 0, MEDIUM: 1, HIGH: 2, CRITICAL: 3 }[level];
}

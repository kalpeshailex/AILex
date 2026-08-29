// Safety Validator (task's "Safety Validator" and "Prompt Injection"
// sections). Pure, deterministic pattern matching over the final response
// text -- the last gate before anything reaches the user. Also exports
// sanitizeUntrustedText, used by the orchestrator to defensively neutralize
// instruction-like content inside retrieved legal evidence and raw user
// input before it's ever interpolated into a prompt, on top of (not instead
// of) each module's own system-prompt instructions to treat such content as
// data.
import { FinalResponse, SafetyCheckResult, SafetyFlag } from "../schemas/types";

interface SafetyRule {
  flag: SafetyFlag;
  patterns: RegExp[];
}

const RULES: SafetyRule[] = [
  {
    flag: "FABRICATED_CERTAINTY",
    patterns: [
      /\byou will (win|definitely)\b/i,
      /\bdefinitely\b/i,
      /\b100%\s*(sure|certain)\b/i,
      /\bthey cannot do anything\b/i,
      /\bthe officer has no power\b/i,
    ],
  },
  {
    flag: "UNSUPPORTED_GUARANTEE",
    patterns: [/\bguarantee(s|d)?\b/i, /\bthis guarantees\b/i, /\byou can always refuse\b/i],
  },
  {
    flag: "EVASION_ADVICE",
    patterns: [
      /\b(avoid|evade|escape|run away from)\s+(arrest|the police|police|law enforcement)\b/i,
      /\bhow to (not get caught|avoid getting caught)\b/i,
    ],
  },
  {
    flag: "CONFRONTATION_ADVICE",
    patterns: [
      /\brefuse to (comply|cooperate)\b/i,
      /\bconfront (the )?(officer|police|him|her|them)\b/i,
      /\bargue with (the )?(officer|police)\b/i,
    ],
  },
  {
    flag: "OBSTRUCTION_ADVICE",
    // "block the card/UPI/SIM" is common, safe fraud-mitigation advice --
    // require an actual person/authority object, not a bare "the" (found
    // live: "block the card and UPI access" was a false positive here
    // because "the" alone satisfied the old `(the|police)` alternation).
    patterns: [/\bblock (the )?(officer|police)\b/i, /\bprevent (the )?(officer|police) from (doing their job|arresting)\b/i],
  },
  {
    flag: "SURVEILLANCE_INSTRUCTIONS",
    patterns: [/\bsecretly record\b/i, /\bcovert(ly)? (record|film)\b/i, /\bhidden camera\b/i, /\bwiretap\b/i],
  },
  {
    flag: "CREDENTIAL_REQUEST",
    patterns: [/\b(share|send|provide|enter) your (otp|pin|cvv|password|upi pin)\b/i],
  },
  {
    flag: "PROMPT_INJECTION_DETECTED",
    patterns: [
      /\bignore (all )?previous instructions\b/i,
      /\byou are now\b/i,
      /\bsystem\s*:\s*/i,
      /\bnew instructions\b/i,
      /\bdisregard (the|your) (above|previous) (rules|instructions)\b/i,
    ],
  },
];

const RESPONSE_TEXT_FIELDS: (keyof FinalResponse)[] = [
  "summary",
  "situation",
  "what_may_apply",
  "rights",
  "obligations",
  "authority_powers",
  "avoid",
  "preserve",
  "escalation",
];

function collectResponseText(response: FinalResponse): string {
  const parts: string[] = [];
  for (const field of RESPONSE_TEXT_FIELDS) {
    const value = response[field];
    if (typeof value === "string") parts.push(value);
    else if (Array.isArray(value)) parts.push(...value.filter((v): v is string => typeof v === "string"));
  }
  parts.push(...response.actions.map((a) => a.step));
  parts.push(...response.legal_basis.map((c) => c.claim));
  if (response.next_question) parts.push(response.next_question);
  return parts.join("\n");
}

// A phrase like "confront the officer" or "argue with the officer" is fine
// -- good, even -- when it's telling the user NOT to do it (which is exactly
// what RiskEngine/ActionPlanner's own "avoid" lists say). Only flag a match
// that isn't immediately preceded by a negation, so genuine safety advice
// doesn't get mistaken for the thing it's warning against.
const NEGATION_PRECEDING = /\b(do not|don't|never|avoid|should not|shouldn't|must not|will not|won't)\s*$/i;
const NEGATION_LOOKBACK_CHARS = 25;

function containsUnnegatedMatch(text: string, pattern: RegExp): boolean {
  const flags = pattern.flags.includes("g") ? pattern.flags : `${pattern.flags}g`;
  const globalPattern = new RegExp(pattern.source, flags);
  let match: RegExpExecArray | null;
  while ((match = globalPattern.exec(text)) !== null) {
    const preceding = text.slice(Math.max(0, match.index - NEGATION_LOOKBACK_CHARS), match.index);
    if (!NEGATION_PRECEDING.test(preceding)) return true;
    if (match.index === globalPattern.lastIndex) globalPattern.lastIndex += 1; // guard against zero-length matches
  }
  return false;
}

export function validateSafety(response: FinalResponse): SafetyCheckResult {
  const text = collectResponseText(response);
  const flags: SafetyFlag[] = [];
  for (const rule of RULES) {
    if (rule.patterns.some((p) => containsUnnegatedMatch(text, p))) flags.push(rule.flag);
  }

  if (flags.length === 0) {
    return { passed: true, flags: [], safeFallback: null };
  }

  return { passed: false, flags, safeFallback: buildSafeFallback() };
}

function buildSafeFallback(): FinalResponse {
  return {
    summary: "I can't provide a reliable answer to this as phrased.",
    situation: "This response was withheld by a safety check.",
    what_may_apply: [],
    rights: [],
    obligations: [],
    authority_powers: [],
    actions: [
      {
        step: "Consider verifying your situation through an official government source, or seeking professional legal advice.",
        grounded_in: [],
      },
    ],
    avoid: [],
    preserve: [],
    legal_basis: [],
    escalation: ["Consider professional legal advice or an official government source."],
    citations: [],
    needs_follow_up: false,
    next_question: null,
  };
}

const INJECTION_MARKERS: RegExp[] = [
  /\bignore (all )?previous instructions\b/i,
  /\byou are now\b/i,
  /\bsystem\s*:\s*/i,
  /\bnew instructions\b/i,
  /\bdisregard (the|your) (above|previous) (rules|instructions)\b/i,
  /\bact as\b/i,
];

export interface SanitizeResult {
  sanitized: string;
  injectionDetected: boolean;
}

/**
 * Neutralizes instruction-like phrases inside text that is about to be
 * interpolated into a prompt as untrusted data (user input or retrieved
 * legal evidence). This is defense in depth on top of each module's system
 * prompt already instructing the model to treat such content as data, not
 * commands -- it does not rewrite the user's actual meaning otherwise.
 */
export function sanitizeUntrustedText(text: string): SanitizeResult {
  let injectionDetected = false;
  let sanitized = text;
  for (const marker of INJECTION_MARKERS) {
    if (marker.test(sanitized)) {
      injectionDetected = true;
      // Deliberately does not echo the matched text back -- the goal is
      // that no trace of the instruction-like phrase reaches a prompt, not
      // just that it's visually marked. injectionDetected is what callers
      // should log/flag, not the sanitized text itself.
      sanitized = sanitized.replace(marker, "[flagged content removed]");
    }
  }
  return { sanitized, injectionDetected };
}

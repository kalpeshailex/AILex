// Shared structured types for the AI pipeline (06_AI_ARCHITECTURE.md / 07_RAG.md /
// 08_LEGAL_KNOWLEDGE.md / 10_API.md). These are the contracts every module in
// backend/src/{normalization,classification,context,risk,legal,authority,
// planning,response,validation,orchestrator} is built around — keep them the
// single source of truth rather than letting each module invent its own shape.

/** V1 domains only — see 15_DECISIONS.md D007. Do not add more without approval. */
export type LegalDomain = "POLICE" | "TRAFFIC" | "RAILWAY" | "GOVERNMENT_RTS" | "CYBER";

export const LEGAL_DOMAINS: readonly LegalDomain[] = [
  "POLICE",
  "TRAFFIC",
  "RAILWAY",
  "GOVERNMENT_RTS",
  "CYBER",
];

export type RiskLevel = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export type InputType = "text" | "stt";

// ---------------------------------------------------------------------------
// Input Normalizer
// ---------------------------------------------------------------------------

export interface NormalizedInput {
  /** Whitespace/formatting-cleaned text. Never adds words, qualifiers or conclusions. */
  text: string;
  inputType: InputType;
  language: string;
  /** True if normalization stripped/changed anything, for observability only. */
  wasModified: boolean;
}

// ---------------------------------------------------------------------------
// Situation Classifier
// ---------------------------------------------------------------------------

export interface SituationClassification {
  domain: LegalDomain | "UNKNOWN";
  scenario: string;
  /** e.g. "Mumbai Metropolitan Region, Maharashtra" or "UNSUPPORTED" */
  jurisdiction: string;
  /** 0..1 */
  confidence: number;
  clarification_required: boolean;
  clarification_question?: string;
}

// ---------------------------------------------------------------------------
// Context Extractor
// ---------------------------------------------------------------------------

/** A fact the extractor is confident is stated (or directly implied) by the user — never invented. */
export interface KnownFact {
  field: string;
  value: string;
  /** "user_stated" for facts taken directly from the user; "carried_forward" for facts merged in from prior turns. */
  source: "user_stated" | "carried_forward";
}

export interface ExtractedContext {
  what_happened: string | null;
  when: string | null;
  where: string | null;
  authority_or_person: string | null;
  actions_taken: string | null;
  documents_mentioned: string[];
  immediate_risk: string | null;
  user_objective: string | null;
  known_facts: KnownFact[];
  /** Facts that materially change the legal path but are not yet known. */
  missing_material_facts: string[];
}

// ---------------------------------------------------------------------------
// Risk Engine
// ---------------------------------------------------------------------------

export interface RiskAssessment {
  level: RiskLevel;
  reasons: string[];
  immediate_safety_required: boolean;
  prohibited_actions: string[];
  escalation_recommended: boolean;
}

// ---------------------------------------------------------------------------
// Legal Knowledge Service
// ---------------------------------------------------------------------------

export type VerificationStatus =
  | "draft"
  | "pending_review"
  | "verified"
  | "outdated"
  | "superseded"
  | "rejected";

/** Normalized legal evidence object handed from LegalKnowledgeService to the AI layer. */
export interface LegalEvidence {
  source_id: string;
  title: string;
  source_type: string;
  issuing_authority: string;
  jurisdiction: string;
  effective_date: string | null;
  verification_status: VerificationStatus;
  section_reference: string | null;
  content: string;
  official_url: string | null;
  last_verified_at: string | null;
}

export interface LegalKnowledgeQuery {
  domain: LegalDomain;
  jurisdiction: string;
  scenario?: string;
  authority?: string;
  effectiveDateOnOrBefore?: string;
  sourceType?: string;
  language?: string;
  sectionReference?: string;
  keywords?: string[];
}

// ---------------------------------------------------------------------------
// Authority Service
// ---------------------------------------------------------------------------

export interface AuthorityInfo {
  authority_id: string;
  name: string;
  domain: LegalDomain;
  jurisdiction: string;
  official_url: string | null;
  /** Only ever populated from a verified source — never invented. */
  verified_contact: string | null;
  escalation_path: string | null;
  source_id: string | null;
}

export interface AuthorityQuery {
  domain: LegalDomain;
  jurisdiction: string;
  scenario?: string;
}

// ---------------------------------------------------------------------------
// Action Planner
// ---------------------------------------------------------------------------

export interface ActionStep {
  step: string;
  /** source_id(s) this step is grounded in; empty means it's a safety/procedural step not itself a legal claim. */
  grounded_in: string[];
}

export interface ActionPlan {
  steps: ActionStep[];
  avoid: string[];
  preserve: string[];
}

// ---------------------------------------------------------------------------
// Response Generator / final API shape (10_API.md)
// ---------------------------------------------------------------------------

export interface LegalClaim {
  claim: string;
  source_id: string | null;
  section_reference: string | null;
}

export interface CitationRef {
  source_id: string;
  title: string;
  section_reference: string | null;
  official_url: string | null;
  jurisdiction: string;
  effective_date: string | null;
}

export interface FinalResponse {
  summary: string;
  situation: string;
  what_may_apply: string[];
  rights: string[];
  obligations: string[];
  authority_powers: string[];
  actions: ActionStep[];
  avoid: string[];
  preserve: string[];
  /** Legal claims as generated, before citation validation strips unsupported ones. */
  legal_basis: LegalClaim[];
  escalation: string[];
  citations: CitationRef[];
  needs_follow_up: boolean;
  next_question: string | null;
}

// ---------------------------------------------------------------------------
// Citation Validator
// ---------------------------------------------------------------------------

export type CitationSupport = "SUPPORTED" | "PARTIALLY_SUPPORTED" | "UNSUPPORTED";

export interface CitationCheckResult {
  claim: string;
  source_id: string | null;
  section_reference: string | null;
  jurisdiction: string | null;
  effective_date: string | null;
  verification_status: VerificationStatus | null;
  support: CitationSupport;
}

export interface CitationValidationResult {
  response: FinalResponse;
  checks: CitationCheckResult[];
  /** True if any claim had to be removed/qualified. */
  wasModified: boolean;
}

// ---------------------------------------------------------------------------
// Safety Validator
// ---------------------------------------------------------------------------

export type SafetyFlag =
  | "FABRICATED_CERTAINTY"
  | "EVASION_ADVICE"
  | "CONFRONTATION_ADVICE"
  | "OBSTRUCTION_ADVICE"
  | "DANGEROUS_INSTRUCTIONS"
  | "CREDENTIAL_REQUEST"
  | "SURVEILLANCE_INSTRUCTIONS"
  | "UNSUPPORTED_GUARANTEE"
  | "PROMPT_INJECTION_DETECTED";

export interface SafetyCheckResult {
  passed: boolean;
  flags: SafetyFlag[];
  /** Present when passed=false — a safe response to return instead. */
  safeFallback: FinalResponse | null;
}

// ---------------------------------------------------------------------------
// Conversation API (10_API.md) — request/response shapes the route deals with
// ---------------------------------------------------------------------------

export interface ConversationMessageRequest {
  conversation_id?: string;
  input_type: InputType;
  message: string;
  language?: string;
  /**
   * The caller round-trips these from the previous turn's response so the
   * orchestrator can avoid re-asking established facts. There is no
   * conversations/messages table yet (see backend/README.md) — this
   * endpoint is stateless per call until that persistence layer exists.
   */
  previous_context?: ExtractedContext | null;
  previous_domain?: LegalDomain;
  previous_scenario?: string;
}

export interface ConversationMessageResponse {
  conversation_id: string;
  message_id: string;
  response: {
    summary: string;
    situation: string;
    rights: string[];
    obligations: string[];
    authority_powers: string[];
    actions: ActionStep[];
    avoid: string[];
    preserve: string[];
    escalation: string[];
    citations: CitationRef[];
  };
  risk: {
    level: RiskLevel;
    reason: string;
  };
  needs_follow_up: boolean;
  next_question: string | null;
  /** Echoed back so the client can pass it into the next turn's previous_context/previous_domain/previous_scenario. */
  context: ExtractedContext;
  domain: LegalDomain | "UNKNOWN";
  scenario: string;
}

// AI Orchestrator (06_AI_ARCHITECTURE.md, 15_DECISIONS.md D009). This is the
// one place that wires the logical pipeline together:
//
//   Input Normalizer -> Situation Classifier -> Context Extractor ->
//   Risk Engine -> Legal Knowledge Service -> Authority Service ->
//   Action Planner -> Response Generator -> Citation Validator ->
//   Safety Validator -> Response
//
// This is a modular monolith, not a multi-agent system: every stage below
// is a plain function/class call, not an autonomous agent with its own
// control loop. AIOrchestrator itself never touches Supabase/pgvector
// directly -- all legal retrieval goes through LegalKnowledgeService.
import { AIProvider } from "../ai/AIProvider";
import { normalizeInput } from "../normalization/InputNormalizer";
import { classifySituation } from "../classification/SituationClassifier";
import { extractContext } from "../context/ContextExtractor";
import { assessRisk } from "../risk/RiskEngine";
import { LegalKnowledgeService } from "../legal/LegalKnowledgeService";
import { AuthorityService } from "../authority/AuthorityService";
import { planActions } from "../planning/ActionPlanner";
import { generateResponse } from "../response/ResponseGenerator";
import { validateCitations } from "../validation/CitationValidator";
import { validateSafety, sanitizeUntrustedText } from "../validation/SafetyValidator";
import { logStage, timedStage } from "../logging/logger";
import { AppError, toAppError } from "../errors/AppError";
import {
  ConversationMessageRequest,
  ConversationMessageResponse,
  ExtractedContext,
  FinalResponse,
  LegalDomain,
  LegalEvidence,
} from "../schemas/types";

const MUMBAI_JURISDICTION = "Mumbai Metropolitan Region, Maharashtra";

export interface OrchestratorDeps {
  aiProvider: AIProvider;
  legalKnowledgeService: LegalKnowledgeService;
  authorityService: AuthorityService;
}

export class AIOrchestrator {
  constructor(private readonly deps: OrchestratorDeps) {}

  async processConversation(
    request: ConversationMessageRequest,
    requestId: string
  ): Promise<ConversationMessageResponse> {
    const endpoint = "/conversation/message";

    const normalized = normalizeInput(request.message, request.input_type, request.language ?? "en");
    const { sanitized: safeMessage, injectionDetected: inputInjection } = sanitizeUntrustedText(normalized.text);
    if (inputInjection) {
      logStage({ requestId, endpoint, stage: "input_sanitize", status: "ok", extra: { injectionDetected: true } });
    }

    const classification = await timedStage(requestId, endpoint, "situation_classifier", () =>
      classifySituation(this.deps.aiProvider, {
        message: safeMessage,
        priorDomain: request.previous_domain,
        priorScenario: request.previous_scenario,
      })
    );
    logStage({
      requestId,
      endpoint,
      stage: "situation_classifier",
      status: "ok",
      extra: { domain: classification.domain, confidence: classification.confidence },
    });

    const domainForExtraction: LegalDomain | "UNKNOWN" = classification.domain;
    const context = await timedStage(requestId, endpoint, "context_extractor", () =>
      extractContext(this.deps.aiProvider, {
        message: safeMessage,
        domain: domainForExtraction,
        scenario: classification.scenario,
        previous: request.previous_context ?? null,
      })
    );

    const risk = await timedStage(requestId, endpoint, "risk_engine", async () =>
      assessRisk(safeMessage, context)
    );
    logStage({ requestId, endpoint, stage: "risk_engine", status: "ok", extra: { level: risk.level } });

    const jurisdictionSupported = classification.jurisdiction !== "UNSUPPORTED";
    const domainKnown = classification.domain !== "UNKNOWN";

    let evidence: LegalEvidence[] = [];
    let authorities: Awaited<ReturnType<AuthorityService["retrieve"]>> = [];

    if (domainKnown && jurisdictionSupported && !classification.clarification_required) {
      const domain = classification.domain as LegalDomain;
      evidence = await timedStage(requestId, endpoint, "legal_knowledge_service", async () => {
        try {
          return await this.deps.legalKnowledgeService.retrieve({
            domain,
            jurisdiction: MUMBAI_JURISDICTION,
            scenario: classification.scenario,
          });
        } catch (err) {
          throw toAppError(err, "LEGAL_SOURCE_UNAVAILABLE");
        }
      });
      // Retrieved content is untrusted data -- neutralize any embedded
      // instruction-like text before it reaches a prompt (see 09_SECURITY.md
      // "Prompt injection").
      evidence = evidence.map((e) => ({ ...e, content: sanitizeUntrustedText(e.content).sanitized }));

      authorities = await timedStage(requestId, endpoint, "authority_service", () =>
        this.deps.authorityService.retrieve({ domain, jurisdiction: MUMBAI_JURISDICTION, scenario: classification.scenario })
      );
    }

    const nextQuestion = deriveNextQuestion(classification, context);

    let finalResponse: FinalResponse;
    if (classification.clarification_required || !domainKnown || !jurisdictionSupported) {
      finalResponse = buildClarificationResponse(classification, context, risk, nextQuestion);
    } else {
      const domain = classification.domain as LegalDomain;
      const actionPlan = await timedStage(requestId, endpoint, "action_planner", () =>
        planActions(this.deps.aiProvider, {
          domain,
          scenario: classification.scenario,
          context,
          risk,
          evidence,
          authorities,
        })
      );

      finalResponse = await timedStage(requestId, endpoint, "response_generator", () =>
        generateResponse(this.deps.aiProvider, {
          domain,
          scenario: classification.scenario,
          context,
          risk,
          evidence,
          authorities,
          actionPlan,
          nextQuestion,
        })
      );
    }

    const citationResult = await timedStage(requestId, endpoint, "citation_validator", async () =>
      validateCitations(finalResponse, evidence)
    );
    if (citationResult.wasModified) {
      logStage({ requestId, endpoint, stage: "citation_validator", status: "ok", extra: { modified: true } });
    }

    const safetyResult = await timedStage(requestId, endpoint, "safety_validator", async () =>
      validateSafety(citationResult.response)
    );
    if (!safetyResult.passed) {
      logStage({
        requestId,
        endpoint,
        stage: "safety_validator",
        status: "ok",
        extra: { blocked: true, flags: safetyResult.flags },
      });
    }

    const finalized = safetyResult.passed ? citationResult.response : safetyResult.safeFallback!;

    return {
      conversation_id: request.conversation_id ?? crypto.randomUUID(),
      message_id: crypto.randomUUID(),
      response: {
        summary: finalized.summary,
        situation: finalized.situation,
        rights: finalized.rights,
        obligations: finalized.obligations,
        authority_powers: finalized.authority_powers,
        actions: finalized.actions,
        avoid: finalized.avoid,
        preserve: finalized.preserve,
        escalation: finalized.escalation,
        citations: finalized.citations,
      },
      risk: { level: risk.level, reason: risk.reasons[0] ?? "No specific risk indicators detected." },
      needs_follow_up: finalized.needs_follow_up,
      next_question: finalized.next_question,
      context,
      domain: classification.domain,
      scenario: classification.scenario,
    };
  }
}

function deriveNextQuestion(
  classification: { clarification_required: boolean; clarification_question?: string },
  context: ExtractedContext
): string | null {
  if (classification.clarification_required && classification.clarification_question) {
    return classification.clarification_question;
  }
  if (context.missing_material_facts.length > 0) {
    return context.missing_material_facts[0];
  }
  return null;
}

function buildClarificationResponse(
  classification: { domain: LegalDomain | "UNKNOWN"; jurisdiction: string; clarification_question?: string },
  context: ExtractedContext,
  risk: { level: string; reasons: string[]; prohibited_actions: string[] },
  nextQuestion: string | null
): FinalResponse {
  const jurisdictionUnsupported = classification.jurisdiction === "UNSUPPORTED";

  return {
    summary: jurisdictionUnsupported
      ? "This app currently covers Mumbai Metropolitan Region / Maharashtra situations. I can only give general information outside that area, and you should verify anything against a local official source."
      : "I need a bit more detail before I can give reliable guidance here.",
    situation: context.what_happened ?? "Not enough information has been shared yet.",
    what_may_apply: [],
    rights: [],
    obligations: [],
    authority_powers: [],
    actions: [],
    avoid: risk.prohibited_actions,
    preserve: [],
    legal_basis: [],
    escalation: [],
    citations: [],
    needs_follow_up: nextQuestion !== null,
    next_question: nextQuestion,
  };
}

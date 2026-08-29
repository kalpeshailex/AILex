// Situation Classifier (06_AI_ARCHITECTURE.md, 15_DECISIONS.md D007). Uses
// the AIProvider's structured-output mode -- never free-text parsing -- and
// then applies a deterministic guard so the LLM cannot silently invent a
// domain outside the five V1 domains or skip clarification when jurisdiction
// is genuinely uncertain. The LLM proposes; this function is the final say
// on whether the result is trustworthy enough to proceed.
import { AIProvider, JSONSchema } from "../ai/AIProvider";
import { LEGAL_DOMAINS, LegalDomain, SituationClassification } from "../schemas/types";
import { AppError } from "../errors/AppError";

const SUPPORTED_JURISDICTION_HINT =
  "Mumbai Metropolitan Region (MMR): Mumbai City, Mumbai Suburban, Thane, Navi Mumbai, other Maharashtra locations only where clearly applicable.";

const CLASSIFICATION_SCHEMA: JSONSchema = {
  type: "object",
  properties: {
    domain: { type: "string", enum: [...LEGAL_DOMAINS, "UNKNOWN"] },
    scenario: { type: "string", description: "Short scenario label, e.g. HELMET, ARREST, UPI_FRAUD" },
    jurisdiction: { type: "string" },
    confidence: { type: "number" },
    clarification_required: { type: "boolean" },
    clarification_question: { type: "string", nullable: true },
  },
  required: ["domain", "scenario", "jurisdiction", "confidence", "clarification_required"],
};

const SYSTEM_PROMPT = `You classify a citizen's message about a real-world situation in Mumbai/Maharashtra, India, into one of exactly five legal domains: POLICE, TRAFFIC, RAILWAY, GOVERNMENT_RTS, CYBER.

Rules:
- Use "UNKNOWN" for domain if none of the five domains clearly apply, or the message is too vague to classify.
- Only claim jurisdiction is "${SUPPORTED_JURISDICTION_HINT}" style Mumbai/Maharashtra coverage if the message gives no reason to think otherwise. If the user states a location clearly outside Maharashtra, set jurisdiction to "UNSUPPORTED".
- Set clarification_required=true whenever the domain, scenario, or jurisdiction is uncertain in a way that would change the legal answer. Provide one short, specific clarification_question in that case.
- confidence is your own calibrated 0..1 confidence in this classification.
- Do not invent facts not present in the message. Do not answer the user's legal question here -- only classify.
- Output must be valid JSON matching the given schema, nothing else.`;

export interface ClassifyInput {
  message: string;
  /** Prior turns' established domain/scenario, if any, so context isn't lost mid-conversation. */
  priorDomain?: LegalDomain;
  priorScenario?: string;
}

export async function classifySituation(
  aiProvider: AIProvider,
  input: ClassifyInput
): Promise<SituationClassification> {
  const priorContextLine = input.priorDomain
    ? `Established so far in this conversation: domain=${input.priorDomain}${
        input.priorScenario ? `, scenario=${input.priorScenario}` : ""
      }. Only change domain/scenario if the new message clearly indicates something different.`
    : "No prior context established yet.";

  const prompt = `${priorContextLine}\n\nUser message:\n"""\n${input.message}\n"""`;

  const result = await aiProvider.generateStructuredOutput<SituationClassification>({
    system: SYSTEM_PROMPT,
    prompt,
    schema: CLASSIFICATION_SCHEMA,
    temperature: 0.1,
  });

  return applyDeterministicGuards(result);
}

/** Never trust the LLM's classification blindly for the two things that matter most: an allowed domain, and a sane confidence range. */
function applyDeterministicGuards(result: SituationClassification): SituationClassification {
  const domain: LegalDomain | "UNKNOWN" = LEGAL_DOMAINS.includes(result.domain as LegalDomain)
    ? (result.domain as LegalDomain)
    : "UNKNOWN";

  const confidence = Number.isFinite(result.confidence) ? Math.min(1, Math.max(0, result.confidence)) : 0;

  const clarificationRequired =
    result.clarification_required || domain === "UNKNOWN" || result.jurisdiction === "UNSUPPORTED" || confidence < 0.5;

  if (clarificationRequired && !result.clarification_question) {
    return {
      ...result,
      domain,
      confidence,
      clarification_required: true,
      clarification_question:
        domain === "UNKNOWN"
          ? "Could you tell me a bit more about what happened -- for example, was this with police, traffic, the railway, a government office, or something online/financial?"
          : "Could you share a bit more detail so I can point you to the right guidance?",
    };
  }

  return { ...result, domain, confidence, clarification_required: clarificationRequired };
}

export function assertKnownDomain(domain: LegalDomain | "UNKNOWN"): asserts domain is LegalDomain {
  if (domain === "UNKNOWN" || !LEGAL_DOMAINS.includes(domain)) {
    throw new AppError("CLASSIFICATION_UNCERTAIN", "Could not confidently classify this situation.");
  }
}

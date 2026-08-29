// Context Extractor (06_AI_ARCHITECTURE.md, task's "Context Extractor" and
// "Conversation Context" sections). Extracts only what the user actually
// said via structured AI output, then deterministically merges it with
// whatever was already established in prior turns -- the merge itself is
// plain code, not another LLM call, so "don't re-ask known facts" is a hard
// guarantee rather than a hope.
import { AIProvider, JSONSchema } from "../ai/AIProvider";
import { ExtractedContext, KnownFact } from "../schemas/types";

const CONTEXT_SCHEMA: JSONSchema = {
  type: "object",
  properties: {
    what_happened: { type: "string", nullable: true },
    when: { type: "string", nullable: true },
    where: { type: "string", nullable: true },
    authority_or_person: { type: "string", nullable: true },
    actions_taken: { type: "string", nullable: true },
    documents_mentioned: { type: "array", items: { type: "string" } },
    immediate_risk: { type: "string", nullable: true },
    user_objective: { type: "string", nullable: true },
    missing_material_facts: {
      type: "array",
      items: { type: "string" },
      description: "Facts not yet known that would materially change the legal path/advice.",
    },
  },
  required: ["documents_mentioned", "missing_material_facts"],
};

const SYSTEM_PROMPT = `You extract structured facts from a citizen's message about a legal/civic situation in Mumbai, India. You are NOT answering their question -- only extracting what they said.

Critical rules:
- Only include a field's value if the user actually stated it or it is a direct, unambiguous restatement of what they said. Use null for anything not stated.
- Never infer legal conclusions (e.g. never write "illegally", "wrongly", "without cause" unless the user used that word themselves).
- Never invent dates, locations, names, or amounts.
- missing_material_facts should list only facts that would change what advice applies -- not everything conceivably unknown. Prefer few, specific items (e.g. "Whether a challan/notice was actually issued" rather than "more details").
- Output must be valid JSON matching the given schema, nothing else.`;

export interface ExtractInput {
  message: string;
  domain: string;
  scenario: string;
  previous?: ExtractedContext | null;
}

/** Raw shape the AIProvider returns -- known_facts is derived deterministically afterward, not asked of the model directly, to avoid the model inventing its own notion of what counts as "known". */
type RawExtraction = Omit<ExtractedContext, "known_facts">;

export async function extractContext(aiProvider: AIProvider, input: ExtractInput): Promise<ExtractedContext> {
  const priorSummary = input.previous
    ? summarizePrevious(input.previous)
    : "No prior context in this conversation.";

  const prompt = `Domain: ${input.domain}\nScenario: ${input.scenario}\n\nPrior context:\n${priorSummary}\n\nLatest user message:\n"""\n${input.message}\n"""`;

  const raw = await aiProvider.generateStructuredOutput<RawExtraction>({
    system: SYSTEM_PROMPT,
    prompt,
    schema: CONTEXT_SCHEMA,
    temperature: 0.1,
  });

  return mergeContext(input.previous ?? null, raw);
}

function summarizePrevious(ctx: ExtractedContext): string {
  const lines = ctx.known_facts.map((f) => `- ${f.field}: ${f.value}`);
  return lines.length > 0 ? lines.join("\n") : "None established yet.";
}

/**
 * Deterministic merge: a newly-stated field overwrites the prior value; a
 * field the model left null this turn keeps whatever was already known. This
 * is what actually prevents re-asking established facts -- not model memory.
 */
export function mergeContext(previous: ExtractedContext | null, latest: RawExtraction): ExtractedContext {
  const merged: RawExtraction = {
    what_happened: latest.what_happened ?? previous?.what_happened ?? null,
    when: latest.when ?? previous?.when ?? null,
    where: latest.where ?? previous?.where ?? null,
    authority_or_person: latest.authority_or_person ?? previous?.authority_or_person ?? null,
    actions_taken: latest.actions_taken ?? previous?.actions_taken ?? null,
    documents_mentioned: dedupe([...(previous?.documents_mentioned ?? []), ...latest.documents_mentioned]),
    immediate_risk: latest.immediate_risk ?? previous?.immediate_risk ?? null,
    user_objective: latest.user_objective ?? previous?.user_objective ?? null,
    missing_material_facts: latest.missing_material_facts,
  };

  const known_facts: KnownFact[] = [];
  const fieldNames: (keyof RawExtraction)[] = [
    "what_happened",
    "when",
    "where",
    "authority_or_person",
    "actions_taken",
    "immediate_risk",
    "user_objective",
  ];
  for (const field of fieldNames) {
    const value = merged[field];
    if (typeof value === "string" && value.length > 0) {
      const isNew = latest[field] === value;
      known_facts.push({ field, value, source: isNew ? "user_stated" : "carried_forward" });
    }
  }
  for (const doc of merged.documents_mentioned) {
    known_facts.push({ field: "document_mentioned", value: doc, source: "user_stated" });
  }

  // The model is given the full known-facts summary as prior context (see
  // summarizePrevious), so it is expected to already exclude established
  // facts from missing_material_facts each turn -- deduping here just
  // guards against the model repeating itself verbatim across turns.
  const missing_material_facts = dedupe(latest.missing_material_facts);

  return { ...merged, missing_material_facts, known_facts };
}

function dedupe(items: string[]): string[] {
  return Array.from(new Set(items.map((i) => i.trim()).filter(Boolean)));
}

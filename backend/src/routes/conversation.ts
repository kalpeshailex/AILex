// Conversation route (10_API.md POST /conversation/message). Follows the
// same unprefixed-path, requireUser-gated convention as the existing
// incidents/profile/notifications routes (see backend/README.md) rather
// than introducing a new /api/v1 prefix unilaterally -- see the AI-task
// final report's "API contract" note for the conflict this resolves.
//
// LegalKnowledgeService/AuthorityService are backed by the real knowledge
// base seeded from Mumbai_Legal_KB_All_Domains_Populated.xlsx (see
// backend/legal_kb_schema.sql / legal_kb_seed.sql) via the caller's own
// request-scoped Supabase client -- same RLS-as-the-user pattern as every
// other route. A domain/scenario the corpus doesn't cover yet still
// legitimately falls through to the honest "insufficient verified
// information" response -- that's expected, not a bug.
import { Hono } from "hono";
import { Env } from "../lib/supabase";
import { AppVariables, requireUser } from "../middleware/auth";
import { getAIConfig } from "../config/env";
import { GeminiAIProvider } from "../ai/GeminiAIProvider";
import { AIOrchestrator } from "../orchestrator/AIOrchestrator";
import { SupabaseLegalKnowledgeService } from "../legal/SupabaseLegalKnowledgeService";
import { SupabaseAuthorityService } from "../authority/SupabaseAuthorityService";
import { AppError, toAppError } from "../errors/AppError";
import { ConversationMessageRequest } from "../schemas/types";
import { logStage } from "../logging/logger";

export const conversation = new Hono<{ Bindings: Env; Variables: AppVariables }>();

conversation.use("*", requireUser);

conversation.post("/message", async (c) => {
  const requestId = crypto.randomUUID();
  const endpoint = "/conversation/message";

  let body: ConversationMessageRequest;
  try {
    body = await c.req.json();
  } catch {
    return c.json(new AppError("VALIDATION_FAILED", "Request body must be valid JSON.").toClientBody(), 400);
  }

  if (!body || typeof body.message !== "string" || body.message.trim().length === 0) {
    return c.json(new AppError("VALIDATION_FAILED", "`message` is required.").toClientBody(), 400);
  }
  if (body.input_type !== "text" && body.input_type !== "stt") {
    return c.json(new AppError("VALIDATION_FAILED", "`input_type` must be 'text' or 'stt'.").toClientBody(), 400);
  }

  try {
    const aiConfig = getAIConfig(c.env);
    const supabase = c.get("supabase");
    const orchestrator = new AIOrchestrator({
      aiProvider: new GeminiAIProvider(aiConfig.geminiApiKey, aiConfig.geminiModel),
      legalKnowledgeService: new SupabaseLegalKnowledgeService(supabase),
      authorityService: new SupabaseAuthorityService(supabase),
    });

    const result = await orchestrator.processConversation(body, requestId);
    return c.json(result, 200);
  } catch (err) {
    const appError = toAppError(err);
    logStage({
      requestId,
      endpoint,
      stage: "route_handler",
      status: "error",
      errorClass: appError.category,
    });
    return c.json(appError.toClientBody(), appError.httpStatus as any);
  }
});

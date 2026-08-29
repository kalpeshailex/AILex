// Conversation route (10_API.md POST /conversation/message). Follows the
// same unprefixed-path, requireUser-gated convention as the existing
// incidents/profile/notifications routes (see backend/README.md) rather
// than introducing a new /api/v1 prefix unilaterally -- see the AI-task
// final report's "API contract" note for the conflict this resolves.
//
// LegalKnowledgeService/AuthorityService are still mock implementations
// (see src/legal, src/authority) -- there is no real legal corpus or
// pgvector retrieval wired up yet, so most real conversations will
// currently get the honest "insufficient verified information" fallback.
// That is expected, not a bug.
import { Hono } from "hono";
import { Env } from "../lib/supabase";
import { AppVariables, requireUser } from "../middleware/auth";
import { getAIConfig } from "../config/env";
import { GeminiAIProvider } from "../ai/GeminiAIProvider";
import { AIOrchestrator } from "../orchestrator/AIOrchestrator";
import { MockLegalKnowledgeService } from "../legal/LegalKnowledgeService";
import { MockAuthorityService } from "../authority/AuthorityService";
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
    const orchestrator = new AIOrchestrator({
      aiProvider: new GeminiAIProvider(aiConfig.geminiApiKey, aiConfig.geminiModel),
      // No real legal/authority data source yet (see module comments) --
      // this deliberately ships with zero canned "legal" content.
      legalKnowledgeService: new MockLegalKnowledgeService([]),
      authorityService: new MockAuthorityService([]),
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

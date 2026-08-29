// Central place that reads AI-related config out of the Worker's Env
// bindings, so model names / provider choice are never hard-coded through
// business logic (see the task's "Model Configuration" requirement).
import { Env } from "../lib/supabase";
import { AppError } from "../errors/AppError";

export interface AIConfig {
  provider: "gemini";
  geminiApiKey: string;
  geminiModel: string;
  geminiEmbeddingModel: string;
}

// Verified live against the Gemini API on 2026-08-29 (this project's models
// change faster than expected -- gemini-2.0-flash and text-embedding-004
// were already retired by then). gemini-3.5-flash-lite responds in ~1s with
// no extended-thinking overhead, which matters here since one conversation
// turn makes up to 4 sequential structured-output calls. Re-verify before
// assuming either name is still current.
const DEFAULT_GEMINI_MODEL = "gemini-3.5-flash-lite";
const DEFAULT_GEMINI_EMBEDDING_MODEL = "gemini-embedding-001";

/**
 * Reads and validates AI config from Env. Throws AI_PROVIDER_UNAVAILABLE
 * (not a raw error) if GEMINI_API_KEY is missing, since every AI-pipeline
 * request needs it — callers should catch this at the route boundary the
 * same way they handle any other AppError.
 */
export function getAIConfig(env: Env): AIConfig {
  const provider = (env.AI_PROVIDER ?? "gemini").toLowerCase();
  if (provider !== "gemini") {
    // Only Gemini is implemented today (see AIProvider abstraction) — this
    // guards against a misconfigured AI_PROVIDER value silently no-op'ing.
    throw new AppError(
      "AI_PROVIDER_UNAVAILABLE",
      "The configured AI provider is not supported by this deployment."
    );
  }
  if (!env.GEMINI_API_KEY) {
    throw new AppError(
      "AI_PROVIDER_UNAVAILABLE",
      "The AI service is not configured. Please try again later."
    );
  }
  return {
    provider: "gemini",
    geminiApiKey: env.GEMINI_API_KEY,
    geminiModel: env.GEMINI_MODEL ?? DEFAULT_GEMINI_MODEL,
    geminiEmbeddingModel: env.GEMINI_EMBEDDING_MODEL ?? DEFAULT_GEMINI_EMBEDDING_MODEL,
  };
}

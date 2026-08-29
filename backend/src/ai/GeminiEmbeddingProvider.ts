// First EmbeddingProvider implementation, using Gemini's embedContent API.
// Not yet wired into a real LegalKnowledgeService (there is no pgvector
// retrieval implementation yet — see backend/README.md and 07_RAG.md) but
// provided now so that work can plug straight in without inventing a new
// provider abstraction later.
import { EmbeddingProvider } from "./EmbeddingProvider";
import { AppError } from "../errors/AppError";

const GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models";
const DEFAULT_TIMEOUT_MS = 15_000;

export class GeminiEmbeddingProvider implements EmbeddingProvider {
  constructor(private readonly apiKey: string, private readonly model: string) {}

  async embed(text: string): Promise<number[]> {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), DEFAULT_TIMEOUT_MS);
    let response: Response;
    try {
      response = await fetch(`${GEMINI_API_BASE}/${this.model}:embedContent`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "x-goog-api-key": this.apiKey,
        },
        body: JSON.stringify({ content: { parts: [{ text }] } }),
        signal: controller.signal,
      });
    } catch (err) {
      if (err instanceof Error && err.name === "AbortError") {
        throw new AppError("AI_PROVIDER_TIMEOUT", "The embedding service took too long to respond.");
      }
      throw new AppError("AI_PROVIDER_UNAVAILABLE", "The embedding service could not be reached.");
    } finally {
      clearTimeout(timeout);
    }

    if (response.status === 429) {
      throw new AppError("RATE_LIMITED", "Too many requests. Please try again shortly.");
    }
    if (!response.ok) {
      throw new AppError("AI_PROVIDER_UNAVAILABLE", "The embedding service returned an error.");
    }

    const json: any = await response.json();
    const values = json?.embedding?.values;
    if (!Array.isArray(values)) {
      throw new AppError("AI_INVALID_RESPONSE", "The embedding service returned an unexpected response.");
    }
    return values as number[];
  }
}

// First (and currently only) AIProvider implementation. Talks to the Gemini
// API's generateContent endpoint over plain fetch — no SDK dependency, same
// minimal-dependency approach the Android app uses for its own network
// calls. The API key is read once at construction from server-side config
// (see src/config/env.ts) and is sent only via the x-goog-api-key header —
// never in a URL (so it can't end up in a logged request line) and never
// echoed into any error message this class throws.
import { AIProvider, GenerateStructuredOptions, GenerateTextOptions } from "./AIProvider";
import { AppError } from "../errors/AppError";

const GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models";
const DEFAULT_TIMEOUT_MS = 20_000;

export class GeminiAIProvider implements AIProvider {
  constructor(private readonly apiKey: string, private readonly model: string) {}

  async generateText(options: GenerateTextOptions): Promise<string> {
    const body = {
      contents: [{ role: "user", parts: [{ text: options.prompt }] }],
      ...(options.system ? { systemInstruction: { role: "system", parts: [{ text: options.system }] } } : {}),
      generationConfig: {
        temperature: options.temperature ?? 0.2,
        maxOutputTokens: options.maxOutputTokens ?? 1024,
      },
    };
    const json = await this.call(body);
    return this.extractText(json);
  }

  async generateStructuredOutput<T>(options: GenerateStructuredOptions): Promise<T> {
    const body = {
      contents: [{ role: "user", parts: [{ text: options.prompt }] }],
      ...(options.system ? { systemInstruction: { role: "system", parts: [{ text: options.system }] } } : {}),
      generationConfig: {
        temperature: options.temperature ?? 0.1,
        responseMimeType: "application/json",
        responseSchema: options.schema,
      },
    };
    const json = await this.call(body);
    const text = this.extractText(json);
    try {
      return JSON.parse(text) as T;
    } catch {
      throw new AppError("AI_INVALID_RESPONSE", "The AI service returned a response that could not be parsed.");
    }
  }

  private async call(body: unknown): Promise<any> {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), DEFAULT_TIMEOUT_MS);
    let response: Response;
    try {
      response = await fetch(`${GEMINI_API_BASE}/${this.model}:generateContent`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "x-goog-api-key": this.apiKey,
        },
        body: JSON.stringify(body),
        signal: controller.signal,
      });
    } catch (err) {
      if (err instanceof Error && err.name === "AbortError") {
        throw new AppError("AI_PROVIDER_TIMEOUT", "The AI service took too long to respond.");
      }
      throw new AppError("AI_PROVIDER_UNAVAILABLE", "The AI service could not be reached.");
    } finally {
      clearTimeout(timeout);
    }

    if (response.status === 429) {
      throw new AppError("RATE_LIMITED", "Too many requests. Please try again shortly.");
    }
    if (!response.ok) {
      // Never surface the response body (may contain provider-internal
      // detail) — log it server-side only, at the call site, if needed.
      throw new AppError("AI_PROVIDER_UNAVAILABLE", "The AI service returned an error.");
    }
    try {
      return await response.json();
    } catch {
      throw new AppError("AI_INVALID_RESPONSE", "The AI service returned a response that could not be parsed.");
    }
  }

  private extractText(json: any): string {
    const text = json?.candidates?.[0]?.content?.parts?.[0]?.text;
    if (typeof text !== "string") {
      throw new AppError("AI_INVALID_RESPONSE", "The AI service returned an empty response.");
    }
    return text;
  }
}

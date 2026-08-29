import { afterEach, describe, expect, it, vi } from "vitest";
import { GeminiAIProvider } from "../src/ai/GeminiAIProvider";
import { AppError } from "../src/errors/AppError";

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), { status, headers: { "Content-Type": "application/json" } });
}

describe("GeminiAIProvider", () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it("sends the API key only via the x-goog-api-key header, never in the URL", async () => {
    const fetchMock = vi.spyOn(globalThis, "fetch").mockResolvedValue(
      jsonResponse({ candidates: [{ content: { parts: [{ text: "hello" }] } }] })
    );

    const provider = new GeminiAIProvider("super-secret-key", "gemini-2.0-flash");
    await provider.generateText({ prompt: "hi" });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, init] = fetchMock.mock.calls[0];
    expect(String(url)).not.toContain("super-secret-key");
    expect((init?.headers as Record<string, string>)["x-goog-api-key"]).toBe("super-secret-key");
  });

  it("generateText returns the model's text", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      jsonResponse({ candidates: [{ content: { parts: [{ text: "You may want to verify this." }] } }] })
    );
    const provider = new GeminiAIProvider("key", "gemini-2.0-flash");
    const text = await provider.generateText({ prompt: "hi" });
    expect(text).toBe("You may want to verify this.");
  });

  it("generateStructuredOutput parses the model's JSON text", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      jsonResponse({ candidates: [{ content: { parts: [{ text: '{"domain":"TRAFFIC"}' }] } }] })
    );
    const provider = new GeminiAIProvider("key", "gemini-2.0-flash");
    const result = await provider.generateStructuredOutput<{ domain: string }>({
      prompt: "classify",
      schema: { type: "object", properties: { domain: { type: "string" } } },
    });
    expect(result.domain).toBe("TRAFFIC");
  });

  it("throws AI_INVALID_RESPONSE when the model's text is not valid JSON", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(
      jsonResponse({ candidates: [{ content: { parts: [{ text: "not json" }] } }] })
    );
    const provider = new GeminiAIProvider("key", "gemini-2.0-flash");
    await expect(
      provider.generateStructuredOutput({ prompt: "classify", schema: { type: "object" } })
    ).rejects.toMatchObject({ category: "AI_INVALID_RESPONSE" } satisfies Partial<AppError>);
  });

  it("throws RATE_LIMITED on HTTP 429", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(jsonResponse({ error: "quota" }, 429));
    const provider = new GeminiAIProvider("key", "gemini-2.0-flash");
    await expect(provider.generateText({ prompt: "hi" })).rejects.toMatchObject({ category: "RATE_LIMITED" });
  });

  it("throws AI_PROVIDER_UNAVAILABLE on a non-ok, non-429 response", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(jsonResponse({ error: "boom" }, 500));
    const provider = new GeminiAIProvider("key", "gemini-2.0-flash");
    await expect(provider.generateText({ prompt: "hi" })).rejects.toMatchObject({ category: "AI_PROVIDER_UNAVAILABLE" });
  });

  it("throws AI_PROVIDER_UNAVAILABLE when fetch itself fails", async () => {
    vi.spyOn(globalThis, "fetch").mockRejectedValue(new Error("network down"));
    const provider = new GeminiAIProvider("key", "gemini-2.0-flash");
    await expect(provider.generateText({ prompt: "hi" })).rejects.toMatchObject({ category: "AI_PROVIDER_UNAVAILABLE" });
  });

  it("never echoes the API key into a thrown error's message", async () => {
    vi.spyOn(globalThis, "fetch").mockResolvedValue(jsonResponse({ error: "super-secret-key leaked here" }, 500));
    const provider = new GeminiAIProvider("super-secret-key", "gemini-2.0-flash");
    try {
      await provider.generateText({ prompt: "hi" });
      expect.unreachable();
    } catch (err) {
      expect((err as Error).message).not.toContain("super-secret-key");
    }
  });
});

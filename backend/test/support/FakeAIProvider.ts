// Test-only AIProvider double. Lets tests control exactly what the "model"
// returns without any network call, and lets tests spy on what prompts each
// pipeline stage actually sent it (e.g. to prove untrusted input was
// sanitized before reaching a prompt). Never used outside test/.
import { AIProvider, GenerateStructuredOptions, GenerateTextOptions } from "../../src/ai/AIProvider";

export interface RecordedCall {
  kind: "text" | "structured";
  system?: string;
  prompt: string;
}

export class FakeAIProvider implements AIProvider {
  readonly calls: RecordedCall[] = [];
  private structuredQueue: unknown[] = [];
  private textQueue: string[] = [];

  /** Queue one structured response per expected call, in order. */
  queueStructured(...responses: unknown[]): this {
    this.structuredQueue.push(...responses);
    return this;
  }

  queueText(...responses: string[]): this {
    this.textQueue.push(...responses);
    return this;
  }

  async generateText(options: GenerateTextOptions): Promise<string> {
    this.calls.push({ kind: "text", system: options.system, prompt: options.prompt });
    const next = this.textQueue.shift();
    if (next === undefined) throw new Error("FakeAIProvider.generateText called with no queued response");
    return next;
  }

  async generateStructuredOutput<T>(options: GenerateStructuredOptions): Promise<T> {
    this.calls.push({ kind: "structured", system: options.system, prompt: options.prompt });
    const next = this.structuredQueue.shift();
    if (next === undefined) throw new Error("FakeAIProvider.generateStructuredOutput called with no queued response");
    return next as T;
  }
}

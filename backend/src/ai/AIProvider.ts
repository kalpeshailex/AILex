// Provider abstraction (15_DECISIONS.md D010, 02_ARCHITECTURE.md "Provider
// abstraction"). Nothing outside backend/src/ai should import Gemini
// directly — every other module depends on this interface so a future
// provider can be swapped in without touching classification/context/
// planning/response-generation logic.

/** A minimal JSON Schema subset — enough to describe the structured outputs this app needs. */
export interface JSONSchema {
  type: "object" | "string" | "number" | "boolean" | "array" | "integer";
  description?: string;
  enum?: string[];
  properties?: Record<string, JSONSchema>;
  required?: string[];
  items?: JSONSchema;
  nullable?: boolean;
}

export interface GenerateTextOptions {
  system?: string;
  prompt: string;
  temperature?: number;
  maxOutputTokens?: number;
}

export interface GenerateStructuredOptions {
  system?: string;
  prompt: string;
  schema: JSONSchema;
  temperature?: number;
}

/**
 * generateStructuredOutput<T> is untyped at the schema level (JSONSchema
 * doesn't carry a compile-time type parameter) — callers assert the result
 * shape themselves. Every AIProvider implementation must guarantee the
 * returned value is valid JSON matching `schema`, or throw AI_INVALID_RESPONSE.
 */
export interface AIProvider {
  generateText(options: GenerateTextOptions): Promise<string>;
  generateStructuredOutput<T>(options: GenerateStructuredOptions): Promise<T>;
}

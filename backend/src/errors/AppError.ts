// Stable error categories for the AI pipeline (see 10_API.md "Errors" and
// 18_AI_BUILDER_RULES.md). Every stage should throw AppError (or let one of
// its typed subclasses propagate) rather than a raw Error, so the outer
// route handler can map it to a safe, stable HTTP response without ever
// leaking stack traces, provider internals, or secrets to the client.

export type ErrorCategory =
  | "AI_PROVIDER_UNAVAILABLE"
  | "AI_PROVIDER_TIMEOUT"
  | "AI_INVALID_RESPONSE"
  | "LEGAL_SOURCE_UNAVAILABLE"
  | "LEGAL_SUPPORT_INSUFFICIENT"
  | "CLASSIFICATION_UNCERTAIN"
  | "UNSUPPORTED_JURISDICTION"
  | "RATE_LIMITED"
  | "UNAUTHORIZED"
  | "VALIDATION_FAILED"
  | "SAFETY_BLOCKED";

const HTTP_STATUS_BY_CATEGORY: Record<ErrorCategory, number> = {
  AI_PROVIDER_UNAVAILABLE: 503,
  AI_PROVIDER_TIMEOUT: 504,
  AI_INVALID_RESPONSE: 502,
  LEGAL_SOURCE_UNAVAILABLE: 503,
  LEGAL_SUPPORT_INSUFFICIENT: 200, // not a failure — a safe, expected outcome (see 08_LEGAL_KNOWLEDGE.md)
  CLASSIFICATION_UNCERTAIN: 200, // resolved as a clarification turn, not an error to the client
  UNSUPPORTED_JURISDICTION: 200,
  RATE_LIMITED: 429,
  UNAUTHORIZED: 401,
  VALIDATION_FAILED: 400,
  SAFETY_BLOCKED: 200, // resolved as a safe fallback response, not surfaced as a hard failure
};

export class AppError extends Error {
  readonly category: ErrorCategory;
  readonly httpStatus: number;
  /** Non-sensitive extra context for logs only — never echoed to the client verbatim. */
  readonly details?: Record<string, unknown>;

  constructor(category: ErrorCategory, message: string, details?: Record<string, unknown>) {
    super(message);
    this.name = "AppError";
    this.category = category;
    this.httpStatus = HTTP_STATUS_BY_CATEGORY[category];
    this.details = details;
  }

  /** The only shape ever sent to Android — no stack, no provider/database internals. */
  toClientBody(): { error: string; category: ErrorCategory } {
    return { error: this.message, category: this.category };
  }
}

export function isAppError(err: unknown): err is AppError {
  return err instanceof AppError;
}

/** Wraps an unknown thrown value as a safe AppError, never leaking the original message to clients by default. */
export function toAppError(err: unknown, fallbackCategory: ErrorCategory = "AI_PROVIDER_UNAVAILABLE"): AppError {
  if (isAppError(err)) return err;
  return new AppError(fallbackCategory, "Something went wrong. Please try again.", {
    originalMessage: err instanceof Error ? err.message : String(err),
  });
}

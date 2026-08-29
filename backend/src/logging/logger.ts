// Safe structured logging for the AI pipeline (09_SECURITY.md, 04_BACKEND.md
// "Logging"). Every stage of AIOrchestrator should log through here so
// observability is consistent and secrets never leak into logs by accident.

const DENYLIST_KEYS = [
  "apikey",
  "api_key",
  "gemini_api_key",
  "supabase_service_role_key",
  "supabase_service_key",
  "authorization",
  "access_token",
  "refresh_token",
  "otp",
  "password",
  "pin",
  "cvv",
  "upi_pin",
];

/** Best-effort redaction for any object logged alongside a stage event — defense in depth, not the only safeguard. */
function redact(value: unknown): unknown {
  if (value === null || typeof value !== "object") return value;
  if (Array.isArray(value)) return value.map(redact);
  const out: Record<string, unknown> = {};
  for (const [key, val] of Object.entries(value as Record<string, unknown>)) {
    if (DENYLIST_KEYS.includes(key.toLowerCase())) {
      out[key] = "[redacted]";
    } else {
      out[key] = redact(val);
    }
  }
  return out;
}

export interface StageLogEvent {
  requestId: string;
  endpoint: string;
  stage: string;
  latencyMs?: number;
  status: "ok" | "error";
  errorClass?: string;
  /**
   * Non-sensitive extra fields only (e.g. domain, riskLevel, confidence).
   * Never pass raw user message text, legal conversation content, or
   * anything from the DENYLIST_KEYS list here — it is redacted defensively,
   * but the caller should not rely on that as the primary safeguard.
   */
  extra?: Record<string, unknown>;
}

export function logStage(event: StageLogEvent): void {
  const safeExtra = event.extra ? redact(event.extra) : undefined;
  console.log(
    JSON.stringify({
      ts: new Date().toISOString(),
      requestId: event.requestId,
      endpoint: event.endpoint,
      stage: event.stage,
      latencyMs: event.latencyMs,
      status: event.status,
      errorClass: event.errorClass,
      extra: safeExtra,
    })
  );
}

/** Times a pipeline stage and logs it consistently on both success and failure. */
export async function timedStage<T>(
  requestId: string,
  endpoint: string,
  stage: string,
  fn: () => Promise<T>,
  extra?: Record<string, unknown>
): Promise<T> {
  const start = Date.now();
  try {
    const result = await fn();
    logStage({ requestId, endpoint, stage, latencyMs: Date.now() - start, status: "ok", extra });
    return result;
  } catch (err) {
    logStage({
      requestId,
      endpoint,
      stage,
      latencyMs: Date.now() - start,
      status: "error",
      errorClass: err instanceof Error ? err.constructor.name : typeof err,
      extra,
    });
    throw err;
  }
}

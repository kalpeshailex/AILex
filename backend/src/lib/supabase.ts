import { createClient, SupabaseClient } from "@supabase/supabase-js";

export interface Env {
  SUPABASE_URL: string;
  SUPABASE_ANON_KEY: string;
  // AI pipeline bindings (see src/config/env.ts). GEMINI_API_KEY is a secret
  // (`wrangler secret put GEMINI_API_KEY`) — never committed, never logged,
  // never returned in a response. The rest are plain, non-secret config and
  // may live in wrangler.toml's [vars].
  GEMINI_API_KEY?: string;
  AI_PROVIDER?: string;
  GEMINI_MODEL?: string;
  GEMINI_EMBEDDING_MODEL?: string;
}

/**
 * Creates a Supabase client scoped to the calling user's own access token,
 * so every query this Worker makes runs through Postgres Row Level
 * Security as that user — never as an admin.
 *
 * There is no service-role key here on purpose: this API never needs to
 * see across users, so it never gets the ability to. If a future endpoint
 * genuinely needs elevated privileges (e.g. an admin dashboard), add a
 * separate service-role client for just that route rather than widening
 * this one.
 */
export function supabaseForRequest(env: Env, accessToken: string): SupabaseClient {
  return createClient(env.SUPABASE_URL, env.SUPABASE_ANON_KEY, {
    global: { headers: { Authorization: `Bearer ${accessToken}` } },
    auth: { persistSession: false, autoRefreshToken: false },
  });
}

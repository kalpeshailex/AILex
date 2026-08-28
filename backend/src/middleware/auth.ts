import { Context, Next } from "hono";
import { SupabaseClient } from "@supabase/supabase-js";
import { Env, supabaseForRequest } from "../lib/supabase";

export type AppVariables = {
  userId: string;
  supabase: SupabaseClient;
};

/**
 * Verifies the `Authorization: Bearer <supabase access token>` header
 * against Supabase Auth, then attaches the user id and a request-scoped
 * Supabase client (see supabaseForRequest) to the context for downstream
 * route handlers.
 */
export async function requireUser(
  c: Context<{ Bindings: Env; Variables: AppVariables }>,
  next: Next
) {
  const authHeader = c.req.header("Authorization");
  const token = authHeader?.startsWith("Bearer ") ? authHeader.slice(7) : null;
  if (!token) {
    return c.json({ error: "Missing Authorization bearer token" }, 401);
  }

  const supabase = supabaseForRequest(c.env, token);
  const { data, error } = await supabase.auth.getUser(token);
  if (error || !data.user) {
    return c.json({ error: "Invalid or expired session" }, 401);
  }

  c.set("userId", data.user.id);
  c.set("supabase", supabase);
  await next();
}

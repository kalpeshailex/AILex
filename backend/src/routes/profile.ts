import { Hono } from "hono";
import { Env } from "../lib/supabase";
import { AppVariables, requireUser } from "../middleware/auth";

export const profile = new Hono<{ Bindings: Env; Variables: AppVariables }>();

profile.use("*", requireUser);

// GET /profile — the caller's own profile row.
profile.get("/", async (c) => {
  const userId = c.get("userId");
  const supabase = c.get("supabase");
  const { data, error } = await supabase
    .from("profiles")
    .select("*")
    .eq("id", userId)
    .maybeSingle();
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data ?? { id: userId, display_name: "", language: "ENGLISH" });
});

// PATCH /profile — upsert display_name / language.
profile.patch("/", async (c) => {
  const userId = c.get("userId");
  const supabase = c.get("supabase");
  const body = await c.req.json();
  const { data, error } = await supabase
    .from("profiles")
    .upsert({ ...body, id: userId })
    .select()
    .single();
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data);
});

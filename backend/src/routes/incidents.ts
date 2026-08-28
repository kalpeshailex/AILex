import { Hono } from "hono";
import { Env } from "../lib/supabase";
import { AppVariables, requireUser } from "../middleware/auth";

export const incidents = new Hono<{ Bindings: Env; Variables: AppVariables }>();

incidents.use("*", requireUser);

// GET /incidents — every incident belonging to the calling user.
incidents.get("/", async (c) => {
  const supabase = c.get("supabase");
  const { data, error } = await supabase
    .from("incidents")
    .select("*")
    .order("saved_at", { ascending: false });
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data);
});

// POST /incidents — create one. user_id is stamped server-side, never
// trusted from the request body.
incidents.post("/", async (c) => {
  const userId = c.get("userId");
  const supabase = c.get("supabase");
  const body = await c.req.json();
  const { data, error } = await supabase
    .from("incidents")
    .insert({ ...body, user_id: userId })
    .select()
    .single();
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data, 201);
});

// PATCH /incidents/:id — partial update (notes, status, timeline, complaint
// edits, ...). RLS guarantees this can only touch the caller's own row.
incidents.patch("/:id", async (c) => {
  const supabase = c.get("supabase");
  const id = c.req.param("id");
  const body = await c.req.json();
  const { data, error } = await supabase
    .from("incidents")
    .update(body)
    .eq("id", id)
    .select()
    .single();
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data);
});

// DELETE /incidents/:id
incidents.delete("/:id", async (c) => {
  const supabase = c.get("supabase");
  const id = c.req.param("id");
  const { error } = await supabase.from("incidents").delete().eq("id", id);
  if (error) return c.json({ error: error.message }, 500);
  return c.body(null, 204);
});

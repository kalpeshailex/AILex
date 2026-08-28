import { Hono } from "hono";
import { Env } from "../lib/supabase";
import { AppVariables, requireUser } from "../middleware/auth";

export const notifications = new Hono<{ Bindings: Env; Variables: AppVariables }>();

notifications.use("*", requireUser);

// GET /notifications
notifications.get("/", async (c) => {
  const supabase = c.get("supabase");
  const { data, error } = await supabase
    .from("notifications")
    .select("*")
    .order("created_at", { ascending: false });
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data);
});

// PATCH /notifications/:id — mark read/unread.
notifications.patch("/:id", async (c) => {
  const supabase = c.get("supabase");
  const id = c.req.param("id");
  const body = await c.req.json();
  const { data, error } = await supabase
    .from("notifications")
    .update(body)
    .eq("id", id)
    .select()
    .single();
  if (error) return c.json({ error: error.message }, 500);
  return c.json(data);
});

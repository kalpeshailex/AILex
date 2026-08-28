import { Hono } from "hono";
import { Env } from "./lib/supabase";
import { incidents } from "./routes/incidents";
import { profile } from "./routes/profile";
import { notifications } from "./routes/notifications";

const app = new Hono<{ Bindings: Env }>();

// Public — no auth required. Useful to confirm the Worker is deployed and
// reachable before wiring anything else up.
app.get("/health", (c) => c.json({ ok: true }));

app.route("/incidents", incidents);
app.route("/profile", profile);
app.route("/notifications", notifications);

app.onError((err, c) => {
  console.error(err);
  return c.json({ error: "Internal error" }, 500);
});

export default app;

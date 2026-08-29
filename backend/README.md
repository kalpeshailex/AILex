# Ailex API — Cloudflare Worker

A thin backend, deployed as a Cloudflare Worker, sitting between the Ailex
Android app and Supabase. It never uses Supabase's service-role key —
every request runs through Postgres Row Level Security **as the calling
user**, using their own Supabase access token. See `src/lib/supabase.ts`
for why.

## Architecture

```
Android app
   │  (1) signs in directly against Supabase Auth (phone/OTP)
   │      → gets back a Supabase access token (JWT)
   │
   │  (2) calls this Worker's REST API, sending that token as
   │      Authorization: Bearer <token>
   ▼
Cloudflare Worker (this project)
   │  verifies the token with Supabase Auth, then queries Postgres
   │  AS that user (RLS enforces per-user isolation)
   ▼
Supabase Postgres
```

Auth itself talks to Supabase directly from the app — there's no reason to
proxy it. This Worker's job is the data API (incidents, profile,
notifications) and is the natural place to add real business logic (or a
real AI backend for Ask Legal AI) later.

## One-time setup

### 1. Install dependencies

```
cd backend
npm install
```

### 2. Log into Cloudflare

```
npx wrangler login
```

This opens a browser to authorize the CLI against your Cloudflare account.

### 3. Get your Supabase credentials

In the [Supabase dashboard](https://supabase.com/dashboard) → your project
→ **Settings → API**, copy:

- **Project URL** (`https://<project-ref>.supabase.co`)
- **anon / public key** (safe to use here — this Worker only ever acts
  with a caller's own token layered on top of it, never on its own)

You do **not** need the `service_role` key for anything in this project.

### 4. Run the database schema

Supabase dashboard → **SQL Editor → New query** → paste the contents of
[`schema.sql`](schema.sql) → **Run**. This creates the `incidents`,
`profiles`, and `notifications` tables with Row Level Security policies
scoped to `auth.uid()`.

### 4a. Run the legal knowledge base schema + seed

Same SQL Editor, two more scripts, **in order**:

1. [`legal_kb_schema.sql`](legal_kb_schema.sql) — creates 9 reference tables
   (`legal_sources`, `law_sections`, `penalties`, `scenarios`,
   `scenario_questions`, `authorities`, `authority_contacts`,
   `escalation_paths`, `document_chunks`), each with RLS allowing any
   authenticated user to `SELECT` and nobody to mutate via the app.
2. [`legal_kb_seed.sql`](legal_kb_seed.sql) — ~294 `INSERT ... ON CONFLICT DO
   NOTHING` statements generated from a user-provided research spreadsheet
   (`Mumbai_Legal_KB_All_Domains_Populated.xlsx`, 2026-08-27 pass covering
   all 5 V1 domains). Safe to re-run. Several rows are `pending_review`, not
   `verified` — see that spreadsheet's own `_README` sheet for exactly which
   ones still need a human to open the official URL and confirm before this
   goes anywhere near a real launch; `CitationValidator` treats the two
   statuses differently (qualified vs. fully trusted) but both are usable
   for a working preview.

This is what `POST /conversation/message` (see "AI pipeline" below) actually
retrieves from — without it, `LegalKnowledgeService`/`AuthorityService`
query real tables that simply don't exist yet, and every answer falls back
to the generic "insufficient verified information" response.

### 5. Enable phone/OTP auth

Supabase dashboard → **Authentication → Providers → Phone** → enable it,
and configure an SMS provider (Twilio, MessageBird, or Vonage — Supabase
needs API credentials from one of these to actually send OTP texts). This
is what will eventually replace the app's current fake OTP flow, which
just validates that a 10-digit number was typed.

### 6. Local development

```
cp .dev.vars.example .dev.vars
```

Fill in `.dev.vars` with your Project URL and anon key from step 3 (this
file is gitignored — never commit it). Then:

```
npm run dev
```

Test it:

```
curl http://localhost:8787/health
# {"ok":true}
```

### 7. (Optional) AI pipeline / Ask Legal AI backend

The `/conversation/message` endpoint (see "API" below) needs a Gemini API
key. Get one at [aistudio.google.com/apikey](https://aistudio.google.com/apikey)
— it must **never** be put in `wrangler.toml`, `.dev.vars` committed to git,
or anywhere in the Android app. Add it to your local `.dev.vars` for `npm run
dev`, and as a Worker secret for deploy (step 8). `AI_PROVIDER`,
`GEMINI_MODEL`, and `GEMINI_EMBEDDING_MODEL` are plain (non-secret) config
already set in `wrangler.toml`'s `[vars]` — override them there if you need
a different model.

There's a real (if still `pending_review`-heavy) legal knowledge base behind
this now — see step 4a above. It's a plain domain+jurisdiction SQL filter
plus a keyword relevance pass, not pgvector/embeddings yet (see
`06_AI_ARCHITECTURE.md`/`07_RAG.md`). A domain/scenario the corpus doesn't
cover still legitimately falls back to the honest "I don't have enough
verified information to answer this reliably" — expected, not a bug.

### 8. Deploy

```
npx wrangler secret put SUPABASE_URL
npx wrangler secret put SUPABASE_ANON_KEY
npx wrangler secret put GEMINI_API_KEY
npm run deploy
```

`wrangler secret put` prompts you to paste each value — they're stored
encrypted on Cloudflare, not in this repo. After deploying, Wrangler prints
your Worker's live URL (`https://ailex-api.<your-subdomain>.workers.dev`).

```
curl https://ailex-api.<your-subdomain>.workers.dev/health
```

## API

All routes except `/health` require `Authorization: Bearer <supabase access token>`.

| Method | Path | Body | Notes |
|---|---|---|---|
| GET | `/health` | — | public |
| GET | `/incidents` | — | all of the caller's incidents |
| POST | `/incidents` | `Incident` fields (no `id`/`user_id`) | creates one |
| PATCH | `/incidents/:id` | partial `Incident` fields | e.g. `{ "notes": "..." }` |
| DELETE | `/incidents/:id` | — | |
| GET | `/profile` | — | |
| PATCH | `/profile` | `{ "display_name": "...", "language": "..." }` | upsert |
| GET | `/notifications` | — | |
| PATCH | `/notifications/:id` | `{ "unread": false }` | |
| POST | `/conversation/message` | `{ "input_type": "text", "message": "...", "language": "en", "previous_context"?, "previous_domain"?, "previous_scenario"? }` | runs the AI pipeline (see `src/orchestrator/AIOrchestrator.ts`); needs `GEMINI_API_KEY` (see step 7) |

## AI pipeline

`/conversation/message` runs a modular pipeline, not a single model call —
see `06_AI_ARCHITECTURE.md` for the design and `src/orchestrator/AIOrchestrator.ts`
for the implementation: Input Normalizer → Situation Classifier → Context
Extractor → Risk Engine → Legal Knowledge Service → Authority Service →
Action Planner → Response Generator → Citation Validator → Safety Validator.

`LegalKnowledgeService`/`AuthorityService` are interfaces (`src/legal/`,
`src/authority/`) with two implementations each: a `Mock*` that returns
nothing (still used by tests — see `test/`) and the real
`Supabase*` ones the route actually uses, querying the tables from step 4a
through the caller's own request-scoped Supabase client. Either way, real
legal claims can only ever come from a real implementation of these
interfaces, never from the LLM's own memory — see `CitationValidator`/
`SafetyValidator` for how that's enforced even if a prompt change tried to
bypass it.

Gemini is the only implemented `AIProvider` (`src/ai/GeminiAIProvider.ts`),
but nothing outside `src/ai/` depends on Gemini directly — swapping
providers means implementing `AIProvider`/`EmbeddingProvider` again, not
touching classification/context/planning/response-generation logic.

## What's not done yet

- **Incidents/profile/notifications/Ask Legal AI are all wired up** on the
  Android side — see `BUILD_LOG.md`.
- The legal knowledge base (step 4a) covers all 5 domains but is a first
  research pass, not a launch-ready corpus — many rows are `pending_review`,
  not `verified` (see the spreadsheet's own `_README` sheet), and there's no
  pgvector/embeddings retrieval yet, just domain+jurisdiction+keyword
  filtering.
- There is no `conversations`/`messages` persistence table — `/conversation/message`
  is stateless per call; the caller round-trips `previous_context`/
  `previous_domain`/`previous_scenario` from the previous response to avoid
  re-asking established facts within a session.

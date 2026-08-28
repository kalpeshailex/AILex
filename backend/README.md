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

### 7. Deploy

```
npx wrangler secret put SUPABASE_URL
npx wrangler secret put SUPABASE_ANON_KEY
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

## What's not done yet

This Worker and schema are ready to use, but the **Android app still runs
entirely in-memory** — nothing calls this API yet. Wiring the app up
(adding a Supabase Auth client for real phone/OTP sign-in, and an HTTP
client that calls this Worker instead of `IncidentsViewModel`'s in-memory
list) is the next step once you've deployed this and confirmed `/health`
responds.

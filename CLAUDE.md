# Working in this repo

## Keep BUILD_LOG.md current

`BUILD_LOG.md` is this project's living build record and changelog. After any meaningful change to the app (a new screen, a component rework, a bug fix, a design/UX pass, a dependency change, etc.):

1. Add a new dated entry at the **top** of the Changelog section (newest first) — what changed, why, and what was verified.
2. If the change affects it, update the **Current State** section in place (architecture, screens, components, design tokens, known limitations) so it always reflects the app as it stands right now, not a snapshot from when it was written.

`BUILD_LOG.md` is plain Markdown, not referenced by any Gradle module or source file — editing it never affects the build. Don't skip updating it just because a change felt small; short entries are fine, but the log should stay a true record of what's been done.

## Hard constraints (do not relax without the user explicitly asking)

- ~~Local-only: no backend, no cloud services, no network calls~~ — **relaxed 2026-08-28, explicitly requested by the user.** A Cloudflare Worker + Supabase backend is being added (see `backend/README.md`). As of that date the Worker/schema exist but the Android app itself is **not yet wired up** — it still runs entirely in-memory. Don't assume network calls exist in `app/` until BUILD_LOG.md says a given ViewModel was migrated off in-memory state.
- ~~No real authentication~~ — same exception: real phone/OTP auth via Supabase Auth is the intended replacement for the current fake flow, but that migration hasn't happened yet either.
- ~~No AI API/model~~ — **relaxed 2026-08-29, explicitly requested by the user.** A Gemini-backed AI pipeline (`backend/src/orchestrator/AIOrchestrator.ts` and friends) is deployed server-side behind `POST /conversation/message` — see `06_AI_ARCHITECTURE.md` and `BUILD_LOG.md`. The Gemini key lives only as a Cloudflare Worker secret, never in `app/`. **Ask Legal AI is now wired up** (`core/network/ConversationApi.kt`, `AskLegalAiSessionViewModel`) — every message goes to the real endpoint, replacing the old hand-scripted UPI-fraud demo turn. There is still no real legal knowledge base behind it (`LegalKnowledgeService` is a mock returning nothing), so real answers mostly get the honest "insufficient verified information" fallback by design, not a bug.
- No OCR, no continuous background voice capture — still true; not part of the current work.
- No fabricated legal content, citations, phone numbers, or URLs anywhere in the app.
- No business logic in Compose UI — screens stay dumb renderers over ViewModels.

See `BUILD_LOG.md` → Current State for the full detail behind each of these. `design.md` is the spec behind the existing Android UI/screens; `00_PROJECT_MASTER.md` and the numbered `0X_*.md` files (read `00_PROJECT_MASTER.md` first) are the authoritative spec for the AI/backend/legal-knowledge architecture — `15_DECISIONS.md` is the source of truth if the two ever disagree.

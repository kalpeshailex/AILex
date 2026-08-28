# Working in this repo

## Keep BUILD_LOG.md current

`BUILD_LOG.md` is this project's living build record and changelog. After any meaningful change to the app (a new screen, a component rework, a bug fix, a design/UX pass, a dependency change, etc.):

1. Add a new dated entry at the **top** of the Changelog section (newest first) — what changed, why, and what was verified.
2. If the change affects it, update the **Current State** section in place (architecture, screens, components, design tokens, known limitations) so it always reflects the app as it stands right now, not a snapshot from when it was written.

`BUILD_LOG.md` is plain Markdown, not referenced by any Gradle module or source file — editing it never affects the build. Don't skip updating it just because a change felt small; short entries are fine, but the log should stay a true record of what's been done.

## Hard constraints (do not relax without the user explicitly asking)

- Local-only: no backend, no cloud services (Supabase/Cloudflare/etc.), no network calls.
- No real authentication, AI API/model, OCR, or continuous background voice capture.
- No fabricated legal content, citations, phone numbers, or URLs anywhere in the app.
- No business logic in Compose UI — screens stay dumb renderers over ViewModels.

See `BUILD_LOG.md` → Current State for the full detail behind each of these, and `design.md` for the product spec they were derived from.

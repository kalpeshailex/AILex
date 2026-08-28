# Ailex — Build Log

This file is the project's living record. It has two parts:

- **Current State** — always reflects the app *as it stands right now*. Rewritten in place whenever it goes stale.
- **Changelog** — dated, append-only history of what changed, why, and what was verified. Newest entry on top.

It is a plain Markdown file, not referenced by any Gradle module, resource, or source file — editing it never affects the build. See `CLAUDE.md` for the rule that keeps it updated.

---

## Current State

### What this is

Mumbai Legal Rights AI ("Ailex") — a **local-only** native Android app for everyday legal situations in Mumbai (police stops, traffic challans, railway/Mumbai Local issues, government services, cyber incidents). Kotlin + Jetpack Compose + Material 3, package `com.example.ailex`.

- 8 feature packages, 24 navigable routes, ~21 shared UI components — the full design_handoff_ailex_v1 rebuild (24 screens) is now complete; the design.md-era component/token generation has been fully retired
- The Android app now has **real authentication** (Supabase Auth, email OTP — see Auth, below) but everything else (incidents, notifications, profile data) is still in-memory UI with no AI model. A separate `backend/` Cloudflare Worker + Supabase project was added 2026-08-28 (see below) but is **not yet wired to the app** for data.
- minSdk 24 / targetSdk 36, Compose BOM 2024.09.00, Kotlin 2.0.21, Navigation Compose 2.8.5

### Auth (real, 2026-08-29 → )

The Welcome screen offers two sign-in methods, both against Supabase Auth directly (no SMS/email provider config lives in the app):

- **Email OTP — working end-to-end**, verified on-device. Welcome → "Continue with email" → `EmailScreen` → `AuthViewModel.sendCode()` calls `SupabaseAuthApi.sendEmailOtp()` (plain OkHttp + `org.json` against `POST /auth/v1/otp` — no Supabase SDK; see `core/network/SupabaseAuthApi.kt` for why) → user receives a code by email → `OtpScreen` → `verifyCode()` → `POST /auth/v1/verify` → session (access token + user id) stored in `AppViewModel.state.accessToken`.
- **Phone OTP — UI exists, not functional**: same code path (`sendPhoneOtp`/`verifyPhoneOtp`), but the Supabase project has no SMS provider configured, so it fails with a real (not fabricated) "unsupported phone provider" error surfaced from `AuthFormState.errorMessage`. Will start working the moment an SMS provider (Twilio/MessageBird/Vonage) is added in Supabase — no app changes needed.
- **OTP length is project-configurable, not hardcoded**: this Supabase project sends 8-character codes (not Supabase's historical 6-digit default), so the app reads a single `OtpLength` constant (`features/auth/AuthViewModel.kt`) used everywhere — the OTP screen's box count, input truncation, and validation. If the Supabase project's OTP length setting changes, update that one constant.
- Getting real delivery working end-to-end required two Supabase-side configuration steps beyond the app code, both one-time dashboard settings: (1) custom SMTP (Resend) — Supabase's dashboard won't let you edit an email template's raw HTML source without one configured, and its default shared mailer is unreliable for real use; (2) editing the "Confirm signup" and "Magic Link" email templates to include `{{ .Token }}` — Supabase's default templates only show a `{{ .ConfirmationURL }}` link, not the bare code the app's OTP screen expects.
- **Not done**: session persistence (the access token is in-memory only, same as everything else — lost on process death, no refresh-token flow yet), and the Worker backend (below) still isn't wired to use this session for the data API.

### Backend (in progress, 2026-08-28 → )

A `backend/` Cloudflare Worker (Hono + `@supabase/supabase-js`, TypeScript) was added at the user's explicit request, overriding the local-only constraint below. Full setup/architecture in `backend/README.md`. Status:

- ✅ Worker scaffolded: `/health` (public), `/incidents`, `/profile`, `/notifications` (all require `Authorization: Bearer <supabase access token>`). Every DB query runs through Postgres RLS as the calling user — the Worker never holds a service-role key.
- ✅ `backend/schema.sql` — Postgres tables (`incidents`, `profiles`, `notifications`) + RLS policies, ready to run in Supabase's SQL editor.
- ✅ Locally verified: `npm install`, `tsc --noEmit`, `wrangler deploy --dry-run`, and `wrangler dev` all succeed; `GET /health` → `200 {"ok":true}`, `GET /incidents` without a token → `401`.
- ⬜ Not deployed yet — needs the user's own `wrangler login` + Supabase URL/anon key (`wrangler secret put`) + running `schema.sql` + enabling Supabase's Phone auth provider with an SMS provider (Twilio/MessageBird/Vonage).
- ⬜ Android app not wired up yet — no Supabase Auth client, no HTTP client calling this Worker. `AuthViewModel`, `IncidentsViewModel`, `NotificationsViewModel`, `AppViewModel` are all still pure in-memory `StateFlow`s. This is the next step once the Worker is deployed and confirmed reachable.

### Hard constraints (see CLAUDE.md — two of these are being deliberately relaxed; re-verify the rest after every change)

- ~~Local-only: no git remote, no backend, no Supabase/Cloudflare/cloud services, no network calls~~ — relaxed 2026-08-28. `app/` now makes real network calls to Supabase Auth (see Auth, above); the Worker/data-API side (Backend, below) is built but not yet called from the app.
- ~~No real authentication~~ — relaxed 2026-08-28. Email OTP is real and working (see Auth, above) — a verified Supabase Auth session, not shape-only validation. Phone OTP still only validates shape, pending an SMS provider.
- No AI API keys, no live model. **Ask Legal AI** returns one fixed placeholder reply per message, never generated content.
- No OCR, no continuous background voice capture — the mic control is a purely visual state machine.
- No fabricated legal content, citations, phone numbers, or URLs anywhere. See "Content safety" below.
- No business logic in Compose UI — screens are dumb renderers over `ViewModel`s exposing `StateFlow<UiState<T>>`.
- All state is in-memory only (no DataStore/Room yet) — resets on process death. (Will change once the backend is wired up.)

### Architecture

```
com.example.ailex
├─ core.common        AppViewModel (session state), LegalDomain, IncidentStatus, UiState<T>
├─ domain.legal        Live Situation question sets + per-domain results (Stage 4)
├─ domain.incident     Incident model + seed data (Stage 6)
├─ domain.complaint    Complaint draft templates (Stage 6)
├─ domain.conversation Ask Legal AI suggested prompts + the one worked demo turn (Stage 5)
├─ domain.voice        Voice screen's fixed demo transcript (Stage 5)
├─ domain.escalation   Per-domain escalation routes + legal aid (Stage 7)
├─ features.auth       Welcome → Phone → OTP → Name → Language
├─ features.home       Home tab
├─ features.conversation   Ask Legal AI, Voice, Conversation
├─ features.live_situation  Safety → Urgent → Category → Question/FreeText → Result
├─ features.incidents  Incidents tab, incident detail (notes/timeline/evidence inline)
├─ features.complaint  Complaint draft
├─ features.escalation Escalation routes, domain-aware
├─ features.profile    Me tab (MeScreen — profile card + 4 settings groups) (Stage 8)
├─ features.settings   Notifications, Understanding AILex (Help), Privacy and data, Delete my data (Stage 8)
├─ ui.theme            Color, Spacing, Type, Shape, Theme — one token generation (design_handoff_ailex_v1; the design.md-era generation was fully retired in Stage 8)
├─ ui.components        ~21 shared, reusable composables — one generation, no dead legacy components remaining
└─ ui.navigation        Routes, NavHost, bottom bar
```

Cross-cutting session state (`AppViewModel`), the shared incidents list (`IncidentsViewModel`), and the shared notification list (`NotificationsViewModel`) are Activity-scoped and provided once in `AilexApp()` via `CompositionLocalProvider`, so Home / Ask / Live Situation / Incidents / Me all read and write the same in-memory data.

### Design system

**Migration complete (2026-08-27 → 2026-08-28).** `design_handoff_ailex_v1/README.md` was the spec of record for a full, 24-screen redesign that replaced the `design.md` system, screen by screen, the same way the `design.md` build itself superseded the original placeholder V1. Done in 8 staged sessions (theme+components, then one feature package per session) so the app stayed buildable and runnable between stages — Stage 8 (Settings/Profile, this entry) was the last one; every design.md-era token and component has now been deleted rather than merely superseded.

- **Color**: `ui/theme/Color.kt` carries one token set — `Navy*`, `Blue*`, `Ink*`, `Line*`, `Danger*`, `Caution*`, `Preserve*`, plus `Success*` — design_handoff_ailex_v1's spec, verbatim. The old design.md set (`Primary*`, `Secondary*`, `Accent*`, `Warning*`, `Error*`, `Urgent*`, `Neutral*`) is still declared in the file (nothing references it) since removing it wasn't part of this stage's scope; safe to delete in a future cleanup pass. `MaterialTheme.colorScheme` (in `Theme.kt`) maps onto the new tokens.
- **Type**: `ui/theme/Type.kt`'s `Typography` matches design_handoff_ailex_v1's scale (added `displaySmall`/`headlineSmall`/`titleSmall`/`bodySmall`/`labelMedium`; resized `titleLarge`/`titleMedium`/`bodyLarge`/`bodyMedium`/`labelSmall` to the new spec's numbers). Every screen is now on this scale — there is no unmigrated slot left.
- **Shape**: new `ui/theme/Shape.kt` — `RadiusSheet`/`RadiusCardLg`/`RadiusCard`/`RadiusCardSm`/`RadiusField`/`RadiusChip`/`RadiusPill` plus matching `Shape*` `RoundedCornerShape`s. `Theme.kt`'s stock `Shapes` block now sources from these.
- **Spacing**: `ui/theme/Spacing.kt` gained the new 4dp-base scale (`space1`…`space8`) and named screen/card/control dimensions (`screenHorizontal`, `cardPadding`, `buttonHeightPrimary/Secondary`, `inputHeight`, `navBarHeight`, etc.) alongside the old `xs`…`xxl` scale.
- Each of the 5 `LegalDomain`s now carries the new spec's exact tint/ink pair (`DomainAccents` in `Color.kt`) and blurb copy; Government's tile changed from an amber wash to a neutral gray one (`Line100`/`Ink700`) per the new domain accent table.
- Icon set: `material-icons-extended` **added** (README's explicit instruction, superseding the design.md-era decision to avoid it). `domainIcon()` resolves to real Material icons (`LocalPolice`, `Traffic`, `Train`, `AccountBalance`, `Security`). `AilexIcons.Mic` (hand-built) is kept — no Material-icon equivalent, not migration debt. `AppMark` was a hand-built hexagon mark until 2026-08-28, when it was replaced by the real shield-and-cross logo (`drawable/app_mark.png`).
- **Elevation**: every card is a flat 1dp border, no elevation — borders, not shadows, do the work. No screen still uses `shadowElevation`.

### Navigation map

One `NavHost`, three nested graphs for flow-scoped ViewModels, bottom bar visible only on the 4 tab roots.

| Graph / tab root | Routes |
|---|---|
| `auth_graph` → `AuthViewModel` | `auth/welcome`, `auth/mobile`, `auth/otp`, `auth/name`, `auth/language` |
| `home` (tab) | `home` |
| `live_situation_graph` → `LiveSituationViewModel` | `live_situation/safety?domainId={domainId}`, `/urgent`, `/category`, `/question`, `/freetext`, `/result` |
| `ask_graph` → `AskLegalAiSessionViewModel` (tab root `ask`) | `ask`, `ask/voice`, `ask/conversation` |
| top-level | `escalation?domain={domain}` |
| `incidents` (tab) | `incidents`, `incidents/{id}`, `incidents/{id}/complaint_draft` |
| `me` (tab) | `me` |
| top-level | `notifications`, `help?topic={topic}` (topic ∈ `how`/`limits`/`faq`), `settings/privacy`, `settings/delete` |

### Screens by feature

- **Auth** (5): Welcome, Phone, OTP, Name, Language
- **Home** (1): Home
- **Live Situation** (6): Safety, Urgent, Category, Question, FreeText, Result
- **Ask Legal AI** (3): Ask, Voice, Conversation
- **Incidents** (2): List, Detail (Notes and timeline are inline on Detail, not separate screens)
- **Complaint** (1): Draft
- **Escalation** (1, domain-aware): route
- **Me** (1): MeScreen — profile card (avatar/name/masked number/language, inline name-edit sheet, inline language-picker sheet) + 4 settings groups (Preferences, Privacy and data, Understanding AILex, Account — Log out)
- **Settings** (4, Stage 8): Notifications, Help (3 tabs: How it works / What it cannot do / Common questions), Privacy and data, Delete my data

24 screens total, matching the README's full package map.

### Component library (`ui/components`)

One generation — the design.md-era set was fully deleted in Stage 8 (`PlaceholderTileCard`, `ConfirmationDialog`, `BottomSheetSelector`, `LoadingSkeleton`, `DomainTile`, `UrgentActionCard`, `AppTopBar`, `InformationBanner`, `SafetyBanner`, `ListRow`, `SectionHeading` — all confirmed to have zero remaining call sites before removal).

- **Surfaces**: `AilexCard` (flat 1dp border, press-state `pressedFill`/`pressedBorder`), `AilexBottomSheet`
- **Actions**: `PrimaryButton`/`SecondaryButton`/`DangerButton`/`OutlinedAilexButton` (all support `leadingIcon`), `AilexFilterChip`
- **Inputs**: `AilexTextField` (focus-ring border)
- **Content**: `SectionKicker`, `IconTile`, `StatusPill`, `TagChip`, `Accordion`, `CalloutBanner`, `StepItem`, `TimelineItem`, `SourceCard`
- **Feedback/state**: `UiStateContent`, `EmptyState`, `ErrorState`, `Toast`/`showToast` + `LocalToastHostState`
- **Motion**: `PressScale`
- **Icons/identity**: `AilexIcons.Mic`, `AppMark`, `DomainIcons.domainIcon()`
- **Nav chrome**: `ui/navigation/AilexBottomBar` — rebuilt in Stage 8 to the exact spec (`Navy700` active label + `Blue100` pill behind the icon, `Ink500` inactive with no pill, 1dp `Line200` top border, no elevation), replacing the earlier generic Material3 `NavigationBar` styling; also now shows a small unread-notification dot on the Me tab.

### Content safety rules

Never fabricated, anywhere: legal advice/citations/statute text, phone numbers/station names/URLs for escalation, or "recommended actions" beyond the user's own free-text input.

- Live Situation's question screen asks only domain-agnostic intake questions ("Is there immediate danger right now?", "Where/when did this happen?").
- Its result screen renders the intended section structure (action plan / rights / obligations / authority powers / legal basis / escalation) but every body reads *"Detailed guidance for this situation isn't available in this preview yet"*; the citation accordion always renders with zero entries.
- Ask Legal AI: one fixed placeholder reply per message, never generated content.
- Escalation lists generic categories only ("Contact police control room", "Consult a lawyer") — no invented contact details.
- Settings/Privacy (Stage 8): the prototype's own Privacy and data copy describes a backend ("held on our servers", "removes your data from our servers"). This build has none, so every row on `PrivacyScreen` and the Delete-my-data confirmation copy is re-worded, not lifted verbatim, to describe what actually happens — everything stays on-device.

### Account

- `MeScreen` has a 4th settings group, **Account**, with a single **Log out** row. Confirms via a bottom sheet ("You'll need to verify your mobile number again to sign back in. Your saved incidents stay on this device."), then calls `AppViewModel.clearSession()` only (name/mobile/language reset — incidents and notifications are untouched, unlike Delete my data) and navigates to `auth/welcome` with the whole back stack cleared.

### Known limitations (deliberate, not oversights)

- **Persistence**: no DataStore/Room yet — all state is in-memory, resets on process death (Delete my data's "removed from this device" claim is accurate today, but so is "lost on process death," which is a limitation, not a feature, until real persistence exists).
- **Dark theme**: Light and System currently render the same palette; source spec only publishes light values and gates dark mode on being fully tested.
- **Voice preferences and Export my incidents** (Me tab, Stage 8): the current SETTINGS content has no dedicated screen for either — both are stub rows that show a toast ("...isn't available in this preview") rather than a fabricated settings UI with no real effect behind it.

---

## Changelog

### 2026-08-29 — Real email OTP authentication (Supabase Auth), wired into the app
User asked for real auth with an email option (they don't have an SMS provider set up), replacing the fake shape-only OTP validation for at least one path. Both phone and email are now offered on Welcome; only email actually delivers a code today.

- **New**: `core/network/SupabaseAuthApi.kt` — plain OkHttp + `org.json` calls to Supabase Auth's REST API (`/auth/v1/otp`, `/auth/v1/verify`). Deliberately not the official multiplatform Supabase Kotlin SDK: its transitive dependencies require a newer Kotlin toolchain than this project uses (hit a real `kotlin_module` binary-metadata-version incompatibility trying `okhttp` 5.x for the same reason — settled on `okhttp:4.12.0`, which is Kotlin-toolchain-safe and doesn't bump the project's `compileSdk` requirement).
- **`AuthViewModel`**: added `AuthMethod` (PHONE/EMAIL), `sendCode()`/`verifyCode()` real network calls with loading/error state (`isSending`, `isVerifying`, `errorMessage`) — replacing the old purely-local validation. `OtpLength` is now a single named constant (currently 8, matching this Supabase project's configured OTP length) instead of a hardcoded 6 scattered across validation, box count, and copy.
- **New `EmailScreen.kt`**, `WelcomeScreen` now offers both entry points, `OtpScreen`/`NameScreen` generalized to show the right masked contact and "Email verified"/"Number verified" copy per method (`NameScreen` previously always said "Number verified" — fixed after the user caught it mid-testing).
- **`AppSessionState`** gained `maskedEmail` and `accessToken`; `MeScreen`'s profile line now shows whichever contact method was actually used.
- **Gradle**: added `okhttp` dependency, `buildConfig = true`, and `SUPABASE_URL`/`SUPABASE_ANON_KEY` `buildConfigField`s sourced from `local.properties` (gitignored, real values never committed). Added `INTERNET` permission to the manifest (previously absent — the app made zero network calls before this).
- **Two Supabase-side dashboard fixes needed beyond app code**, both now documented in Current State → Auth: custom SMTP (Resend) was required before Supabase would even allow editing an email template's raw source, and the default "Confirm signup"/"Magic Link" templates had to be edited to include `{{ .Token }}` (they only show a confirmation link by default, not a bare code).
- **Bug caught during testing, fixed same session**: navigating to `EmailScreen` never told `AuthViewModel` to switch its `AuthMethod` away from the default (`PHONE`), so tapping "Send code" from the email screen silently tried to send an SMS and failed with a real "unsupported phone provider" error. Fixed by setting the method explicitly (`LaunchedEffect`) on entry to both `PhoneScreen` and `EmailScreen`.
- Verified end-to-end on a physical device with a real email address: send code → received an actual 8-character code by email → verified → reached Home → Me tab shows the correctly masked email. No crashes in logcat.
- **Not done**: session persistence (access token is in-memory only, lost on process death — consistent with every other piece of app state today, not a new gap), phone OTP (needs an SMS provider added in Supabase, no app changes required when that happens), and the Worker backend from 2026-08-28 still isn't called by the app for incidents/notifications/profile data.

### 2026-08-28 — Backend scaffolding: Cloudflare Worker + Supabase (not yet wired to the app)
User explicitly requested moving off local-only storage/auth onto Supabase (database) + a Cloudflare Worker (backend API), overriding the hard constraint that previously forbade this — see CLAUDE.md and Current State → Backend, above, for the exact status.

- New `backend/` directory: a Cloudflare Worker project (Hono router, TypeScript, `@supabase/supabase-js`) — independent of the Gradle build, doesn't touch `app/`.
- Auth design: the Android app will sign in **directly** against Supabase Auth (phone/OTP) rather than through the Worker; the Worker only handles the data API. Every Worker request carries the user's own Supabase access token, and the Worker creates its Supabase client scoped to that token — so Postgres RLS enforces per-user isolation and the Worker never needs (or gets) the `service_role` key. This was a deliberate simplification over routing auth through a custom backend.
- `backend/schema.sql`: `incidents` (mirrors `domain.incident.Incident`), `profiles` (mirrors `AppViewModel`'s `AppSessionState`), `notifications` (mirrors `NotificationsViewModel`'s `AppNotification`) — each with an RLS policy scoped to `auth.uid()`.
- `backend/README.md`: full step-by-step (install → `wrangler login` → Supabase URL/anon key → run `schema.sql` → enable Phone auth provider → local dev via `.dev.vars` → `wrangler secret put` + `wrangler deploy`) plus the API table.
- Verified locally: `tsc --noEmit` clean, `wrangler deploy --dry-run` bundles (787KB / 157KB gzip, no binding errors), `wrangler dev` serves `GET /health` → `200 {"ok":true}` and `GET /incidents` (no token) → `401` as expected. Not deployed to Cloudflare — that requires the user's own `wrangler login` and Supabase credentials, which weren't shared with the assistant.
- **Explicitly not done in this pass**: no Android code was touched. The app's auth flow, `IncidentsViewModel`, `NotificationsViewModel`, and `AppViewModel` are unchanged and still fully in-memory. Wiring the app to this backend (Supabase Auth SDK, an HTTP client for the Worker's API, replacing each ViewModel's in-memory `StateFlow` with real network state incl. loading/error handling) is a separate, larger follow-up once the user has deployed the Worker and confirmed it's reachable.

### 2026-08-28 — Real app icon and brand mark (shield-and-cross logo)
Replaced the default Android-robot launcher icon and the hand-built abstract hexagon mark on the Welcome screen with the user-supplied shield-and-cross logo.

- **Launcher icon**: generated from the source PNG (trimmed to content, Pillow) — adaptive icon foreground/monochrome PNGs at all 5 densities (`mipmap-{m,h,xh,xxh,xxx}hdpi/ic_launcher_foreground.png` + `ic_launcher_monochrome.png` for Android 13+ themed icons), flat legacy `ic_launcher.png`/`ic_launcher_round.png` (white background, circular-masked for round) for pre-adaptive-icon fallback. `mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml` now point at these instead of the default template's vector drawables; background is a plain white `@color/ic_launcher_background`. The old debug-grid vector drawables (`drawable/ic_launcher_background.xml`, `ic_launcher_foreground.xml`) are deleted.
- **`AppMark` (Welcome screen)**: `ui/components/AppMark.kt` now renders `drawable/app_mark.png` (the same source logo, trimmed) via `Image`/`painterResource` at a fixed 66×72dp, replacing the hand-built `Navy900` hexagon + Material `Balance` icon. That hexagon was deliberately abstract "so it never reads as an official government emblem" (see the old code comment) — this change is a deliberate user-requested override of that choice, not an oversight.
- Verified on-device: launcher icon renders correctly through the OEM's adaptive squircle mask, Welcome screen shows the new mark, no crashes in logcat.

### 2026-08-28 — Incidents tab audit: fixed stale search query bug
Checked the Incidents tab (list, filters, search, detail, complaint draft, escalation, delete) end-to-end on a physical device.

- **Bug found and fixed**: `IncidentListScreen`'s search toggle (`searchOpen = !searchOpen`) collapsed the search input but never cleared `viewModel.searchQuery`, so the last-typed query kept silently filtering the list after the search bar was closed. Symptom: typed "traffic" into search, closed the search bar, then tapping the **Drafts** filter chip showed "Nothing here" / "0 of 4" even though a draft incident exists — the hidden stale query ("traffic") was still ANDed with the status filter, and with no visible search box there was no way to tell why. Fixed by calling `viewModel.setSearchQuery("")` whenever the icon collapses the search bar.
- Everything else verified working: filter chips (All/Active/Resolved/Drafts) once search is inactive, search-as-you-type against title/area/tag, empty state, incident detail (key facts, timeline, notes save, evidence list including the "no longer available" broken-evidence state), delete confirmation dialog (cancelled, not deleted, to preserve seed data), Create complaint draft (Copy/Share, bottom buttons clear of the system nav bar per the insets fix below), and View escalation route. No crashes in logcat.

### 2026-08-28 — UI fixes: system-bar insets, Browse-by-area carousel, icon-only play/stop, Log out
Five reported UI issues, fixed and verified on a physical device:

- **Bottom nav bar overlapped by the system nav buttons**: `AilexBottomBar` never accounted for the navigation-bar inset — under edge-to-edge (`enableEdgeToEdge()` in `MainActivity`) it rendered flush with the bottom of the window, so its icons/labels visually collided with the phone's own 3-button/gesture nav. Fixed by adding `.navigationBarsPadding()` to the bar's root `Column` (after its `background`, so the `Surface` fill still extends behind the system bar).
- **Non-tab screens' content crowding the system nav bar** (reported as "the notification panel is getting merged" — the in-app Notifications screen, reached from Home's bell icon): any route that isn't one of the 4 bottom-tab roots renders with `bottomBar = {}` in `AilexApp`'s `Scaffold`, so `innerPadding` reserves zero space for the nav bar there. Fixed in `AilexApp.kt`: `AilexNavHost`'s modifier now adds `.navigationBarsPadding()` whenever the current route isn't top-level, so detail/settings screens (Notifications, Conversation, Voice, incident detail, complaint draft, escalation, Settings sub-screens, Help) keep their last content clear of the system bar.
- **"Browse by area" is now a horizontal carousel** (`HomeScreen.kt`): replaced the `chunked(2)` 2-column grid with a `LazyRow` of fixed-width (`230.dp`) `DomainGridTile`s, horizontally scrollable, peeking the next card at the edge.
- **Ask Legal AI's Play/Stop control is icon-only**: the rich demo turn's action-chip row (`ConversationScreen.kt`) previously rendered Play/Stop as a labeled pill matching "Save this situation"/"Escalation". Replaced with a new `IconOnlyChip` — a plain 38dp circular icon button (no text) — since the icon alone (speaker vs. stop-circle) already communicates state.
- **Me tab: added Log out** — see Current State → Account, above.

Verified end-to-end on a connected physical device (not emulator): full auth flow (mobile → OTP → name → language) into Home, confirmed the bottom bar no longer overlaps system nav buttons in either 3-button mode, confirmed the carousel scrolls, triggered the UPI-fraud rich demo turn in Ask Legal AI and toggled the Play/Stop icon (`contentDescription` flips "Play" ↔ "Stop" on tap), and ran the full Log out flow (confirm sheet → Welcome screen, back stack cleared). No crashes in logcat across the session. Also fixed, incidentally: `./gradlew :app:assembleDebug` was still broken from a corrupted `AndroidManifest.xml` at the start of this session — see the entry directly below.

### 2026-08-28 — Fixed corrupted AndroidManifest.xml (build was broken)
`app/src/main/AndroidManifest.xml` had two stray characters (`cl`) prepended before the `<?xml version="1.0"...?>` declaration, which made every build fail at the manifest-merge step (`SAXParseException: Content is not allowed in prolog`) — likely a leftover fragment from a bad edit/paste. The rest of the file was intact.

- Fix: removed the stray `cl` prefix so the file starts cleanly with `<?xml version="1.0" encoding="utf-8"?>`.
- Verified: `./gradlew :app:assembleDebug` now succeeds (JAVA_HOME must point at Android Studio's bundled JBR, e.g. `C:\Program Files\Android\Android Studio\jbr`, since no system JDK is on PATH in this environment). Installed the debug APK on a connected physical device (`adb install -r`) and drove it manually: Welcome → "Continue with mobile number" → mobile-number entry (10-digit validation correctly gates the Send code button) → Send code → OTP screen (masked number, resend countdown, 6-digit boxes) all rendered and navigated correctly. No crashes in logcat during the session.

### 2026-08-28 — Stage 8 (final) of the design_handoff_ailex_v1 rebuild: Settings/Profile
Rebuilt the Me tab and its four settings sub-screens — the last stage of the 8-session rebuild. All 24 screens from the README's package map now exist. This stage also did a final cleanup pass: deleted every remaining design.md-era screen and component, leaving one unified design system.

- **Scope reconciliation, done explicitly rather than silently**: the old design.md-era Me tab had 11 sub-screens (Language, Text size, Voice preferences, Privacy, Data deletion, Help/FAQs, About, Terms, Privacy policy, Legal disclaimer, Report incorrect answer). The new spec's `SETTINGS` array in the prototype's script consolidates this to `me` (one profile+settings screen), `notifications`, `help?topic=` (3 tabs covering what were 2 old screens), `settings/privacy`, and `settings/delete` — Text size, About, Terms, Privacy policy, and Legal disclaimer have no equivalent at all in the new spec and were deleted outright (not folded in anywhere); Voice preferences and Export-my-incidents are present as rows but stubbed to a toast, matching the prototype's own `to:'toast'` markers for those two entries.
- **New package `features/settings`**: `NotificationsViewModel`/`NotificationsScreen` (4 seeded notifications lifted verbatim from the prototype's `NOTIFICATIONS` array, cross-referencing the same seeded incidents — `i1`/`i2` — and the same re-verified traffic source as Live Situation's results, so the content is cross-consistent by design), `HelpContent`/`HelpScreen` (3-tab Understanding AILex content, lifted verbatim from the prototype's `HELP` object), `PrivacyScreen`, `DeleteDataScreen`.
- **`features/profile/MeScreen.kt`**: profile card (initials avatar, name, masked mobile · language, an inline bottom-sheet name editor) plus 3 grouped settings sections built from a small local `SettingsRowSpec`/`SettingsGroup` pair (indented dividers between rows in one card, not bespoke per-row cards).
- **Privacy content adapted, not lifted verbatim** — see Content safety rules above: the prototype's Privacy and data copy assumes a backend this build doesn't have, so every row's body text was re-worded to accurately describe on-device-only storage, and the delete confirmation copy drops "from our servers."
- **Delete my data has real computed counts**, per the README's explicit instruction ("Counts must be real," unlike the prototype's hardcoded 11/2/1): saved incidents from `IncidentsViewModel.incidents.size`, notes+timeline events summed across incidents, complaint drafts counted by non-empty `complaintEdits`, profile+mobile fixed at 1. Confirming requires typing `DELETE` in a real (uppercase, letters-only, 6-char-capped) text field; confirming calls `IncidentsViewModel.clearAll()` and a new `AppViewModel.clearSession()`, shows a toast, and navigates to Welcome with the entire back stack cleared.
- **`AilexBottomBar` rebuilt** to the README's exact spec (`Navy700` active label + `Blue100` pill behind the icon, `Ink500` inactive with no pill, 1dp `Line200` top border, no elevation, 11.5sp/500 labels) — the previous generic Material3 `NavigationBar` styling (icon-scale animation, tonal colors) is gone. It also renders a small unread-count dot on the Me tab; Home's bell icon shows the same real unread state instead of the placeholder comment that was there since Stage 3. `Destination.kt`'s Ask/Incidents icons were also wrong (`Info`/`Warning` instead of the spec's `forum`/`folder_open`) — fixed to `Forum`/`FolderOpen`.
- **Final legacy cleanup**: deleted the entire old `features/profile` screen set (`ProfileScreen`, `LanguageScreen`, `TextSizeScreen`, `VoicePreferencesScreen`, `PrivacyScreen`, `DataDeletionScreen`, `HelpFaqsScreen`, `AboutScreen`, `TermsScreen`, `PrivacyPolicyScreen`, `LegalDisclaimerScreen`, `ReportIncorrectAnswerScreen`) and every design.md-era component confirmed to have zero remaining call sites (`PlaceholderTileCard`, `ConfirmationDialog`, `BottomSheetSelector`, `LoadingSkeleton`, `DomainTile`, `UrgentActionCard`, `AppTopBar`, `InformationBanner`, `SafetyBanner`, `ListRow`, `SectionHeading`). `Routes.kt`'s `Me` object was rewritten from a nested `profile/...` tree to the flat top-level paths (`me`, `notifications`, `help?topic={topic}`, `settings/privacy`, `settings/delete`) the README actually specifies.
- Verified with `./gradlew :app:compileDebugKotlin` and `:app:assembleDebug` (both BUILD SUCCESSFUL, zero warnings after the legacy-component deletion removed two pre-existing deprecation warnings from `AppTopBar`/`DomainTile`).
- **Not done here, and out of scope for a UI-only rebuild**: Voice preferences and Export-my-incidents remain toast stubs (see Known limitations); the design.md-era color/type token generation in `Color.kt`/`Type.kt` is now fully unreferenced but wasn't deleted this stage (low-risk future cleanup, not blocking anything).

### 2026-08-28 — Stage 7 of the design_handoff_ailex_v1 rebuild: Escalation
Rebuilt Escalation (screen 19) in its own `features/escalation/EscalationScreen.kt`, per the README's package map — it lived inside `features/conversation` as a generic four-category placeholder before this. Deleted the old one.

- **Content lift**: `domain/escalation/EscalationRoutes.kt` — the five domain-specific ordered routes plus the legal-aid entry always appended last, lifted verbatim from the prototype's `ESCALATION` and `LEGAL_AID` objects. `authoritiesFor(domain)` falls back to the traffic route set for a null/unresolved domain, matching the prototype's own `ESCALATION[cat] || ESCALATION.traffic` default rather than crashing or showing nothing.
- **Escalation is now genuinely domain-aware** end to end: the route is `escalation?domain={domain}`, and all three existing callers were updated to pass a real domain instead of the old parameterless placeholder — Live Situation's Result screen passes the situation's domain, Conversation passes Cyber (matching its fixed UPI-fraud demo topic, same as the prototype's own hardcoded choice for that screen), and Incident detail passes the incident's own domain (its `onEscalation` callback changed shape from `() -> Unit` to `(LegalDomain) -> Unit` to carry it).
- **A content-safety line drawn deliberately**: contact rows only make `call` entries tappable (dialing via `ACTION_DIAL`). `mail` and `web` entries render as reference text only — the prototype itself never hardcodes a real URL for these ("Web · verify the URL in the app"), and `CLAUDE.md` forbids fabricating one, so making them look tappable without a real destination would be worse than leaving them inert.
- Verified with `./gradlew :app:compileDebugKotlin` and `:app:assembleDebug` (both BUILD SUCCESSFUL, zero warnings) — clean on the first pass this time, having learned from the sloppier edit-fix-edit cycles in earlier stages to nail down exact icons/imports/dividers before writing rather than after.
- **Not done here**: the "Web · verify the URL in the app" contacts are exactly what they say — real URLs still need to be sourced and wired before this ships. Nothing else deferred from this stage.

### 2026-08-28 — Stage 6 of the design_handoff_ailex_v1 rebuild: My Incidents / Incident detail / Complaint draft
Rebuilt `features/incidents` (`IncidentListScreen`, `IncidentDetailScreen`) and — in a new package, per the README's package map — `features/complaint/ComplaintDraftScreen.kt`, to screens 16–18. This stage also moved the core `Incident` model to `domain/incident`, since it needed real restructuring anyway.

- **`Incident` moved from `features/incidents` to `domain/incident`** (`Incident.kt`), matching the package the README always intended for it. Reshaped along the way to fit what the new detail screen actually renders: `keyFacts` is now `List<Pair<String, String>>` (a real key/value list, not pre-joined "question: answer" strings) with a proper `IncidentTimelineEvent(title, whenText, dotColor, body)` and `EvidenceRef(displayName, meta, available, uri)`. Dropped fields the new spec's detail screen doesn't show (`whatHappened`, `recommendedActions`, `actionsTaken`, `referenceNumber` — the old flat `evidenceRefs: List<String>`, `complaintDraft: ComplaintDraft?`); `complaintEdits: Map<Int, String>` replaces the old `ComplaintDraft` object, paired with the generated template text now in `domain/complaint`. Updated the three existing call sites that construct `Incident(...)` (`HomeScreen`, `ResultScreen`, `ConversationScreen`) and `LiveSituationViewModel.answeredFacts()`'s return type accordingly.
- **Content lift**: `domain/incident/IncidentSeedData.kt` (the four seeded incidents — i1–i4, full summary/key-facts/timeline/evidence — verbatim from the prototype's `INCIDENTS` array) and `domain/complaint/ComplaintDraftTemplates.kt` (all five domain-specific drafts — traffic/cyber/railway/govt/police, 7 sections each — verbatim from `DRAFTS`/`DRAFT`). `IncidentsViewModel` now seeds `_incidents` with these four on creation instead of starting empty, matching the design's intent of a populated demo.
- **Scope cut, not an oversight**: the new nav map has no separate notes route (`incidents/{id}/notes` is gone) — Notes is inline on the detail screen per the README, so `IncidentNotesScreen.kt` was folded in and deleted. Deleted the now-fully-unused design.md-era `IncidentRow`/`StatusChip` components (superseded by `StatusPill`/`TagChip`/`IconTile` from Stage 1).
- Timeline rendering reuses Stage 1's `TimelineItem` component (its first real caller); "Add a timeline event" opens Stage 1's `AilexBottomSheet` (also its first real caller outside Home's coverage sheet).
- Evidence handling is honestly scoped: the model carries `uri: Uri?` and an `available` flag matching the README's data-model sketch, but there's no real `ACTION_OPEN_DOCUMENT` file-picker flow yet to populate it — the seeded incidents' evidence rows render correctly (including the one deliberately-missing file per seed data), but a user can't attach a new file from this screen yet. Flagging this now rather than silently shipping a dead-looking feature.
- Delete confirmation uses a stock Material3 `AlertDialog` — the README doesn't specify bespoke visuals for this dialog, so no custom redesign was invented for it.
- Verified with `./gradlew :app:compileDebugKotlin` and `:app:assembleDebug` (both BUILD SUCCESSFUL, zero warnings — also modernized `ComplaintDraftScreen`'s clipboard copy to `LocalClipboard`/`ClipEntry` instead of the deprecated `LocalClipboardManager`, since this was new code anyway).
- **Not done here**: Escalation is still the old generic placeholder (`features/conversation/EscalationScreen.kt`) — "View escalation route" from Incident detail and Complaint draft's implicit escalation context both just open it unchanged, same as every other stage's `onEscalation` wiring so far.

### 2026-08-28 — Stage 5 of the design_handoff_ailex_v1 rebuild: Ask Legal AI / Conversation / Voice
Rebuilt `features/conversation` — `AskScreen` (renamed from `AskLegalAiScreen.kt`), `VoiceScreen` (new), and `ConversationScreen` — to the README's spec (screens 13–15). This stage also dropped two screens the new spec doesn't have.

- **Scope cut, not an oversight**: the design_handoff_ailex_v1 README's 24-screen list has no History or Citation-detail screen — Ask's "incident history" icon just opens the My Incidents tab (an existing top-level route), and citations render inline via the Legal basis accordion. Deleted `HistoryScreen.kt`, `CitationDetailScreen.kt`, and the now-fully-unused `CitationAccordion`/`Citation` and `VoiceControlBar` (`ui/components`), plus their routes. `AskLegalAiSessionViewModel` dropped multi-session storage (`sessions`/`selectSession`) accordingly — one ongoing session per graph visit is all the new flow needs.
- **Content lift** (`domain/conversation`, `domain/voice`, new): `SuggestedPrompts` (the 4 example prompts, each mapped to a `LegalDomain` for its icon/tint rather than storing a duplicate color), `UpiFraudDemoTurn` (the prototype's one fully-worked example answer — urgency banner, 4 steps, 2 legal sources, follow-up chips, all verbatim from `convoSteps`/`convoSources`/`convoChips`), and `VoiceDemo` (the fixed spoken-word transcript `VoiceScreen` shows, since there's no real speech recognition).
- **`AskLegalAiSessionViewModel` reply logic**: the user's message bubble always shows what the user actually typed or tapped — never fabricated. The *reply* is fixed either way (no model, per `CLAUDE.md`): the first reply in a session is the fully-worked `UpiFraudDemoTurn` (demonstrating the intended rich-answer UI), every reply after that is the existing plain placeholder sentence. This was a deliberate call, not a literal port — the prototype's own JS actually re-answers *every* message with the same canned UPI-fraud card regardless of what was typed, which reads fine as a click-through demo but would look broken in a real app (ask about a traffic stop, get UPI-fraud advice back, every time).
- **`VoiceScreen`**: Listening → Transcribing is the one user-driven step (tap Stop); Transcribing → Review now auto-advances after a short simulated delay rather than needing another tap, since a real recognizer completing is what that transition represents once one exists. One route serves both callers that open it (Ask's mic button and Conversation's reply composer) — both want the identical outcome ("send this text into the session and land on Conversation"), so no per-caller branching was needed.
- Verified with `./gradlew :app:compileDebugKotlin` and `:app:assembleDebug` (both BUILD SUCCESSFUL, zero warnings).
- **Not done here**: Live Situation's `FreeTextScreen.onSpeakInstead` (flagged as a no-op in Stage 4) still isn't wired to `VoiceScreen` — doing that properly means threading the transcript back via a `SavedStateHandle` nav result, which is a self-contained follow-up, not attempted under this session's Ask-Legal-AI scope. The feedback chips in `RichDemoTurn` (Save/Escalation/Play) work; nothing records "was this useful" anywhere in Conversation because the new spec doesn't ask for that row here (only Result has it).

### 2026-08-28 — Stage 4 of the design_handoff_ailex_v1 rebuild: Live Situation
Rebuilt the entire Live Situation flow (`features/live_situation`) to the README's spec: Safety → Urgent → Category → Question(s)/FreeText → Result. This is the largest stage — it replaces the design.md-era flow's domain-agnostic generic questions and "detailed guidance isn't available yet" placeholder with the real, five-domain content lifted verbatim from the prototype.

- **Content lift** (`domain/legal`, new): `LiveSituationQuestions.kt` (3 questions × 5 domains, plus the location-question index used when saving an incident) and `LiveSituationResults.kt` (title/safety note/situation summary/fact chips/5 action steps/7 sections/2 legal sources × 5 domains) — both transcribed verbatim from `AILex Prototype.dc.html`'s `QUESTIONS` and `RESULTS` script objects, not from the README's prose paraphrase, since I could confirm the actual content only from the script (e.g. the Mumbai Local line-choice question has 4 options in the real data, not the 5 the README's prose implies). `SituationResult.kt` defines the supporting data model (`SectionId`, `ResultSection`, `LegalSource` with a real `java.time.LocalDate`, etc.), reusing the existing `LegalDomain` enum rather than introducing a parallel one.
- Added Android core library desugaring (`compileOptions.isCoreLibraryDesugaringEnabled` + `desugar_jdk_libs`) so `java.time.LocalDate` (used for `LegalSource.lastVerified`, matching the README's data-model sketch) works down to minSdk 24 — without it, `LocalDate` needs API 26 and would crash on older devices.
- All 6 screens rebuilt: `SafetyScreen`, `UrgentScreen` (new — 4 real emergency numbers, dialing via `ACTION_DIAL`, never auto-calling), `CategoryScreen`, `QuestionScreen`, `FreeTextScreen` (new), `ResultScreen` (the big one — safety banner, "what I understand" facts, tappable numbered action steps via the Stage 1 `StepItem`, the 7 fixed collapsible sections via `Accordion`/`SourceCard`/`CalloutBanner`, escalation row, save-to-incident, feedback row, disclaimer). Deleted the superseded `LiveSituationStartScreen`, `LiveSituationCategoryScreen`, `LiveSituationQuestionScreen`, `LiveSituationResultScreen`, and `DomainScenarioListScreen` (no equivalent in the new flow — domain selection now leads straight into that domain's questions rather than a free-text "scenario picker").
- **Toast infrastructure**: added `LocalToastHostState`, provided once at `AilexApp`'s root `Scaffold` (not per-screen) so a toast triggered by e.g. "Save this situation" survives the navigation back to Home instead of vanishing with the screen that requested it. First real use of the Stage 1 `Toast`/`showToast` component.
- **Product decisions made where the spec was silent or self-contradictory**, flagged here rather than buried in code comments only:
  - Every Live Situation entry point (Home's "Start live help" *and* each domain tile) now passes through the Safety screen first, matching the README's safety-first framing ("put safety ahead of paperwork"). A domain tile pre-fills the domain (via a `domainId` nav arg) and skips straight to that domain's questions after the safety check, skipping Category since the domain is already known.
  - Category's "Something else" tile: the prototype's own JS actually hard-codes this to silently reuse the *traffic* question set (`cat.id === 'other' ? 'traffic' : ...`) — almost certainly a demo shortcut, not an intentional design. Rebuilt it honestly instead: it opens `FreeTextScreen` in a domain-less general mode and lands on a distinct fallback Result (no fabricated domain-specific guidance, an honest "I don't have enough verified information to answer this reliably", and a pointer to the Maharashtra State Legal Services Authority), consistent with the app's established content-safety rule against fabricating guidance.
  - Section item icon colors (check/close/arrow_right) are undefined in the prototype's own data (`i.ink` is referenced in the template but never actually set on any item) — assigned a sensible fixed mapping (Success500/Danger600/Ink500) rather than reproducing the apparent gap.
  - Two Material Symbols glyphs the prototype uses have no classic-icon-set equivalent: `emergency_home` (Home's live-help kicker, already substituted in Stage 3) and `shield_with_heart` (Safety screen's "I am safe" option) — substituted `Emergency` and `GppGood` respectively.
- Fixed two per-card pressed-state mismatches found while cross-checking the prototype's actual `style-hover` CSS against `AilexCard`'s default press treatment: the Safety screen's two option cards don't lighten to Blue050/BlueBorder like most tappable cards do (the prototype only darkens their existing border on press), and the "Where to escalate"/"Ask Legal AI" rows' hover fill is `Blue100`, one step darker than their resting `Blue050` — both are now explicit per-card overrides. The Ask Legal AI fix also applies to Stage 3's Home screen.
- Verified with `./gradlew :app:compileDebugKotlin` (BUILD SUCCESSFUL, zero warnings) and `:app:assembleDebug`. Still no device connected this session for an on-device check — this stage in particular (multi-screen nav flow, back-stack popUpTo logic, the free-text/question interplay) would benefit from a real click-through before trusting it fully.
- **Not done in this stage**: the "Read aloud" speak toggle on Result is a visual on/off state only (no TTS wired up — that's `core/voice`, out of scope here); the feedback row's Yes/No/Incorrect buttons render but don't record anything yet; `onEscalation` and the notification bell still point at pre-existing/no-op placeholders pending their own stages (Escalation, Settings).

### 2026-08-27 — Stage 3 of the design_handoff_ailex_v1 rebuild: Home
Rebuilt `features/home/HomeScreen.kt` to the README's spec — header, live-help card, domain grid, Ask Legal AI row, recent incidents, coverage sheet — cross-checked pixel-for-pixel against the prototype's HTML/CSS.

- `AilexCard` (Stage 1) gained press feedback: any card with `onClick` set now lightens to `pressedFill`/`pressedBorder` (default `Blue050`/`BlueBorder`) while pressed, matching the interactions spec ("every tappable card lightens to `Blue050`..."); overridable per call site for cards that should deepen their own tint instead. This is additive and applies retroactively to Stage 2's auth `LanguageScreen` option rows too.
- `DangerButton` (Stage 1) gained an optional `leadingIcon`, matching `PrimaryButton`/`SecondaryButton` — needed for "Start live help"'s bolt icon.
- `AilexBottomSheet` (Stage 1) had its `sheetState` parameter removed — exposing Material3's experimental `SheetState` type in the signature was forcing every call site to opt in to `ExperimentalMaterial3Api`, for a parameter nothing used. It now creates its own internally; the experimental annotation stays fully contained in `BottomSheet.kt`.
- Home's "Recent incidents" now explicitly sorts by `savedAt` descending before taking 2 — the shared `IncidentsViewModel`'s raw list is insertion-ordered, not necessarily recency-ordered, so this was a real (if currently unobservable, since nothing saves incidents yet) correctness fix, not just a style choice.
- The `emergency_home` Material Symbol used for the live-help card's kicker icon has no equivalent in the classic Material Icons set `material-icons-extended` ships (it's a newer Material Symbols-only glyph) — substituted `Icons.Filled.Emergency` (a siren), noted inline.
- Notification bell renders fully styled (44dp, white, `Line200` border) but never shows the unread badge yet — real unread state depends on the `features/settings` Notifications screen's seed data, not built in this stage. Tapping it is currently a no-op; wire it once that screen exists.
- Domain tile taps still route through the old `Routes.LiveSituation.domainScenarios(domainId)` → `DomainScenarioListScreen` (design.md-era intermediate step) — deliberately left alone. The new spec's Live Situation flow (safety check → category → questions) doesn't have an equivalent screen, so this transition gets redesigned in the Live Situation stage, not here.
- Verified with `./gradlew :app:compileDebugKotlin` (BUILD SUCCESSFUL, zero warnings). Still no device connected this session for an on-device check.
- Next: Live Situation (safety check, urgent help, category, questions, free-text, result) — needs the question sets and per-domain results lifted verbatim out of the prototype's script block into `domain/legal`, plus a decision on how Home's/the bottom nav's entry points feed into it.

### 2026-08-27 — Stage 2 of the design_handoff_ailex_v1 rebuild: auth flow
Rebuilt all 5 auth screens (`features/auth`: Welcome, Phone, OTP, Name, Language) to the README's exact spec, using the Stage 1 theme/components. Cross-checked every pixel value against the prototype's actual HTML/CSS (`AILex Prototype.dc.html`) rather than the README's paraphrase alone, since the two disagree in one place (see below).

- `WelcomeScreen`: new hexagonal `AppMark` (56×64dp, `Navy900`, white `balance` icon), points panel, and footnote, replacing the old shield-less placeholder mark. The hexagon's vertices are lifted verbatim from the prototype's `clip-path:polygon(50% 0,100% 16%,100% 62%,50% 100%,0 62%,0 16%)` — a pointed-top/bottom hexagon with flat vertical sides, which is the *opposite* of the README prose's "flat top and bottom edges" description; trusted the CSS over the paraphrase.
- `MobileNumberScreen.kt` renamed to `PhoneScreen.kt` to match the README's file-per-screen naming. Phone/OTP/Name screens now use a new shared `AilexTextField` (`ui/components`) — `Line300` border, `Blue600` + 3dp `Blue100` ring on focus, ring implemented as permanently-reserved padding whose color toggles, so focusing never shifts layout.
- `OtpScreen`: 6 visual cells over a transparent `BasicTextField` overlay (standard hidden-field OTP pattern) instead of a single visible `OutlinedTextField`; masked-number format now matches the prototype exactly (`+91 98XXX XX210` — first 2 digits + "XXX XX" + last 3). Dropped the prototype's "Autofill demo code" chip per the README's explicit instruction; replaced with a real 24s countdown → "Resend code" text button. Real SMS autofill (a `BroadcastReceiver` on the SMS Retriever API) is not wired up — out of scope for a UI-only stage, left as a follow-up.
- `LanguageScreen` (auth): three language rows now show the native-language subtitle (`AppLanguage` gained a `nativeLabel` field: "Recommended for this release" / "हिंदी" / "मराठी").
- Removed onboarding's inline red error messages (`AuthFormState.mobileError`/`otpError`/`nameError` deleted) — the README is explicit that onboarding buttons are disabled, never error-flagged. Validity (`isPhoneValid`/`isOtpValid`/`isNameValid`) is now computed on `AuthFormState` itself, keeping the validation rule out of Compose UI per `CLAUDE.md`.
- Verified with `./gradlew :app:compileDebugKotlin` (BUILD SUCCESSFUL, zero warnings — also cleaned up 4 pre-existing `Icons.Filled.ArrowBack` deprecation warnings in the touched files by switching to `Icons.AutoMirrored.Filled.ArrowBack`). Still no device connected to this session for an on-device check.
- Next: Home, then Live Situation (which needs the question sets and per-domain results lifted verbatim out of the prototype's script block into `domain/legal`).

### 2026-08-27 — Stage 1 of the design_handoff_ailex_v1 rebuild: theme + components
User handed over `design_handoff_ailex_v1/` — a high-fidelity, 24-screen prototype (`AILex Prototype.dc.html`) with a detailed README specifying exact colors/type/spacing/components per screen, plus verbatim content (question sets, results, drafts, escalation routes, help copy) in the prototype's `<script data-dc-script>` block. Asked to rebuild the app's UI to this spec, staged across sessions (theme+components first, then one feature package per session) so the app can be run and checked between stages. This is Stage 1: theme tokens and shared components only — no screens rebuilt yet.

- Added the full design_handoff_ailex_v1 color/type/shape/spacing token set (see Current State → Design system) alongside the existing design.md tokens, rather than replacing them outright — a mechanical rename across every screen would have been required to avoid breaking the build, and that per-screen work belongs in the later staged sessions instead. The two sets share almost all hex values, so this isn't a visible seam.
- Added `material-icons-extended`, reversing the earlier design.md-era decision to avoid it — the new spec explicitly calls for it and needs a much larger icon vocabulary (`gavel`, `balance`, `assignment_ind`, `alt_route`, `bookmark_add`, etc.) than the 49-icon core set covers.
- Updated `LegalDomain`'s per-domain tint/ink and blurb copy to the new domain accent table; `domainIcon()` now resolves real Material icons instead of loose reuses.
- Built the new shared component set called for by the README's component inventory (17 components — see Current State). Restyled `PrimaryButton`/`SecondaryButton`/`TagChip` in place (compatible signatures, so all ~15 existing call sites across auth/home/incidents/live_situation/profile kept compiling unchanged); everything else is additive.
- Verified with `./gradlew :app:compileDebugKotlin` (BUILD SUCCESSFUL, no new warnings). No device was connected this session to verify on-device — recommend a visual spot-check of Home, Incidents and Profile (the screens most affected by the type-scale changes on unmigrated slots) before the next stage.
- Next stages, in order: auth flow (`features/auth`, rebuilt to the README's 5-screen spec), Home, then Live Situation (which also requires lifting the question sets and per-domain results verbatim out of the prototype's script block into `domain/legal`).

### 2026-08-26 — Build log established
Created this file and `CLAUDE.md`'s instruction to keep it updated after every meaningful change, so the project has a durable, in-repo record of what's been built and why — not just conversation history.

### 2026-08-26 — Me tab reworked for a professional settings layout
The Me tab looked unfinished: bare, ungrouped rows directly on the background, no leading icons, two identical-looking buttons for the Theme toggle with no indication of which was selected.

- `SettingsRow` gained an optional leading icon badge and a `trailingContent` slot (defaults to a chevron), so it can host a toggle or segmented control inline instead of every row needing bespoke layout.
- New `SettingsGroupCard` + `SettingsRowDivider` wrap each settings section in a single elevated card with dividers between rows, replacing bare rows on the page background.
- New profile header card: circular initials avatar, name, masked mobile + language on one line.
- Theme control rebuilt as a real segmented pill that now visibly reflects `appState.themeMode` (previously two buttons with no selected-state indication at all — a real bug, not just a style gap).
- "Delete my data" gets a red-tinted icon badge to signal the destructive action.
- Verified on-device: header, all three grouped sections, and the working Theme selector.

### 2026-08-26 — UX/UI professional polish pass
The full design.md build (below) was functionally complete but visually flat — Material 3 defaults with no elevation, no icon treatment, no motion. User asked for a "completely professional" pass. Scope: shared component layer only, no new screens/routes/ViewModels.

- **Theme**: added an explicit `Shapes` block (`Theme.kt`) so stock components inherit the app's own corner radii.
- **Elevation**: added `shadowElevation`/`tonalElevation` to cards, tiles, rows, chat bubbles, bottom nav — previously every `Card` overrode its container color, which silently killed Material 3's tonal elevation, so nothing read as lifted off the background.
- **Icon badges**: `DomainTile` and `IncidentRow` icons moved into tinted circular badges. **Caught a real bug on-device**: `LegalDomain.tileBackground` values are pale wash tones meant for large fills, not icon tints — using them as icon tint made icons nearly invisible. Fixed by adding a saturated `accentColor` field per domain and repointing all 4 call sites (`HomeScreen`, `IncidentsScreen`, `LiveSituationCategoryScreen`) at it.
- **Composer**: rebuilt from a bare `OutlinedTextField` + two `IconButton`s into a pill-shaped messaging composer; send button fills with the primary color once there's input.
- **MessageBubble**: asymmetric "tail" corners per sender side, subtle shadow.
- **AppTopBar**: switched `CenterAlignedTopAppBar` → start-aligned `TopAppBar` (native convention) with a bottom divider.
- **AilexBottomBar**: added a lifted shadow edge and an icon-scale animation on the selected tab.
- **Motion**: `PressScale` (new, shared `Modifier` extension) applied to buttons/tiles/rows; `UiStateContent` now cross-fades between loading/empty/success/error instead of cutting instantly.
- **EmptyState/ErrorState/InformationBanner/SafetyBanner**: added leading icon badges (were text-only).
- **WelcomeScreen**: restructured from dead-centered text to a top-weighted onboarding layout (mark + headline near top, CTA + info banner pinned toward the bottom).
- Verified end-to-end on the connected device: full auth flow, Home, Ask Legal AI → Conversation (save-to-incident), Incidents list, Me. Build stayed dependency-free (no `material-icons-extended`, no new libraries) and all hard constraints held.

### 2026-08-25/26 — Full design.md UI/UX build
User authored a 732-line product spec (`design.md`) covering the full color/type/spacing system, a 13-screen navigation model, and a component inventory, and asked for the complete UI/UX to be built from it in one pass — superseding the placeholder-only V1 below.

Three scope decisions were confirmed with the user before starting: (1) the auth flow is a real, gating nav flow but entirely local — no SMS/backend/stored credentials; (2) Ask Legal AI renders a real user bubble + exactly one fixed placeholder assistant reply, never fabricated content; (3) build everything in this pass rather than phased.

- Rebuilt the theme layer (`Color.kt`, new `Spacing.kt`, new `AilexExtendedColors.kt`, rewritten `Theme.kt`/`Type.kt`) from design.md's token spec.
- Built ~26 shared components and the full navigation model (`Routes.kt`, nested graphs for Auth/Live Situation/Ask, graph-scoped ViewModels via `getBackStackEntry`).
- Built all 6 feature areas end to end: Home, Live Situation, Conversation, Incidents (promoted to Activity-scoped/shared), Me/Profile, Auth.
- Added the content-safety rule (§ above) closing a gap the initial architecture plan flagged: Live Situation's result screen renders full section structure with honest placeholder bodies rather than fabricating guidance.
- Fixed two non-obvious Compose Navigation crashes found via `adb logcat`: (1) `startDestination` must point at a route that is a direct child of the root graph, not one nested inside a sub-graph; (2) a graph-scoped `getBackStackEntry(GRAPH)` lookup gets re-invoked during the exit transition after `popUpTo(inclusive = true)` already removed that graph's back-stack entry — fixed by wrapping the lookup in `remember {}`.
- Verified on the connected device across the full spot-check list (auth flow, Home, Live Situation, Ask AI + Conversation + History, Incidents + Detail + Complaint Draft, Me, bottom-bar visibility, large font-scale pass).
- No new dependencies added; `material-icons-extended` explicitly rejected in favor of core-icon reuse + 2 hand-built `ImageVector`s.

### 2026-08-24 — Initial V1 placeholder build
First working build: entry point, theme shell, Navigation Compose wiring, 4 placeholder screens (Home/Ask/Incidents/Profile) behind bottom navigation. Established the local-only constraints (no git, no backend, no external services, no OCR, no continuous voice, no fabricated legal content) that held for every later phase. Verified running on a connected physical device via `adb`.

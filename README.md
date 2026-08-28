# Ailex

**Legal first-aid for everyday situations in Mumbai.** A local-only native Android app covering police stops, traffic challans, Mumbai Local (railway) issues, government services, and cyber incidents.

Built with Kotlin + Jetpack Compose + Material 3.

## What this is (and isn't)

Ailex is a UI/UX prototype exploring what a Mumbai-focused legal-help app could look like — every screen is real and navigable, but nothing behind it is live:

- **No backend, no cloud services, no network calls.** Everything runs on-device.
- **No real authentication.** The mobile → OTP → name → language flow only validates input shape (10-digit number, 6-digit code) and advances local state — nothing is transmitted anywhere.
- **No AI model.** "Ask Legal AI" returns one fixed placeholder reply per message, never generated content.
- **No OCR, no continuous background voice capture.** The mic control is a purely visual state machine.
- **No fabricated legal content.** Live Situation's result screen and Ask Legal AI never invent citations, phone numbers, or URLs; escalation routes only use real, publicly published helplines.

See [`BUILD_LOG.md`](BUILD_LOG.md) for the full build history, current architecture, and known limitations, and [`design.md`](design.md) for the original product spec.

## Architecture at a glance

```
com.example.ailex
├─ core.common        Session state, shared domain types, UiState<T>
├─ domain.*           Static content: legal Q&A, incidents, escalation routes, demo conversation
├─ features.*         One package per flow: auth, home, conversation, live_situation,
│                      incidents, complaint, escalation, profile, settings
├─ ui.theme           Design tokens — color, type, shape, spacing
├─ ui.components       ~21 shared, reusable composables
└─ ui.navigation       Routes, NavHost, bottom bar
```

One `NavHost` with nested graphs for flow-scoped ViewModels; screens are dumb renderers over `ViewModel`s exposing `StateFlow<UiState<T>>` — no business logic in Compose UI. All app state (profile, incidents, notifications) is in-memory only and resets on process death; there's no persistence layer yet.

24 screens across 8 feature packages. Full detail in `BUILD_LOG.md` → Current State.

## Requirements

- Android Studio (or the command-line tools + a JDK — this project builds with the JBR bundled in Android Studio)
- Android SDK, minSdk 24 / targetSdk 36
- A connected device or emulator running Android 7.0+

## Building

```bash
./gradlew :app:assembleDebug
```

## Running

Install the debug build on a connected device/emulator:

```bash
./gradlew :app:installDebug
```

or open the project in Android Studio and hit Run.

## Hard constraints

These are intentional and shouldn't be relaxed without a deliberate decision (see `CLAUDE.md`):

- Local-only — no backend, no cloud services, no network calls.
- No real authentication, AI API/model, OCR, or continuous background voice capture.
- No fabricated legal content, citations, phone numbers, or URLs anywhere in the app.
- No business logic in Compose UI — screens stay dumb renderers over ViewModels.

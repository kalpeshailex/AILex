# Android Application Specification

## Stack
- Kotlin
- Jetpack Compose
- ViewModel
- Coroutines
- Room
- Retrofit/OkHttp or equivalent
- Kotlin serialization or equivalent
- Android Keystore
- Navigation

## Structure
```text
core/
  network/
  database/
  security/
  storage/
  voice/
  common/
data/
  local/
  remote/
  repository/
domain/
  auth/
  conversation/
  voice/
  legal/
  incident/
  complaint/
  escalation/
features/
  auth/
  home/
  conversation/
  live_situation/
  incidents/
  complaint/
  profile/
  settings/
ui/
  components/
  theme/
  navigation/
```

## Screens
Auth: Splash, Welcome, Mobile, OTP, Name, Language.
Core: Home, Ask AI, Conversation, Live Situation, Result, Save prompt.
Incidents: List, Detail, Timeline, Notes.
Complaint: Draft, Edit, Share.
Escalation: Result, Authority Detail.
Settings: Profile, Privacy, Delete Data, Language, Voice, About, Disclaimer.

## Rules
- No business logic in Compose UI.
- Every screen has loading/error/empty/success states.
- Use a consistent design system.
- Keep voice states explicit: idle, listening, processing, speaking, error.
- Never make voice the only input method.

## Local
Room for incidents/metadata. Keystore for security-sensitive keys. App-private storage/Storage Access Framework for user files.

## Offline
Saved incidents and local preferences remain viewable offline. Current AI/legal retrieval requires connectivity.

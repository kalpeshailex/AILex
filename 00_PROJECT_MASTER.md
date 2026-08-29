# Mumbai Legal Rights AI — Project Master Knowledge

## Purpose
Authoritative project context for the Mumbai Legal Rights AI V1 MVP and its future evolution.

## Product
**Working name:** Mumbai Legal Rights AI  
**Positioning:** Legal First-Aid for Everyday Situations.

**Core promise:** Tell us what happened. Help us understand what may apply, what to do now, what to avoid, and where to go next.

## Platform
- Native Android
- Kotlin + Jetpack Compose
- Supabase PostgreSQL
- pgvector
- Cloudflare Worker or equivalent lightweight API
- Replaceable AI provider
- Local incident/evidence storage in V1

## V1 domains
1. Police
2. Traffic
3. Mumbai Local / Railway
4. Government / RTS
5. Cyber

## V1 interaction
Text and voice are first-class. Voice uses:
`Tap → Speak → Speech-to-text → AI → Text → Text-to-speech`

Continuous real-time voice is deferred.

## V1 authentication
- Mobile number
- OTP
- Name
- Preferred language
- No guest mode

## V1 incident policy
A conversation is not automatically an incident. The user chooses Save Incident or Not Now.

## V1 document policy
OCR, document intelligence and cloud evidence storage are V2. V1 may associate local phone files/photos with incidents without uploading them.

## V1 AI
Logical modules:
- Input Normalizer
- Situation Classifier
- Context Extractor
- Risk Engine
- Legal Knowledge Service
- Authority Service
- Action Planner
- Response Generator
- Citation Validator

Do not build a complex autonomous multi-agent system in V1.

## Legal safety
Never invent laws, sections, penalties, deadlines, procedures, government contacts, judgments or citations.

If insufficiently verified:
> I don't have enough verified information to answer this reliably.

## Source priority
1. `15_DECISIONS.md`
2. `01_PRD.md`
3. architecture/security/database/API files
4. roadmap
5. other project files
6. general model knowledge

If sources conflict, identify the conflict; do not silently reconcile it.

## Product goal
Build a credible, useful, legally cautious MVP that can be tested with real Mumbai users while retaining modular boundaries for future expansion.

# AILex App

**Version:** 1.0  
**Product:** Mumbai Legal Rights AI  
**Platform:** Native Android with Kotlin and Jetpack Compose  
**Primary promise:** Legal first-aid for everyday situations in Mumbai.

## 1. Purpose

This document defines the visual language, screen behavior, navigation, content hierarchy, and interaction rules for the V1 Android app.

The app should feel like a calm, trustworthy help tool for someone who may be stressed, confused, or dealing with an authority right now. It is not a generic chatbot, a lawyer marketplace, or a document-management app.

The design must help users answer four questions quickly:

1. What is happening?
2. What may apply?
3. What should I do now?
4. Where can I go next if I need help?

## 2. Design direction

### Product feeling

- Calm, clear, and reassuring
- Practical rather than corporate
- Trustworthy without pretending to be an official government service
- Serious enough for police, fraud, arrest, and safety situations
- Warm enough for everyday questions
- Fast to scan under stress

### Visual reference from the sample screens

Keep the strongest patterns from the supplied samples:

- White and very light neutral backgrounds
- Dark navy headings with blue as the main action color
- Large, readable titles
- Rounded cards with restrained borders
- Topic tiles with soft tinted backgrounds
- Four-item bottom navigation: Home, Ask AI, Incidents, Me
- Clear icons and short supporting descriptions
- Privacy messaging near sensitive conversation and incident features
- Prominent entry point for an urgent situation

Change or constrain these patterns for the V1 PRD:

- Do not present Premium Plan, subscriptions, or paid access in V1.
- Do not present OCR, document upload, cloud evidence storage, or document AI in V1.
- Do not present BMC, RTO, housing, consumer, employment, or other out-of-scope domains as active features.
- Do not use sample content that implies a feature is available when it is not implemented.
- Use blue, navy, teal, green, amber, and red as the functional palette. Avoid purple/violet as a default brand color.

## 3. Brand and visual system

### 3.1 Color tokens

Use semantic names in the theme rather than hard-coding colors in screens.

#### Primary

- `Primary900`: #102A56 — darkest brand navy, major headings
- `Primary700`: #174A9C — pressed and high-emphasis blue
- `Primary600`: #2166D1 — primary buttons and selected navigation
- `Primary500`: #3B82E8 — links and supportive actions
- `Primary100`: #EAF2FF — selected surfaces
- `Primary050`: #F5F8FF — light brand wash

#### Secondary

- `Secondary700`: #126B69 — calm teal actions and voice support
- `Secondary500`: #2C9C96 — secondary emphasis
- `Secondary100`: #E7F7F5 — teal surfaces

#### Accent

- `Accent700`: #B45309 — warm amber for reminders and pending states
- `Accent500`: #F59E0B — attention indicator
- `Accent100`: #FFF4D6 — warning surface

#### Success

- `Success700`: #18733A — readable success text
- `Success500`: #2E9B52 — success icon and status
- `Success100`: #EAF7EE — success surface

#### Warning

- `Warning700`: #9A5B00 — warning text
- `Warning500`: #E89A17 — warning icon
- `Warning100`: #FFF6E4 — warning surface

#### Error and urgent safety

- `Error700`: #B42318 — readable error text
- `Error500`: #D92D20 — destructive actions and urgent indicators
- `Error100`: #FDECEC — error surface
- `Urgent900`: #8F1717 — urgent headline text
- `Urgent050`: #FFF7F4 — urgent situation background

#### Neutral

- `Neutral950`: #101828 — primary text
- `Neutral800`: #344054 — secondary headings
- `Neutral700`: #475467 — body text
- `Neutral500`: #667085 — supporting text
- `Neutral300`: #D0D5DD — borders and dividers
- `Neutral200`: #EAECF0 — subtle borders
- `Neutral100`: #F2F4F7 — muted surfaces
- `Neutral050`: #F8FAFC — app background
- `White`: #FFFFFF

All text must remain readable against its background in default, pressed, disabled, and selected states. Do not use light gray text on white for essential information.

### 3.2 Typography

Use a clean Android system sans-serif family such as Roboto. Keep the type system compact and predictable.

- Display: 30sp, bold, 120% line height
- Screen title: 26sp, bold, 120% line height
- Section title: 18sp, bold, 120% line height
- Card title: 16sp, bold, 135% line height
- Body: 16sp, regular, 150% line height
- Supporting text: 14sp, regular, 145% line height
- Button label: 15sp, bold
- Caption: 12sp, medium, 140% line height

Use no more than three weights: regular, medium, and bold. Do not communicate meaning through color alone.

### 3.3 Spacing and sizing

Use an 8dp spacing system:

- 4dp: icon-to-label micro spacing
- 8dp: tight grouping
- 16dp: standard internal spacing
- 24dp: section separation
- 32dp: major section separation
- 40dp: page-level breathing room

Recommended dimensions:

- Screen horizontal padding: 20dp on compact phones, 24dp on larger widths
- Standard card radius: 16dp
- Large feature card radius: 20dp
- Small chip radius: 999dp
- Button height: 52dp minimum
- Icon button: 48dp minimum touch target
- Bottom navigation height: 80dp plus system inset
- Minimum touch target: 48dp

### 3.4 Elevation and surfaces

Prefer borders and tonal surfaces over heavy shadows.

- Base screen: `Neutral050` or white
- Standard card: white with 1dp `Neutral200` border
- Elevated action card: white with a very soft shadow, maximum 2dp elevation
- Urgent card: `Urgent050` with `Error100` border
- Information card: `Primary050` with `Primary100` border
- No glassmorphism, neon gradients, or decorative visual noise

## 4. Navigation model

### Primary navigation

Use a persistent bottom navigation bar after authentication:

1. **Home** — start a situation or choose a legal domain
2. **Ask AI** — free-form text and turn-based voice questions
3. **Incidents** — saved situations, drafts, timelines, and notes
4. **Me** — profile, language, privacy, voice preferences, deletion, help, and legal information

The selected item uses `Primary600` and a subtle selected indicator. Unselected items use `Neutral500`. The label remains visible for every item; do not use icon-only navigation.

### Secondary navigation

- Use a top app bar with a back button on child screens.
- Use a clear title and optional one-line context.
- Use bottom sheets for focused choices such as language, save/not save, sorting, and feedback.
- Use full-screen flows for live situations and conversation so the action area remains visible.
- Do not place unrelated editing tasks on the same screen.

### Screen destinations

```text
Welcome
  → Mobile number
  → OTP
  → Name
  → Preferred language
  → Home

Home
  → Live Situation category
  → Ask Legal AI
  → Domain scenario list
  → Incident detail

Ask Legal AI
  → Conversation
  → History
  → Save Incident prompt
  → Citation detail
  → Escalation

Incidents
  → Incident detail
  → Timeline
  → Notes
  → Complaint draft
  → Delete confirmation

Me
  → Profile
  → Language
  → Text size
  → Voice preferences
  → Privacy
  → Data deletion
  → Help and FAQs
  → About
  → Legal disclaimer
```

## 5. Core screen specifications

### 5.1 Welcome and authentication

**Welcome**

- Simple shield-and-scales mark, not an official government emblem
- Headline: “Legal first-aid for everyday situations.”
- Supporting text: “Tell us what happened. Understand what may apply and what to do next.”
- Primary action: “Continue with mobile number”
- Brief privacy note: “Your conversations are private. We never ask for OTPs, PINs, passwords, or CVVs in chat.”
- No guest mode

**Mobile number**

- One focused input
- Country code shown separately and locked to India for V1 unless product scope changes
- Primary action remains disabled until the number is valid
- Show rate-limit and network errors as plain-language messages

**OTP**

- Six-digit input with automatic focus movement
- Clear countdown and “Resend code” action
- Mask the phone number, with an option to change it
- Never show or log the OTP

**Profile setup**

- Name screen: one required name field
- Language screen: English, Hindi, and Marathi choices as supported by the current release
- Explain that language affects explanations and voice preferences; it does not expand legal coverage

### 5.2 Home

The home screen is a practical launchpad, not a dashboard full of metrics.

Top area:

- Greeting using the user’s chosen name
- Location context: “Mumbai / MMR” with a clear scope explanation when tapped
- Notifications icon only for user-created reminders

Primary feature:

- Large urgent card: “Something happening right now?”
- Supporting copy: “Get step-by-step help for your situation.”
- Action: “Start live help”
- Include three short reassurance cues: “Quick guidance”, “Know your rights”, “Stay safe”
- Use a calm red/peach treatment, not an alarmist full-red screen

Domain grid:

- Police
- Traffic
- Mumbai Local / Railway
- Government / RTS
- Cyber

Each tile includes an icon, domain name, and one short description. The tile opens a domain-specific scenario picker.

Below the grid:

- Recent saved incidents, maximum two or three items
- Empty state: “Saved situations will appear here when you choose to save one.”
- Privacy reassurance: “You decide what becomes an incident.”

Do not show unsupported domain tiles in V1. If future domains need to be previewed, label them “Coming later” and keep them non-interactive.

### 5.3 Ask Legal AI

Header:

- Title: “Ask Legal AI”
- Subtitle: “Clear, practical guidance for your situation.”
- History action

Intro panel:

- “Ask anything about a situation involving police, traffic, railways, government services, or cyber incidents.”
- Explain that the assistant may ask only the facts that change the answer.

Suggested prompts:

- “Police ne mujhe roka hai, kya karu?”
- “Can traffic police demand cash for a challan?”
- “What should I do after a UPI fraud?”
- “What if a government application is delayed?”

Input composer:

- Multiline text field with placeholder “Describe what happened…”
- Microphone button for turn-based voice input
- Send button enabled only when text exists
- Visible privacy note below the composer
- Keyboard must not cover the composer or last message

The sample screen’s “Understand a notice” tile should not be enabled unless V2 document handling has been implemented. In V1, replace it with “Understand a procedure” or another text-only action.

### 5.4 Conversation

Message layout:

- User messages aligned to the end with `Primary600` surface and white text
- Assistant messages aligned to the start on white or `Neutral050`
- Do not use long uninterrupted paragraphs
- Break answers into headings, bullets, numbered actions, and expandable legal basis sections

Every important answer should support this order when applicable:

1. Situation
2. What may apply
3. Your position
4. Your rights
5. Authority powers
6. What to do now
7. Avoid
8. Preserve
9. Legal basis
10. Escalation

Priority behavior:

- “What to do now” appears before detailed explanation for live or high-risk situations.
- High-risk answers display a visible “Safety first” banner.
- Unverified information displays “I don’t have enough verified information to answer this reliably.”
- Unsupported location displays a clear coverage message instead of silently applying Mumbai rules.

Actions beneath an answer:

- Play response
- Stop speech
- Save this situation
- Not now
- View legal basis
- View escalation
- Was this useful? Yes / No / Something is incorrect

### 5.5 Voice interaction

Voice is turn-based, never continuous in V1.

States:

1. Ready — microphone button
2. Listening — animated waveform or pulsing ring, with “Listening…”
3. Stop — large stop action
4. Transcribing — “Understanding what you said…”
5. Review — show recognized text before sending when confidence is low
6. Responding — text answer appears first
7. Speaking — replay/pause/stop controls
8. Failed — plain-language retry and text fallback

The app must not permanently store voice recordings by default. Show a short explanation the first time voice is used.

### 5.6 Live Situation

This is the primary V1 flow.

**Start screen**

- Explain: “I’ll ask only the details that may change the guidance.”
- Ask whether there is immediate danger before collecting routine facts
- Provide “I am safe” and “I need urgent help” paths where appropriate

**Category screen**

Use five supported domains:

- Police
- Traffic
- Mumbai Local / Railway
- Government / RTS
- Cyber

**Question screen**

- One material question at a time
- Progress indicator such as “Question 2 of 4” only when the number is known
- Quick options for common answers plus “Something else” and “I’m not sure”
- Allow text input when a fixed choice is insufficient
- Avoid asking for unnecessary identity details

**Result screen**

- Put the immediate action plan first
- Separate rights, obligations, and authority powers into distinct sections
- Show uncertainty next to the relevant claim
- Show verified citations in a collapsed “Legal basis” area
- Provide a clear escalation action
- Ask “Save this situation to My Incidents?” at the natural end of the flow

### 5.7 My Incidents

Header:

- Title: “My Incidents”
- Subtitle: “Saved situations, notes, and drafts.”
- Search and sort actions
- “New incident” starts the live situation flow; it does not create a blank record unless the product later requires that behavior

Filters:

- All
- Active
- Resolved
- Drafts

Do not show Evidence counts or document-vault language in V1. Local evidence references may appear inside an incident, but V1 does not upload or manage cloud files.

Incident row:

- Domain icon and tint
- User-provided or generated title
- Date and location if provided
- Status chip: Active, Resolved, or Draft
- One or two short tags
- Chevron for detail

Empty state:

- “You have not saved an incident yet.”
- “A conversation is not saved automatically.”
- Action: “Ask Legal AI”

### 5.8 Incident detail

Sections:

- Summary
- What happened
- Key facts
- Timeline
- Recommended actions
- Actions taken
- Reference or complaint number
- Notes
- Local evidence references, if the user associated any
- Complaint draft
- Escalation

Actions:

- Edit notes
- Add timeline event
- Create complaint draft
- Copy/share draft
- Delete incident

Deletion must use a confirmation dialog that explains the saved incident and its local references will be removed. Do not use a destructive action without confirmation.

### 5.9 Complaint draft

Clearly label the output:

> “Draft only — review it before sharing. The app does not file complaints automatically.”

Sections:

- Recipient
- Subject
- Background
- Facts
- Requested action
- Supporting information
- Optional contact details

Actions:

- Edit
- Copy
- Share
- Save locally

Do not insert unsupported legal accusations or automatically state that an authority acted illegally.

### 5.10 Me and settings

Keep the sample screen’s grouped settings structure, but make it V1-accurate.

Profile:

- Name
- Mobile number, masked
- Preferred language

Preferences:

- Language
- Text size: Standard / Large / Extra large
- Theme: Light / System, with dark theme only if fully tested
- Voice preferences: auto-play, speech rate, replay
- Reminder preferences

Privacy and data:

- Privacy settings
- Download/export my data where supported
- Delete my data
- App lock only if implemented end-to-end; otherwise omit it
- Explain local incident storage and that local data may be lost after uninstall or clear-data

Support and information:

- Help and FAQs
- How Legal AI works
- About Legal AI
- Terms and conditions
- Privacy policy
- Legal disclaimer
- Report an incorrect answer

Do not show “Premium Plan”, “Manage Plan”, or “Contact Support” unless those product flows are explicitly implemented.

## 6. Content and legal safety patterns

### Plain-language rules

- Lead with the action, not the legal theory.
- Use short paragraphs and numbered steps.
- Explain legal terms the first time they appear.
- Prefer “may”, “can depend on”, and “based on what you told me” where facts are incomplete.
- Never promise an outcome.
- Never tell users to confront, retaliate, obstruct, evade lawful enforcement, or secretly surveil someone.
- Never request OTPs, PINs, CVVs, passwords, or full authentication credentials.

### Citation presentation

A citation must be visibly tied to the claim it supports.

Collapsed state:

- “Legal basis — 2 verified sources”

Expanded state:

- Source title
- Issuing authority
- Jurisdiction
- Verification status
- Last verified date
- Relevant excerpt or explanation
- Official source action when available

Never display fabricated source titles, section numbers, fines, deadlines, phone numbers, or URLs.

### High-risk content

For arrest, violence, sexual blackmail, serious threats, ongoing financial fraud, or immediate danger:

- Use the urgent safety surface
- Put safety and immediate mitigation first
- Keep the answer concise
- Recommend verified official or professional escalation
- Avoid unsupported conclusions about legality
- Keep confrontation language out of the interface

## 7. Component inventory

Build reusable Compose UI elements for:

- App top bar
- Bottom navigation
- Primary, secondary, and outlined buttons
- Urgent action card
- Domain tile
- Incident row
- Status chip
- Tag chip
- Section heading
- Information banner
- Safety banner
- Citation accordion
- Voice control bar
- Message bubble
- Composer
- Loading skeleton
- Empty state
- Error state
- Confirmation dialog
- Bottom sheet selector
- Feedback row

Each component must support loading, enabled, disabled, pressed, and error states where relevant.

## 8. Motion and feedback

Use restrained motion to clarify progress:

- 150–200ms for button and card state changes
- 250–350ms for screen transitions and bottom sheets
- Subtle fade/slide for new assistant messages
- Gentle pulse only while listening or indicating immediate attention
- No looping decorative animation on the home screen
- Respect Android reduced-motion preferences where available

Every tap target must provide visual feedback. Every network action must show a loading state and prevent accidental duplicate submission.

## 9. Accessibility and responsive behavior

- Support Android font scaling through at least 200% without clipped text.
- Use semantic labels for icons and voice controls.
- Maintain 48dp minimum touch targets.
- Never rely on color alone for status, risk, or domain.
- Keep contrast readable for all text and controls.
- Support TalkBack reading order: title, context, primary action, content, secondary action.
- Use scrollable content rather than fixed-height cards.
- On small phones, stack domain tiles into one column or two compact columns.
- On tablets and landscape, constrain reading width to approximately 600–720dp and keep key actions visible.
- Keep bottom navigation visible on main destinations, but allow the keyboard and system insets to be handled correctly.

## 10. Required states for every major screen

### Loading

Use skeletons for lists and a short progress message for AI work. Do not show a blank screen.

### Empty

Explain what the user will see after taking the relevant action and offer one clear next step.

### Error

Use plain language, for example:

- “We could not connect right now. Your saved incidents are still available on this device.”
- “Voice recognition did not catch that. Try again or type your question.”
- “This situation needs information we could not verify yet.”

Never show raw backend errors, stack traces, or internal identifiers.

### Offline

Allow access to locally saved incidents and preferences. For network-required features, say what needs a connection and provide a retry action.

### Success

Confirm meaningful actions with a visible message:

- Incident saved
- Draft copied
- Draft shared
- Note added
- Incident deleted

## 11. Privacy communication

Use short, accurate messages near sensitive actions:

- “A conversation is not saved as an incident unless you choose Save Incident.”
- “Voice recordings are not stored by default.”
- “V1 does not upload your local evidence files.”
- “Do not share OTPs, PINs, passwords, CVVs, or UPI PINs in chat.”
- “The assistant provides legal first-aid, not guaranteed legal advice or representation.”

Do not claim encryption, device-only storage, or secure deletion unless the implemented Android and backend behavior supports the claim.

## 12. V1 scope guardrails for design work

Before adding a screen, tile, menu item, or action, ask:

> Does this help a Mumbai citizen understand and safely act on a supported legal situation right now?

If not, defer it.

The following are not V1 design commitments:

- OCR or document scanning
- Cloud evidence vault
- Automatic complaint filing
- Lawyer marketplace
- BMC, RTO, housing, consumer, or employment flows
- Nationwide coverage
- Continuous real-time voice
- Permanent voice recording
- Legal outcome prediction
- Court representation

## 13. Android implementation guidance for Claude

- Keep screen UI, domain rules, data access, and network communication separate.
- Use a single source of truth for theme tokens, navigation routes, and supported domains.
- Keep dynamic legal claims out of static UI strings; render them from verified backend responses.
- Model every remote action with loading, success, empty, and error states.
- Keep voice as an interface layer that produces normalized text for the conversation flow.
- Keep incident saving explicit and user-controlled.
- Make the Android client communicate with the backend rather than directly with an AI provider.
- Do not add a feature simply because it appears in a sample mockup if it conflicts with the PRD.
- Build and inspect each screen on a compact Android phone, a larger phone, and a tablet layout.

## 14. Acceptance checklist

The design is ready for implementation when:

- The authentication path is clear from first open to Home.
- Home exposes Live Situation, Ask AI, all five V1 domains, and saved incidents.
- A stressed user can reach immediate guidance in one prominent action.
- Ask AI supports text and turn-based voice without hiding the text fallback.
- Live Situation asks only material questions and presents an action plan before detail.
- Rights, obligations, authority powers, citations, and escalation are visually distinct.
- Saving an incident is always a choice.
- Incidents can be viewed, edited with notes, drafted into a complaint, and deleted.
- Out-of-scope sample features are not presented as working V1 functionality.
- Every primary screen has loading, empty, offline, error, and success behavior.
- Text remains readable with large Android font settings.
- Privacy and legal limitations are visible without overwhelming the user.

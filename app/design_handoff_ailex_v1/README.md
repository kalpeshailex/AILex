# Handoff: AILex V1 — Mumbai Legal Rights AI (Android / Compose)

## Overview

A complete clickable prototype of the AILex V1 MVP: onboarding, home, the Live Situation
flow, Ask Legal AI, My Incidents, complaint drafting, escalation and settings —
21 screens in all. This package documents it so it can be implemented in the existing
`com.example.ailex` Android app.

## About the design files

`AILex Prototype.dc.html` in this folder is a **design reference built in HTML**. It is a
prototype of the intended look and behaviour, not production code and not something to
port line by line. The task is to **rebuild these screens as Jetpack Compose UI** inside
the existing app, using Material 3, the app's own theme, and the package structure that
is already scaffolded.

`AILex Prototype (offline).html` is a self-contained copy of the same prototype — open it
in any browser with no network to click through the real flows.

Do not add a WebView. Do not translate the HTML/CSS. Read the prototype for layout,
copy, colour and behaviour, then write idiomatic Compose.

## Fidelity

**High fidelity.** Colours, type sizes, weights, spacing, corner radii and all copy are
final and specified below. Match them. Every hex value and dp value in this document is
the intended value, not an approximation.

Two deliberate constraints:

1. **Risk colour semantics carry meaning and must not be normalised into one brand
   colour.** Red means danger or an unlawful demand, amber means caution or a pending
   obligation, green means done or verified, teal means preserve/evidence. A user reads
   these under stress.
2. **Minimum touch target 48dp**, minimum body text 14sp. The app is used one-handed
   while someone is being stopped by an officer.

---

## Target codebase

- **Package**: `com.example.ailex`, `minSdk` 24, `targetSdk` 36
- **UI**: Jetpack Compose with Material 3 (`androidx.compose.material3`)
- **Navigation**: `androidx.navigation:navigation-compose` (already a dependency)
- **State**: `androidx.lifecycle.viewmodel.compose` + `collectAsStateWithLifecycle`
- **Coroutines**: `kotlinx.coroutines.android`

The scaffold already contains empty packages that map one-to-one onto this design. Put
each screen in the package that already exists for it:

| Prototype screen | Target package |
| --- | --- |
| Welcome, Mobile number, OTP, Name, Language | `features/auth/` |
| Home | `features/home/` |
| Safety check, Urgent help, Category, Questions, Type an answer, Result | `features/live_situation/` |
| Ask Legal AI, Voice turn, Conversation | `features/conversation/` |
| My Incidents, Incident detail | `features/incidents/` |
| Complaint draft | `features/complaint/` |
| Escalation | `features/escalation/` |
| Me, Notifications, Understanding AILex, Privacy, Delete my data | `features/settings/`, `features/profile/` |
| Shared cards, chips, sheets, section rows | `ui/components/` |
| Colour, type, shape tokens | `ui/theme/` |
| NavHost and bottom bar | `ui/navigation/` |

Domain interfaces already have homes in `domain/auth`, `domain/incident`,
`domain/complaint`, `domain/escalation`, `domain/legal`, `domain/conversation`,
`domain/voice`. The prototype's hardcoded content should become repository fakes behind
those interfaces so the UI can be built and previewed before the backend exists.

---

## Design tokens

Put these in `ui/theme/Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`.

### Colour

```kotlin
// Brand / primary
val Navy900        = Color(0xFF102A56)  // headings, brand
val Navy800        = Color(0xFF0B2545)  // voice screen background
val Navy700        = Color(0xFF174A9C)  // pressed primary, active nav label
val Blue600        = Color(0xFF2166D1)  // primary action, links, accents
val Blue100        = Color(0xFFEAF2FF)  // primary tint / active nav pill
val Blue050        = Color(0xFFF5F8FF)  // tinted card background
val BlueBorder     = Color(0xFFD6E4FB)  // border on tinted cards
val NavyLine       = Color(0xFF1E3E68)  // borders on the dark voice screen
val NavyCard       = Color(0xFF0F2F57)  // card on the dark voice screen
val NavyMuted      = Color(0xFF7FA6D4)  // muted text on dark
val NavyBody       = Color(0xFFB9CEE8)  // body text on dark

// Neutrals
val Ink900         = Color(0xFF101828)  // primary text
val Ink700         = Color(0xFF344054)  // body text
val Ink600         = Color(0xFF475467)  // secondary body
val Ink500         = Color(0xFF667085)  // meta text
val Ink400         = Color(0xFF98A2B3)  // disabled text, faint meta
val Line300        = Color(0xFFD0D5DD)  // input borders, strong dividers
val Line200        = Color(0xFFEAECF0)  // card borders, standard dividers
val Line100        = Color(0xFFF2F4F7)  // chip fill, row dividers
val Surface        = Color(0xFFFFFFFF)  // cards, sheets, app bars
val Background     = Color(0xFFF8FAFC)  // screen background
val Canvas         = Color(0xFFECEEF1)  // desk background behind the phone frame

// Risk semantics — DO NOT COLLAPSE THESE
val Danger700      = Color(0xFF8F1717)  // danger heading text
val Danger600      = Color(0xFFB42318)  // danger icon + label, pressed danger button
val Danger500      = Color(0xFFD92D20)  // danger button fill
val Danger100      = Color(0xFFFDECEC)  // danger tint
val Danger050      = Color(0xFFFFF7F4)  // danger screen background
val DangerBorder   = Color(0xFFF2B8AE)  // danger border, strong
val DangerBorderLt = Color(0xFFFBD9D2)  // danger border, soft

val Caution700     = Color(0xFF9A5B00)  // caution text + icon
val Caution500     = Color(0xFFE89A17)  // caution timeline dot
val Caution100     = Color(0xFFFFF6E4)  // caution tint
val CautionBorder  = Color(0xFFFFE7B8)

val Success700     = Color(0xFF18733A)  // success text
val Success500     = Color(0xFF2E9B52)  // success icon, checked step
val Success100     = Color(0xFFEAF7EE)  // success tint
val SuccessBorder  = Color(0xFFC7E9D3)
val SuccessOnDark  = Color(0xFF6EE7A8)  // toast check icon

val Preserve700    = Color(0xFF126B69)  // evidence / preserve text + icon
val Preserve500    = Color(0xFF2C9C96)  // voice orb, positive check
val Preserve100    = Color(0xFFE7F7F5)  // evidence tint
val PreserveBorder = Color(0xFFCFEEEB)
```

Domain accent pairs, used on category tiles, incident rows and question chips:

| Domain | Tint | Ink | Icon (Material Symbols name) |
| --- | --- | --- | --- |
| Police | `#EAF2FF` | `#2166D1` | `local_police` |
| Traffic | `#FFF6E4` | `#9A5B00` | `traffic` |
| Mumbai Local | `#E7F7F5` | `#126B69` | `train` |
| Government / RTS | `#F2F4F7` | `#344054` | `account_balance` |
| Cyber | `#FDECEC` | `#B42318` | `security` |
| Something else | `#F2F4F7` | `#475467` | `more_horiz` |

### Typography

Roboto throughout (the platform default — no font asset needed). Map to Material 3
`Typography`:

| Role | Size / line height | Weight | Letter spacing | Used for |
| --- | --- | --- | --- | --- |
| displaySmall | 30 / 36 | 700 | -0.5 | Welcome headline |
| headlineMedium | 26 / 31 | 700 | -0.3 | Onboarding + flow headings |
| headlineSmall | 24 / 30 | 700 | -0.2 | Question text, result section headings |
| titleLarge | 22 / 26 | 700 | -0.3 | Screen titles ("Hello, Rohan", "My Incidents") |
| titleMedium | 19 / 23 | 700 | 0 | "What to do now" |
| titleSmall | 16 / 20 | 700 | 0 | App bar titles, card titles |
| bodyLarge | 15 / 23 | 400 | 0 | Card body, option labels |
| bodyMedium | 14 / 21 | 400 | 0 | Standard body, action steps (500 weight) |
| bodySmall | 13 / 19 | 400 | 0 | Section item text, meta |
| labelLarge | 15 / 20 | 700 | 0 | Buttons |
| labelMedium | 12.5 / 17 | 500 | 0 | Chips, filters, inline actions |
| labelSmall | 10.5 / 11 | 500 | +0.09em, uppercase | Section kickers ("WHAT I UNDERSTAND") |

### Shape, spacing, elevation

```kotlin
// Shape
val RadiusSheet   = 22.dp  // bottom sheet top corners
val RadiusCardLg  = 20.dp  // live-help hero card
val RadiusCard    = 16.dp  // standard card, list row, tinted panel
val RadiusCardSm  = 14.dp  // nested card, banner, compact row
val RadiusField   = 12.dp  // input, button, small panel
val RadiusChip    = 11.dp  // icon tile
val RadiusPill    = 999.dp // chips, filters, badges, nav pill

// Spacing — 4dp base
val Space1 = 4.dp;  val Space2 = 8.dp;  val Space3 = 12.dp
val Space4 = 16.dp; val Space5 = 20.dp; val Space6 = 24.dp; val Space8 = 32.dp

// Screen padding: horizontal 20.dp, top 14–16.dp, bottom 24.dp
// Card padding: 14–15.dp; large hero card 20.dp; bottom sheet 20.dp horizontal
// Gap between stacked cards: 8–10.dp
// Control heights: primary button 52.dp, secondary 48.dp, input 56.dp,
//                  compact input 50.dp, icon button 44.dp, nav bar 64.dp
```

Cards are `Surface` white with a 1dp `Line200` border and **no elevation**. Elevation is
used only on bottom sheets and the app bar shadow. Borders, not shadows, do the work.

---

## Navigation structure

Single `NavHost`. Four top-level destinations in a `NavigationBar`, everything else
pushed on top with the bar hidden.

```
home            Home                    (bar visible)
ask             Ask Legal AI            (bar visible)
incidents       My Incidents            (bar visible)
me              Me                      (bar visible)

auth/welcome    auth/phone    auth/otp    auth/name    auth/language
live/safety     live/urgent   live/category
live/question/{index}         live/freetext/{index}    live/result
ask/voice       ask/conversation/{id}
incidents/{id}  incidents/{id}/complaint
escalation?domain={domain}&from={route}
notifications   help?topic={how|limits|faq}
settings/privacy             settings/delete
```

Bottom bar items, in order: Home (`home`), Ask AI (`forum`), Incidents (`folder_open`),
Me (`person`). Active item: icon and label in `Navy700` with a `Blue100` pill behind the
icon (3dp vertical, 16dp horizontal padding, fully rounded). Inactive: `Ink500`, no pill.
Label 11.5sp / 500. Bar has a 1dp top border in `Line200`, 8dp top padding, 10dp bottom.

Auth is a separate graph shown when no session exists. Completing `auth/language`
clears the auth graph and lands on `home`.

---

## Screens

Below, "card" means white surface, 1dp `Line200` border, `RadiusCard`, 14–15dp padding
unless stated.

### 1. Welcome — `features/auth/WelcomeScreen.kt`

White background, 44dp top padding, 24dp horizontal, 24dp bottom.

- **Brand mark**: 56×64dp hexagon in `Navy900` (six-point polygon, flat top and bottom
  edges clipped at 16% and 62% height), white `balance` icon 28dp centred. 32dp bottom
  margin. Use a `Canvas` or vector drawable — do not attempt a CSS clip-path equivalent.
- **Headline**: "Legal first-aid for everyday situations." — displaySmall, `Navy900`,
  14dp bottom margin.
- **Subhead**: "Tell us what happened. Understand what may apply and what to do next." —
  16sp / 24, `Ink600`.
- **Spacer**, weight 1.
- **Points panel**: `Background` fill, 1dp `Line200`, `RadiusCard`, 14dp padding, 10dp
  gaps, 20dp bottom margin. Three rows, each an 18dp `check_circle` in `Preserve500`
  plus 14sp / 20 `Ink700` text:
  - "Practical steps first, legal theory second."
  - "Rights, obligations and authority powers kept separate."
  - "Nothing is saved as an incident unless you say so."
- **Primary button**: "Continue with mobile number", full width, 52dp, `Blue600`,
  `RadiusField`, white labelLarge. Pressed `Navy700`.
- **Footnote**: "We never ask for OTPs, PINs, passwords or CVVs in chat." — 12sp / 17,
  `Ink500`, centred, 14dp top margin.

### 2. Mobile number — `features/auth/PhoneScreen.kt`

Back arrow (44dp icon button, `arrow_back`, `Ink700`), 12dp bottom margin.

- **Heading**: "What's your mobile number?" headlineMedium `Navy900`.
- **Subhead**: "We'll send a six-digit code to verify it. This is the only number we
  store." — bodyMedium `Ink500`, 28dp bottom margin.
- **Row**, 10dp gap: fixed "+91" box (56dp high, 14dp horizontal padding, `Background`
  fill, 1dp `Line300`, `RadiusField`, 16sp / 500 `Ink700`) then the number field (56dp,
  17sp, 0.5px letter spacing, placeholder "98765 43210", `keyboardType = Number`).
- **Focus state on any input**: border `Blue600` plus a 3dp `Blue100` ring.
- **Validation**: exactly 10 digits, non-digits stripped on input, capped at 10. Button
  disabled (`Line200` fill, `Ink400` label) until valid.
- **Primary button**: "Send code".
- **Footnote**: "India only in this release. No Aadhaar, address or date of birth is
  collected."

### 3. OTP — `features/auth/OtpScreen.kt`

- **Heading**: "Enter the code".
- **Subhead**: "Sent to +91 98XXX XX210 · change" — the number masked as first two
  digits, `XXX XX`, last three digits; "change" is an underlined `Blue600` text button
  returning to the phone screen.
- **Six cells**, equal width (`weight(1f)`), 60dp high, 8dp gaps, `Background` fill,
  1dp `Line300`, `RadiusField`, digit centred at 22sp / 700. Real implementation should
  use one hidden field driving six cells, with SMS autofill
  (`KeyboardType.NumberPassword` + autofill hints).
- **Row below**, 16dp top: "Resend in 0:24" (13sp `Ink500`) left, a demo autofill chip
  right. **Drop the demo chip in the real app** — it exists only so the prototype can be
  clicked through. Replace with a "Resend code" text button, enabled at 0:00.
- **Primary button**: "Verify", enabled at 6 digits.

### 4. Name — `features/auth/NameScreen.kt`

- **Verified row**: 18dp `verified` icon in `Success500` plus "Number verified" 13sp / 500
  `Success700`. 20dp bottom margin.
- **Heading**: "What should we call you?"
- **Subhead**: "Used only to address you in the app."
- **Input**: 56dp, placeholder "Your name", capped at 40 characters.
- **Primary button**: "Continue", enabled when trimmed length > 1.

### 5. Language — `features/auth/LanguageScreen.kt`

- **Heading**: "Preferred language"
- **Subhead**: "Affects explanations and voice. It does not change which laws are
  covered." — 24dp bottom margin.
- **Three option rows**, 10dp gaps, each 16dp padding, `RadiusCardSm`, 12dp gap between
  icon and text: 22dp `translate` icon in `Blue600`, then title (16sp / 700 `Ink900`)
  over native name (13sp `Ink500`, 2dp top).
  - English / "Recommended for this release"
  - Hindi / "हिंदी"
  - Marathi / "मराठी"
- **Selected**: border `Blue600`, fill `Blue050`. Unselected: border `Line200`, white.
- **Primary button**: "Finish setup".

### 6. Home — `features/home/HomeScreen.kt`

Scrollable, `Background`, 20dp horizontal, 14dp top.

- **Header row**: "Hello, {firstName}" titleLarge `Navy900`; below it a text button with
  a 15dp `place` icon, "Mumbai / MMR", and a 15dp `info` icon in `Ink500` opening the
  coverage sheet. Right: 44dp bell icon button, white, 1dp `Line200`, `RadiusField`,
  with an unread badge — 16dp min width, 16dp high, `Danger500` fill, white 10sp / 700,
  positioned 6dp from the top-right.
- **Live help card**: `Danger050` fill, 1dp `DangerBorderLt`, `RadiusCardLg`, 20dp
  padding, 24dp bottom margin.
  - Kicker row: 20dp `emergency_home` in `Danger600` plus "LIVE HELP" labelSmall
    `Danger600`.
  - Title: "Something happening right now?" 22sp / 27 / 700 `Danger700`.
  - Body: "Get step-by-step help for your situation." bodyMedium `Ink600`.
  - Button: "Start live help" with a 20dp `bolt` icon, full width, 52dp, `Danger500`,
    white labelLarge. Pressed `Danger600`.
  - Three equal cues below, 6dp gaps, `Danger100` pills, 11.5sp / 500 `Danger700`,
    centred, 6dp vertical padding: "Quick guidance", "Know your rights", "Stay safe".
- **"Browse by area"** titleMedium with "5 areas covered" 12sp `Ink500` right-aligned.
- **Domain grid**: two columns, 10dp gaps. Five tiles; the fifth (Cyber) spans both
  columns. Each tile is a card, 14dp padding, 12dp gap: 40dp icon tile (`RadiusChip`,
  domain tint fill, 21dp icon in domain ink), then label (15sp / 700 `Ink900`) over blurb
  (12sp / 16 `Ink500`). Pressed: border `BlueBorder`, fill `Blue050`.
  - Police / "Stops, notices, FIRs, arrest"
  - Traffic / "Challans, towing, documents"
  - Mumbai Local / "Tickets, TC, RPF, theft"
  - Government / RTS / "Delays, refusals, appeals"
  - Cyber / "Fraud, hacking, harassment"
- **Ask Legal AI row**: `Blue050` fill, 1dp `BlueBorder`, `RadiusCard`, 15dp padding,
  22dp `forum` icon in `Blue600`, "Ask Legal AI" (15sp / 700 `Navy900`) over "Type or
  speak a question. English, हिंदी or मराठी." (12.5sp / 17 `Ink600`), trailing
  `chevron_right`. 24dp bottom margin.
- **"Recent incidents"** titleMedium with a "See all" `Blue600` text button. Two most
  recent incidents as compact rows: 34dp icon tile, title (14sp / 500, single line with
  ellipsis) over "{date} · {domain} · {status}" (12sp `Ink500`), trailing chevron.
  Empty state: dashed `Line300` border, `RadiusCardSm`, 18dp padding, centred 13.5sp
  `Ink500` — "Nothing saved yet. Situations appear here only when you choose to save
  them."
- **Privacy footnote**: 15dp `lock` icon in `Preserve500` plus "You decide what becomes an
  incident. Conversations are not saved on their own." 12sp / 17 `Ink500`.

### 7. Live Situation — safety check — `features/live_situation/SafetyScreen.kt`

`Danger050` background. Top row: 44dp `close` button left, "Live help" 13sp / 500
`Danger700` centred, 44dp spacer right.

- **Heading**: "First — is anyone in immediate danger?" headlineMedium `Danger700`.
- **Body**: "I'll only ask the details that may change the guidance. Answer this one
  first so I can put safety ahead of paperwork." 15sp / 23 `Ink600`, 26dp bottom.
- **Two options**, 12dp gap, 18dp padding, `RadiusCard`, 14dp icon gap:
  - "I am safe right now" / "Continue with the situation" — white, 1dp `Line200`,
    24dp `shield_with_heart` in `Success500`. → category.
  - "I need urgent help" / "Show emergency numbers first" — `Danger100` fill, 1dp
    `DangerBorder`, 24dp `sos` in `Danger600`, title `Danger700`, subtitle `Danger600`.
    → urgent.
- **Footer note**: card, 14dp padding, 19dp `info` icon `Ink500` plus "This is legal
  first-aid, not legal representation. In an emergency, call 112 before using the app."

### 8. Live Situation — urgent help — `features/live_situation/UrgentScreen.kt`

`Danger050` background, back arrow.

- **Safety banner**: `Danger100` fill, 1dp `DangerBorder`, `RadiusCard`, 16dp padding.
  Kicker: 20dp `health_and_safety` icon plus "SAFETY FIRST" 13sp / 700 uppercase
  `Danger600`. Body 14sp / 21 `Danger700`: "Get to a safe, public, well-lit place if you
  can. Do not physically resist or argue. Call for help before working out the legal
  position."
- **Four emergency rows**, 10dp gaps, card, 14dp padding: 38dp `Danger100` icon tile,
  label (15sp / 700) over blurb (12.5sp `Ink500`), number right-aligned 18sp / 700
  `Danger600`. Each row must dial via `Intent.ACTION_DIAL` — do not auto-place the call.
  - Emergency response / "Police, fire, ambulance" / **112** / `emergency`
  - Police control room / "Mumbai Police" / **100** / `local_police`
  - Women's helpline / "24x7 support" / **1091** / `support_agent`
  - Cyber fraud reporting / "National helpline" / **1930** / `security`
- **Primary button**: "I'm safe now — continue".

### 9. Live Situation — category — `features/live_situation/CategoryScreen.kt`

- **Heading**: "What kind of situation is it?"
- **Subhead**: "Pick the closest one. You can correct me later."
- **Six rows** (five domains plus "Something else" / "Describe it in your own words"),
  10dp gaps, card, 15dp padding, 13dp gap: 40dp domain icon tile, label (16sp / 700) over
  blurb (12.5sp / 18 `Ink500`), trailing chevron. Pressed: border `BlueBorder`, fill
  `Blue050`. "Something else" routes into the free-text composer.

### 10. Live Situation — questions — `features/live_situation/QuestionScreen.kt`

- **Progress row**: back arrow, then a 6dp track (`Line200`, fully rounded) with a
  `Blue600` fill at `(index + 1) / count`, then "Question 1 of 3" 12sp / 500 `Ink500`.
- **Domain chip**: pill, domain tint fill, 6dp / 12dp padding, 16dp domain icon plus
  label 12.5sp / 500 in domain ink. Self-aligned start, 16dp bottom.
- **Question**: headlineSmall `Ink900`.
- **Why we ask**: 13.5sp / 20 `Ink500`, 20dp bottom.
- **Options**: 9dp gaps, card, 15dp padding, a 20dp unfilled circle (2dp `Line300`
  border) plus 15sp / 20 `Ink900` label. Pressed: border `Blue600`, fill `Blue050`.
  Selecting advances; the last option opens the result.
- **"Type an answer instead"**: `Blue600` text button, 13.5sp / 500, start-aligned.
- **Privacy note** pinned to the bottom: `Background` panel, 1dp `Line200`, `RadiusField`,
  12dp padding, 17dp `visibility_off` icon plus "I won't ask for your name, licence
  number or vehicle number unless it changes the answer."

**Question sets** — three per domain, each with the question, the reason, and the
options. These are content, not UI; put them behind `domain/legal`.

*Traffic*
1. "Has a challan actually been issued?" — "Whether a challan exists changes what you
   should ask for next." — Yes, an e-challan or paper challan / No, nothing has been
   issued / I am not sure
2. "What is the officer asking you for?" — "A cash demand is treated very differently
   from a document check." — Cash on the spot / Vehicle documents / Both / Something else
3. "Where are you right now?" — "Jurisdiction decides which procedure and which
   grievance route applies." — Mumbai City / Mumbai Suburban / Thane / Navi Mumbai /
   Somewhere else

*Police*
1. "What has the officer told you so far?" — "A stop, a notice and an arrest each follow
   a different procedure." — They stopped me for questioning / They asked me to come to
   the station / They served a written notice / They said I am being arrested
2. "Are you at a police station or elsewhere?" — "Where this is happening affects what
   should be recorded." — On the street / At a police station / At my home or workplace
3. "Is anyone with you?" — "Having a witness or informing a family member matters for a
   record." — Yes, someone is with me / No, I am alone / I could call someone

*Mumbai Local*
1. "What is the ticket position?" — "The applicable rule depends on which ticket you
   hold." — No ticket / Wrong class or wrong destination / Valid ticket, dispute anyway /
   Season pass or UTS issue
2. "What is the TC or RPF asking for?" — "A receipted penalty and a cash demand are not
   the same thing." — Cash without a receipt / A penalty with a receipt / My identity
   documents / Nothing yet
3. "Which line are you on?" — "Central, Western and Harbour lines are administered
   separately." — Central / Western / Harbour / Trans-Harbour / Not sure

*Government / RTS*
1. "What happened with your application?" — "RTS, RTI and appeal routes each have their
   own trigger." — It is delayed past the promised date / It was rejected / They refused
   to accept it / They asked for extra documents
2. "Do you have an acknowledgement or receipt number?" — "Almost every escalation route
   needs this reference." — Yes / No / I applied online only
3. "Was any unofficial payment mentioned?" — "This changes the escalation route
   entirely." — Yes / No / It was implied

*Cyber*
1. "What has happened?" — "Money moving out needs a different first hour than a hacked
   account." — Money left my account / My account was hacked / I am being blackmailed or
   threatened / My photos or data were misused
2. "When did it happen?" — "The first hours matter most for stopping or reversing a
   transfer." — Within the last hour / Today / Within the last few days / Longer ago
3. "Have you told your bank or the platform yet?" — "This tells me whether mitigation has
   already started." — Yes / No / I tried but could not reach them

**Location question index** (used to label a saved incident — see State Management):
Traffic question 3, Police question 2, Mumbai Local question 3. Government and Cyber have
no location question, and the saved incident must omit the location segment entirely
rather than substituting an unrelated answer.

### 11. Live Situation — type an answer — `features/live_situation/FreeTextScreen.kt`

Back arrow, domain chip, the current question at 21sp / 28 / 700, then "Answer in your own
words. English, हिंदी or मराठी."

- **Textarea**: min 130dp, `RadiusCardSm`, 1dp `Line300`, 13dp padding, 15sp / 23.
- **"Speak it instead"**: `Preserve100` fill, 1dp `PreserveBorder`, `RadiusField`, 10dp /
  14dp padding, 18dp `mic` icon, `Preserve700` label. Opens the voice screen.
- **Privacy note** and a **Continue** button, enabled when trimmed length > 2. Submitting
  stores the text as that question's answer and advances.

### 12. Live Situation — result — `features/live_situation/ResultScreen.kt`

**The most important screen in the app.** Action plan first, everything else collapsed.

- **App bar**: sticky, white, 1dp `Line200` bottom border. 40dp back arrow, title
  (titleSmall, single line, ellipsis), 40dp speak toggle (white, 1dp `Line200`,
  `RadiusChip`, `volume_up` / `stop_circle` in `Blue600`).
- **Safety banner** (high-risk results only): `Danger100` fill, 1dp `DangerBorder`,
  `RadiusCardSm`, 14dp padding, 20dp `health_and_safety` icon, "SAFETY FIRST" kicker
  13sp / 700 uppercase, body 13.5sp / 20 `Danger700`.
- **"What I understand" card**: labelSmall kicker `Ink400`, situation summary 14.5sp / 22
  `Ink700`, then fact chips — `Line100` pills, 12sp `Ink700`, 5dp / 10dp padding, 6dp gaps,
  wrapping.
- **"What to do now"**: 20dp `checklist` icon in `Blue600` plus titleMedium. Then the
  steps, 9dp gaps. Each step is a **tappable card**: 24dp circular badge (`Blue100` fill,
  `Navy700` number 12.5sp / 700) plus step text 14.5sp / 22 / 500 and an optional note
  13sp / 19 `Ink500`. Checked: card `Success100`, border `SuccessBorder`, badge
  `Success500` with a white check, text `Success700`. Below: "Tap a step to mark it done.
  {n} of {total} done." 12.5sp `Ink500`.
- **Collapsed sections**, 8dp gaps. Each is a card with `clipToBounds`. Header: 14dp
  padding, 20dp icon in the section's ink, title (15sp / 700) over meta (12sp `Ink500`),
  trailing `expand_more` / `expand_less`. Open: header background `Background`, border
  `Line300`. Body: 15dp horizontal / bottom padding, 10dp gaps, each item a 17dp icon plus
  13.5sp / 20 `Ink700` text.
  - Section order is fixed: **Your position** (`assignment_ind`, `Ink700`) → **Your
    rights** (`gavel`, `Blue600`) → **Authority powers** (`shield`, `Caution700`) →
    **What should not happen** (`report`, `Danger600`) → **Avoid**
    (`do_not_disturb_on`, `Danger600`) → **Preserve** (`inventory_2`, `Preserve700`) →
    **Legal basis** (`menu_book`, `Navy900`).
  - Item icons: `check` for rights, `close` for avoid/improper, `arrow_right` otherwise.
  - **Caveat block** (optional, at the end of a section): `Caution100` fill, 1dp
    `CautionBorder`, 11dp padding, 16dp `help` icon, 12.5sp / 18 `Caution700`.
  - **Legal basis sources**: `Background` fill, 1dp `Line200`, `RadiusField`, 12dp
    padding. Title 13.5sp / 700 `Navy900`, excerpt 12.5sp / 19 `Ink600`, then a verified
    pill (`Success100` fill, `Success700`, 13dp `verified` icon, 11sp / 500) and meta
    11sp `Ink500` — "{source} · last verified {date}".
- **"Where to escalate" row**: `Blue050`, 1dp `BlueBorder`, `RadiusCardSm`, 15dp padding,
  21dp `alt_route` icon, title over a domain-specific blurb, trailing chevron.
- **"Save this situation"**: primary button with a 20dp `bookmark_add` icon.
- **Feedback block**: 1dp `Line200` top border, 16dp top padding. "Was this useful?"
  14sp / 500 `Ink700`, then three buttons at 44dp, `RadiusChip`, white, 1dp `Line200`:
  "Yes" (`thumb_up`, hover border `Success500`), "No" (`thumb_down`), "Incorrect"
  (`flag`, `Danger600`, hover `Danger100`). Yes/No fill the width, Incorrect hugs.
- **Disclaimer**: 11.5sp / 17 `Ink400` — "AILex gives legal first-aid based on verified
  sources. It is not a lawyer and does not guarantee any outcome."

**Result content.** Each of the five domains has its own complete result: title, safety
text, situation summary, fact chips, escalation blurb, five action steps, and seven
sections. This is a large body of copy — read it out of the prototype's `RESULTS` object
(in the `<script data-dc-script>` block of `AILex Prototype.dc.html`) and move it into
`domain/legal` as structured data. **Do not paraphrase it.** The wording distinguishes
what is verified from what is situational, and that distinction is the product.

Legal content must be versioned and carry a `lastVerified` date per source, so that the
"last verified" line in the UI is real and not decorative.

### 13. Ask Legal AI — `features/conversation/AskScreen.kt`

- **Header**: "Ask Legal AI" titleLarge over "Clear, practical guidance for your
  situation." Right: 44dp icon button to incident history (`folder_open`).
- **Intro panel**: `Blue050`, 1dp `BlueBorder`, `RadiusCard`, 15dp padding. Body 14sp / 22
  `Ink700`, then a 17dp `psychology` icon plus "I'll ask only the facts that change the
  answer, not your whole life story." 12.5sp / 18 `Ink600`.
- **"Try one of these"** titleSmall, then four suggestion rows, 8dp gaps, card,
  13dp / 14dp padding: 18dp icon in the domain ink, 14sp / 20 `Ink700` text, trailing
  `north_east` 17dp `Ink400`.
  - "Police ne mujhe roka hai, kya karu?" (`local_police`)
  - "Can traffic police demand cash for a challan?" (`traffic`)
  - "What should I do after a UPI fraud?" (`security`)
  - "My ration card application is delayed. What now?" (`account_balance`)
- **Composer**, pinned to the bottom: `Background` fill, 1dp `Line200` top border,
  12dp / 16dp padding. A 48dp min-height field (`RadiusCard`, 1dp `Line300`, placeholder
  "Describe what happened…"), a 48dp mic button (`Preserve100` fill, `Preserve700` icon,
  `RadiusCard`) and a 48dp send button (`Blue600`, white icon). Below: 15dp `lock` icon
  plus "Never share OTPs, PINs, passwords, CVVs or UPI PINs here." 11.5sp `Ink500`.

### 14. Voice turn — `features/conversation/VoiceScreen.kt`

`Navy800` full-bleed. Close button top right in `NavyBody`.

Three states in one screen — `Listening` → `Transcribing` → `Review`:

| State | Orb fill | Orb icon | Title | Body | Button |
| --- | --- | --- | --- | --- | --- |
| Listening | `Preserve500` | `graphic_eq` | "Listening…" | "Speak in English, हिंदी or मराठी. Tap stop when you are done." | "Stop" (`Danger600`) |
| Transcribing | `Navy700` | `hourglass_top` | "Understanding what you said…" | "Converting your words to text. Nothing is recorded." | "Cancel" (`Danger600`) |
| Review | `Blue600` | `task_alt` | "Is this right?" | "Correct it if I misheard, then send it." | "Send this" (`Blue600`) |

- **Orb**: 88dp circle, 40dp white icon, inside a 120dp box. While listening, a
  `Preserve500` circle behind it pulses — scale 1 → 1.9, opacity 0.55 → 0, 1.8s,
  ease-out, infinite. In Compose use `rememberInfiniteTransition` with
  `graphicsLayer { scaleX = …; scaleY = …; alpha = … }`.
- **Transcript card** (Transcribing and Review): `NavyCard` fill, 1dp `NavyLine`,
  `RadiusCard`, 15dp padding. "I HEARD" labelSmall `NavyMuted`, then the text 15.5sp / 23
  white. In Review this must be **editable** — the user corrects a mishearing before
  sending.
- **Secondary button**: "Type instead", 48dp, transparent, 1dp `NavyLine`, `NavyBody`.
- **Footnote**: "Voice recordings are not stored. Only the text is sent." 11.5sp / 17
  `NavyMuted`, centred. This must be true in the implementation — see `core/voice`.

### 15. Conversation — `features/conversation/ConversationScreen.kt`

App bar with back, title ("UPI fraud" — the derived topic), and the speak toggle.

- **User message**: end-aligned, max 82% width, `Blue600` fill, white 14.5sp / 22,
  `RadiusCard` with a 4dp bottom-end corner, 12dp / 14dp padding.
- **Assistant turn**: start-aligned, max 92% width, a column of 12dp-gapped blocks:
  1. **Urgency banner** (when time-critical): `Danger100`, 1dp `DangerBorder`,
     `RadiusCardSm`, 13dp padding, 19dp `bolt` icon, "ACT IN THE NEXT FEW MINUTES"
     kicker 12.5sp / 700 uppercase, body 13.5sp / 20 `Danger700`.
  2. **Answer card**: intro 14.5sp / 22 `Ink700`, then numbered steps — 23dp `Blue100`
     badge with `Navy700` 12sp / 700 number, step text 14sp / 21 / 500, note 12.5sp / 18
     `Ink500`. Then a 1dp `Line200` divider and warning rows: 16dp `block` icon
     `Danger600` and 16dp `inventory_2` icon `Preserve700`, each with 13sp / 19 `Ink600`
     text.
  3. **Legal basis accordion**: same collapsed-card pattern and same source card styling
     as the result screen.
  4. **Action chips**, wrapping, 8dp gaps: "Save this situation" (`Blue100` fill,
     `BlueBorder` border, `Navy700`), "Escalation" and a play/stop toggle (white, 1dp
     `Line200`, `Ink700`). Pill shape, 9dp / 12dp padding, 13sp / 500, 16dp leading icon.
- **Follow-up card**: a question plus tappable chips — `Background` fill, 1dp `Line300`,
  pill, 9dp / 13dp padding, 13sp / 500 `Ink700`. Pressed: border `Blue600`, fill
  `Blue050`. The prototype's example asks whether the user approved anything on their
  phone, with chips: "I entered my UPI PIN" / "I only shared an OTP" / "I installed an app
  they sent" / "I am not sure".
- **Composer**: field plus mic only (no send button variant needed — send appears when
  the field has content).

### 16. My Incidents — `features/incidents/IncidentListScreen.kt`

- **Header**: "My Incidents" titleLarge over "Saved situations, notes and drafts." Right:
  44dp `search` toggle.
- **Search field** (revealed): 48dp, `RadiusField`, 1dp `Line300`, 19dp `search` icon,
  placeholder "Search title, area or tag", trailing clear button. Filters live across
  title, domain, meta and tags, case-insensitive.
- **Filter chips**, horizontally scrollable, 7dp gaps: All / Active / Resolved / Drafts.
  Selected: 1dp `Blue600`, `Blue100` fill, `Navy700`. Unselected: 1dp `Line300`, white,
  `Ink600`. Pill, 8dp / 14dp padding, 13sp / 500.
- **Result count**: "4 saved" when unfiltered, "1 of 4" when filtered. 12sp `Ink400`.
- **Incident rows**, 9dp gaps, card, 14dp padding, 12dp gap: 38dp domain icon tile, then
  a column — a meta row (domain labelSmall in domain ink, then a status pill), title
  (15sp / 20 / 700, wrapping), meta (12.5sp / 18 `Ink500`), and tag chips (`Line100`
  pills, 11.5sp `Ink600`, 4dp / 9dp padding, 5dp gaps) — trailing chevron.
  - Status pills: Active `Caution100` / `Caution700`; Resolved `Success100` /
    `Success700`; Draft `Line100` / `Ink600`.
- **Empty state**: dashed `Line300`, `RadiusCard`, 26dp / 18dp padding, centred — 26dp
  `folder_open` icon `Ink400`, "Nothing here" 15sp / 700 `Ink700`, "No saved incident
  matches this filter or search." 13sp / 19 `Ink500`.
- **Storage note**: dashed border panel, 18dp `devices` icon, "Incidents are stored on
  this device only. Uninstalling the app or clearing its data removes them."

**Seed content** — four incidents, each with full detail. Read them from the prototype's
`INCIDENTS` array; summarised here:

| Id | Domain | Title | Status |
| --- | --- | --- | --- |
| i1 | Traffic | Duplicate challan at Sion Circle | Active |
| i2 | Cyber | UPI debit of ₹18,400 after a fake customer-care call | Active |
| i3 | Mumbai Local | TC asked for cash without a receipt — Kurla | Resolved |
| i4 | Government / RTS | Complaint about a delayed income certificate | Draft |

### 17. Incident detail — `features/incidents/IncidentDetailScreen.kt`

App bar: back, "Incident", and a 40dp delete button (`delete`, `Danger600`, hover
`Danger100`).

- **Header**: domain labelSmall in domain ink plus a status pill; title headlineSmall
  `Navy900`; "Saved {date}, {time} · {place}" 13sp / 19 `Ink500`.
- **Summary card**: `Blue050`, 1dp `BlueBorder`, `RadiusCard`, 15dp padding, "SUMMARY"
  labelSmall `Blue600`, body 14sp / 22 `Ink700`.
- **Key facts**: card with `clipToBounds`, rows of 12dp / 14dp padding separated by 1dp
  `Line100`. Key column 118dp wide, 13sp `Ink500`, wrapping; value 13sp / 500 `Ink900`.
- **Timeline**: for each event, a 22dp-wide gutter containing an 11dp dot (5dp top offset)
  and a 2dp `Line200` connector filling the remaining height, then a 13dp-gapped column:
  title 14sp / 500 `Ink900`, when 12.5sp / 18 `Ink500`, and an optional body in a
  `Background` panel (1dp `Line200`, `RadiusChip`, 11dp padding, 13sp / 19 `Ink600`).
  16dp bottom padding per event. Dot colours: `Blue600` for a normal event,
  `Success500` for a resolved step, `Caution500` for a problem, `Ink400` for a
  bookkeeping entry.
- **"Add a timeline event"**: dashed `Line300`, `RadiusField`, 12dp / 14dp padding, 18dp
  `add` icon, `Blue600` 13.5sp / 500, full width. Opens the composer sheet.
- **Notes**: min 92dp textarea, `RadiusCardSm`, 1dp `Line300`, 13dp padding, 14sp / 21,
  placeholder "Add anything you want to remember…", then a "Save note" button
  (`Blue100` fill, 1dp `BlueBorder`, `Navy700`, `RadiusField`, 10dp / 14dp padding).
  Notes persist per incident.
- **Evidence**: rows with a 20dp icon, name (13.5sp / 500, single line, ellipsis) and meta
  (12sp). A **missing file** is styled as a broken reference: `Danger050` fill, 1dp
  `DangerBorder`, `broken_image` icon and meta both `Danger600`. Empty state: dashed
  panel — "No files linked yet. Photos, receipts and screenshots you attach stay on this
  device." Then "These are references to files already on your phone. Nothing is
  uploaded." 12sp `Ink500`.
- **Actions**: "Create complaint draft" (primary, `description` icon) and "View escalation
  route" (secondary — 52dp, white, 1dp `Line300`, `Ink700`, `alt_route` icon).

Evidence handling matters: the app stores a **URI reference**, not a copy. Use
`ACTION_OPEN_DOCUMENT` with `takePersistableUriPermission`, and detect a revoked or
deleted target so the broken-reference state is real.

### 18. Complaint draft — `features/complaint/ComplaintDraftScreen.kt`

App bar: back, "Draft · {domain}" (single line, ellipsis), and an Edit / Done toggle
(white, 1dp `Line200`, `RadiusChip`, `Blue600` 13sp / 500).

- **Caution banner**: `Caution100`, 1dp `CautionBorder`, `RadiusCardSm`, 13dp padding,
  19dp `edit_note` icon, "Draft only. Fill in every square bracket and review it before
  sharing. The app does not file complaints for you." 13sp / 19 `Caution700`.
- **"Generated for: {incident title}"** 12.5sp / 18 `Ink500`.
- **Edited chip** (when the user has changed anything): `Blue050`, 1dp `BlueBorder`,
  `RadiusField`, 11dp / 13dp padding, 17dp `history` icon, "You have edited this draft."
  plus an underlined "Reset" `Blue600` text button.
- **Draft card**: one card, 18dp padding, 16dp gaps between sections. Each section is a
  labelSmall `Ink400` kicker over the text at 14sp / 22 `Ink700`, preserving line breaks.
  In edit mode each becomes a 4-row textarea: 1dp `Blue600` border, `Blue050` fill,
  `RadiusChip`, 11dp padding, plus a 3dp `Blue100` focus ring.
- **Actions**, 8dp gap: "Copy" (48dp, white, 1dp `Line300`, `content_copy`) and "Share"
  (48dp, `Blue600`, white, `share`). Share uses `Intent.ACTION_SEND` with the assembled
  plain text.
- **Footnote**: "This draft states facts you provided. It does not allege that any officer
  or department acted illegally." 11.5sp / 17 `Ink400`.

There are five domain-specific drafts (traffic, cyber, railway, govt, police), each with
seven sections: To, Subject, Background, Facts, Requested action, Supporting information,
Contact. Read them from the prototype's `DRAFTS` and `DRAFT` objects verbatim. Edits are
stored per incident and per section, and Reset restores the generated text.

### 19. Escalation — `features/escalation/EscalationScreen.kt`

App bar: back, "Escalation · {domain}".

- **Heading**: "Where this can go next" 22sp / 29 / 700 `Navy900`.
- **Subhead**: "Start at the top. Move down only if you get no response or the matter is
  serious."
- **Authority cards**, 10dp gaps, card with `clipToBounds`. Header: 15dp padding, 12dp
  gap, a 26dp step badge, then name (15.5sp / 20 / 700) over role (12.5sp / 18 `Ink500`).
  The **first** card is emphasised: border `BlueBorder`, header background `Blue050`,
  badge `Blue600` with a white number. Others: border `Line200`, white header, badge
  `Line100` with `Ink600`. Body: 15dp horizontal / bottom padding, 9dp gaps — a
  description 13.5sp / 20 `Ink700`, then contact rows (`Background` fill, 1dp `Line200`,
  `RadiusChip`, 11dp padding, 18dp `Blue600` icon, value 13sp / 500 over label 11.5sp
  `Ink500`), then an optional caution note styled like the caveat block.
- **Legal aid card** at the end: `Blue050`, 1dp `BlueBorder`, `RadiusCard`, 15dp padding,
  20dp `balance` icon, "When to get a lawyer" 14.5sp / 700 `Navy900` plus body 13sp / 20
  `Ink600`.

Routes are **domain-specific** and ordered; the Maharashtra State Legal Services Authority
(15100) is appended to every path. Read the five route sets from the prototype's
`ESCALATION` object. Phone numbers must dial via `ACTION_DIAL`; web routes should open in
a browser, and the prototype deliberately labels them "verify the URL in the app" rather
than hardcoding a URL that may move — resolve real URLs before shipping.

### 20. Me — `features/profile/MeScreen.kt`

- **Title**: "Me" titleLarge `Navy900`.
- **Profile card**: 16dp padding, 13dp gap — a 48dp `Blue100` circle with initials
  (18sp / 700 `Navy700`), then name (16sp / 700) over "{masked phone} · {language}"
  (13sp `Ink500`), then an "Edit" button.
- **Three setting groups**, each a labelSmall `Ink400` heading over a card with
  `clipToBounds`. Rows: 14dp padding, 12dp gap, 20dp icon, label 14.5sp / 500 with an
  optional sub (12sp `Ink500`), an optional right-aligned value (13sp `Ink500`), trailing
  chevron. Rows separated by 1dp `Line100`. Pressed: `Background`.
  - **Preferences** — Language (`translate`, `Blue600`, value = current language, opens
    the language sheet) · Notifications (`notifications_active`, `Caution700`, "Reminders
    and source updates") · Voice (`record_voice_over`, `Preserve700`, "Auto-play answers,
    speech rate")
  - **Privacy and data** — Privacy and data (`lock`, `Blue600`, "What is stored, and
    where") · Export my incidents (`download`, `Blue600`, "A plain text copy on this
    device") · Delete my data (`delete_forever`, `Danger600`, label also `Danger600`)
  - **Understanding AILex** — How Legal AI works (`psychology`, "Sources, verification
    and limits") · Common questions (`help`) · What it cannot do (`gavel`, "Limits and
    disclaimer")
- **Footer**: "AILex 1.0 · Mumbai / MMR · Legal first-aid, not legal representation." and
  "Signed in as {masked phone}", both 11.5sp / 17 `Ink400`.

### 21. Notifications — `features/settings/NotificationsScreen.kt`

App bar: back, "Notifications".

- **Intro**: "Reminders about your own incidents, and changes to the sources behind your
  saved answers. Nothing is sent to anyone else." 13.5sp / 20 `Ink500`.
- **Rows**, 9dp gaps, card, 14dp padding, 12dp gap: 36dp tinted icon tile, then a column —
  a title row (14.5sp, **700 when unread** else 500, plus an 8dp `Blue600` dot when
  unread), body 12.5sp / 18 `Ink600`, when 11.5sp `Ink400`. Tapping a row deep-links to
  its incident.

Four seeded notifications: an unfiled NCRP complaint (`priority_high`, danger, → i2), a
still-pending duplicate challan (`traffic`, caution, → i1), a re-verified legal source
(`menu_book`, blue, no link), and a missing evidence file (`broken_image`, danger, → i1).

### 22. Understanding AILex — `features/settings/HelpScreen.kt`

App bar: back, "Understanding AILex". Three filter-style tabs: "How Legal AI works",
"What it cannot do", "Common questions". Selected tab styling matches the incident filter
chips. Body: cards, 10dp gaps, 15dp padding — heading 15.5sp / 20 / 700 `Navy900` over
body 13.5sp / 21 `Ink600`. A legal-aid card closes the screen.

Content for all three tabs is in the prototype's `HELP` object.

### 23. Privacy and data — `features/settings/PrivacyScreen.kt`

- **Heading**: "What we hold, plainly" 22sp / 29 / 700 `Navy900`.
- **Subhead**: "Written as what the app actually does, not as a promise it cannot keep."
- **Five rows**, card, 14dp padding, 12dp gap, 20dp icon, title 14.5sp / 700 over body
  13sp / 20 `Ink600`: mobile number (`phone_iphone`) · conversations (`chat_bubble`) ·
  voice (`mic_off`, `Preserve700`) · saved incidents (`devices`, `Caution700`) · evidence
  files (`folder_off`, `Caution700`).
- **Delete panel**: `Danger050`, 1dp `DangerBorder`, `RadiusCard`, 16dp padding — "Delete
  my data" 15.5sp / 700 `Danger700`, body, then a 48dp button (white, 1dp `Danger500`,
  `Danger600` label): "Continue to deletion".

Every claim on this screen is a **contract with the implementation**. If the app uploads
evidence, caches conversations, or retains audio, this copy has to change.

### 24. Delete my data — `features/settings/DeleteDataScreen.kt`

`Danger050` background, back arrow.

- 52dp `Danger100` tile, `RadiusCardSm`, 26dp `delete_forever` icon `Danger600`.
- **Heading**: "Delete everything?" headlineSmall `Danger700`.
- **Body**: "Confirm what goes. There is no recovery and no backup."
- **Four count rows**: card, 13dp padding, 18dp `remove_circle` icon `Danger600`, label
  13.5sp / 19 `Ink700`, count 12.5sp / 500 `Ink500` — saved incidents, notes and timeline
  events, complaint drafts, profile and mobile number. Counts must be real.
- **Confirmation card**: "Type DELETE to confirm" 13sp / 500, then a 50dp input
  (uppercased on input, capped at 6, 1dp `Danger500` focus border and `Danger100` ring).
- **Destructive button**: 52dp `Danger500`, "Delete my data permanently", disabled until
  the text is exactly `DELETE`. Then "Keep my data" (48dp, white, 1dp `Line300`).

Deleting clears local storage, revokes the session, and returns to Welcome with a
confirmation toast.

---

## Shared components — `ui/components/`

Build these once:

| Composable | Notes |
| --- | --- |
| `AilexCard` | White surface, 1dp `Line200`, `RadiusCard`, no elevation. Optional tint/border override for the `Blue050`, `Danger100`, `Caution100`, `Success100` and `Preserve100` variants. |
| `SectionKicker` | labelSmall, uppercase, +0.09em tracking. |
| `IconTile` | Square tinted tile — 34 / 36 / 38 / 40dp sizes, `RadiusChip`, centred icon in the matching ink. |
| `StatusPill`, `TagChip`, `FilterChip` | Pill shapes, the fill/ink pairs listed above. |
| `PrimaryButton`, `SecondaryButton`, `DangerButton` | 52 / 48dp, `RadiusField`, disabled = `Line200` fill + `Ink400` label. |
| `ListRow` | Icon tile + title + optional sub + trailing chevron; the recurring tappable row. |
| `Accordion` | Header with `expand_more` / `expand_less`, animated body, open-state header tint and border change. Use `animateContentSize()`. |
| `CalloutBanner` | The safety / caution / info banner in four semantic variants. |
| `BottomSheet` | `RadiusSheet` top corners, a 36×4dp `Line300` grabber, 20dp horizontal padding, 24dp bottom. Content slides up 6dp with a fade over 280ms. |
| `Toast` | `Ink900` fill, `RadiusField`, 13dp / 15dp padding, 19dp `check_circle` in `SuccessOnDark`, white 13.5sp / 500. Floats 96dp above the bottom, 16dp inset, 2.6s auto-dismiss. Use a `Snackbar` with custom styling. |
| `StepItem` | The tappable numbered action step with its checked state. |
| `TimelineItem` | Gutter dot + connector + content. |
| `SourceCard` | Legal-basis source with the verified pill. |

### Icons

The prototype uses **Material Symbols Outlined** by name. In Compose, use
`androidx.compose.material:material-icons-extended` where a match exists, and add the
missing glyphs as vector drawables in `res/drawable`. Names used, in full:

`balance` `arrow_back` `close` `arrow_right` `chevron_right` `check` `check_circle`
`verified` `translate` `place` `info` `notifications` `notifications_active`
`emergency_home` `bolt` `local_police` `traffic` `train` `account_balance` `security`
`more_horiz` `forum` `folder_open` `folder_off` `home` `person` `search` `add`
`shield_with_heart` `sos` `emergency` `support_agent` `health_and_safety` `shield`
`gavel` `report` `do_not_disturb_on` `inventory_2` `menu_book` `assignment_ind`
`checklist` `help` `alt_route` `bookmark_add` `thumb_up` `thumb_down` `flag` `visibility_off`
`volume_up` `stop_circle` `mic` `mic_off` `send` `psychology` `north_east` `graphic_eq`
`hourglass_top` `task_alt` `history` `description` `edit_note` `content_copy` `share`
`image` `broken_image` `devices` `phone_iphone` `chat_bubble` `lock` `download`
`delete` `delete_forever` `remove_circle` `priority_high` `block` `call` `mail`
`language` `format_size` `record_voice_over`

No raster assets. No custom fonts. The only bespoke shape is the hexagonal brand mark.

---

## Interactions and behaviour

- **Navigation transitions**: default Material 3 forward/back. Bottom-nav switches do not
  animate. Bottom sheets slide up over 280ms ease-out; the toast rises 6dp with a fade
  over 220ms.
- **Accordion**: `animateContentSize()`, chevron rotates 180°.
- **Action steps**: tap toggles done; the completion count updates immediately.
- **Voice**: the pulse ring is the only continuous animation. Stop → Transcribing →
  Review are explicit user-driven transitions in the prototype; in the real app,
  Transcribing ends when recognition returns.
- **Pressed states**: every tappable card lightens to `Blue050` with a `BlueBorder`
  border, except danger surfaces which deepen their own tint. Use
  `indication = ripple()` bounded to the card shape.
- **Focus**: inputs take a `Blue600` border plus a 3dp `Blue100` ring.
- **Disabled**: `Line200` fill, `Ink400` label, no ripple.
- **Validation**: phone exactly 10 digits · OTP exactly 6 · name trimmed length > 1 ·
  free-text answer trimmed length > 2 · timeline event trimmed length > 2 · deletion
  confirmation exactly `DELETE`. Buttons are disabled, not error-flagged — the prototype
  never shows a red validation message during onboarding.
- **Back behaviour**: the question screen steps backwards through questions before
  leaving the flow. Result → category. Escalation returns to wherever it was opened from
  (result, conversation, or incident detail).
- **Scroll**: app bars on the result, incident detail, complaint, escalation,
  notifications and help screens are sticky. Composers on Ask and Conversation are pinned
  to the bottom.
- **Rotation and process death**: all in-progress flow state must survive both. Use
  `SavedStateHandle` in each ViewModel.

## State management

One ViewModel per feature. Suggested state, mirroring the prototype:

```kotlin
// features/auth
data class AuthState(
  val phone: String = "", val otp: String = "",
  val name: String = "", val language: Language = Language.English,
)

// features/live_situation
data class LiveSituationState(
  val safetyConfirmed: Boolean = false,
  val domain: Domain? = null,
  val questionIndex: Int = 0,
  val answers: Map<Int, String> = emptyMap(),   // question index → answer
  val result: SituationResult? = null,
  val completedSteps: Set<Int> = emptySet(),
  val expandedSections: Set<SectionId> = emptySet(),
  val speaking: Boolean = false,
)

// features/incidents
data class IncidentListState(
  val filter: IncidentFilter = IncidentFilter.All,
  val searchOpen: Boolean = false,
  val query: String = "",
  val incidents: List<Incident> = emptyList(),
)

data class IncidentDetailState(
  val incident: Incident? = null,
  val note: String = "",                        // persisted per incident
  val addedEvents: List<TimelineEvent> = emptyList(),
)

// features/complaint
data class ComplaintState(
  val incidentId: String,
  val sections: List<DraftSection>,             // generated
  val edits: Map<Int, String> = emptyMap(),     // section index → edited text
  val editing: Boolean = false,
) { val isEdited get() = edits.isNotEmpty() }
```

**Saving a situation** creates an incident from the answers actually given. Two rules the
prototype enforces and the implementation must keep:

1. The **location** comes from the domain's location question (Traffic q3, Police q2,
   Mumbai Local q3). Government and Cyber have no location question — omit the segment
   rather than substituting another answer.
2. Generated fact rows use the **question text** as the key, not "Answer 1 / 2 / 3".

**Persistence.** `data/local` + `core/database` (Room) for incidents, notes, timeline
events, draft edits and evidence URIs; `core/storage` for the session and preferences.
Nothing in this list leaves the device in V1, which is what the Privacy screen promises.
`core/security` should hold the encryption at rest.

## Data model

```kotlin
enum class Domain { Police, Traffic, Railway, Government, Cyber }
enum class RiskLevel { Standard, High }
enum class IncidentStatus { Active, Resolved, Draft }

data class SituationResult(
  val domain: Domain, val title: String, val risk: RiskLevel,
  val safetyNote: String?, val situationSummary: String, val factChips: List<String>,
  val actionSteps: List<ActionStep>, val sections: List<ResultSection>,
  val escalationBlurb: String,
)
data class ActionStep(val text: String, val note: String? = null)
data class ResultSection(
  val id: SectionId, val title: String, val meta: String,
  val items: List<SectionItem>, val caveat: String? = null,
  val sources: List<LegalSource> = emptyList(),
)
data class LegalSource(
  val title: String, val excerpt: String,
  val authority: String, val lastVerified: LocalDate,
)
data class Incident(
  val id: String, val domain: Domain, val title: String, val status: IncidentStatus,
  val savedAt: Instant, val place: String?, val summary: String,
  val facts: List<Pair<String, String>>, val timeline: List<TimelineEvent>,
  val evidence: List<EvidenceRef>, val tags: List<String>,
)
data class EvidenceRef(val uri: Uri, val displayName: String, val available: Boolean)
```

## Accessibility

- Every icon-only button needs a `contentDescription`.
- Respect the system font scale — no fixed `sp` clamping. Every layout in the prototype
  must survive 200% text scale; the result screen and the incident rows are where this
  will break first, so test those.
- Minimum touch target 48dp, enforced with `Modifier.minimumInteractiveComponentSize()`.
- Risk states must not rely on colour alone: the safety banner has an icon and the word
  "SAFETY FIRST", completed steps have a check glyph, missing evidence says so in text.
- `TalkBack`: the action plan should read as a numbered list; the accordions need
  expanded/collapsed state announced.

## Not designed yet

Out of scope in the prototype and needing design before implementation: the notification
permission and reminder scheduling UI, voice settings detail, the incident export format,
the "report an incorrect answer" flow, session expiry and re-auth, offline and error
states for the AI call, and empty states for a first-run user with no incidents on the
Incidents tab.

## Legal content caveat

The legal text in the prototype is **illustrative, written for layout review**. Citations
point at real instruments (Motor Vehicles Act 1988 s.130, BNSS 2023, Constitution art.22,
Railways Act 1989, Maharashtra RTS Act 2015, RBI directions on unauthorised electronic
transactions, NCRP / helpline 1930), but the wording is not verified legal drafting. It
must be reviewed by a qualified advocate before release, and the `lastVerified` dates must
become real.

## Files in this bundle

- `AILex Prototype.dc.html` — the prototype source. All content data (question sets,
  results, escalation routes, drafts, help text) lives in the `<script data-dc-script>`
  block near the end.
- `AILex Prototype (offline).html` — self-contained version; open in any browser, offline.
- `android-frame.jsx` — the device bezel used to frame the prototype. **Not part of the
  app.** Ignore it.
- `design.md` — the original AILex design specification this prototype was built against.
- `README.md` — this document.

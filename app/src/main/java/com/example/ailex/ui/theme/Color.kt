package com.example.ailex.ui.theme

import androidx.compose.ui.graphics.Color

// Primary
val Primary900 = Color(0xFF102A56)
val Primary700 = Color(0xFF174A9C)
val Primary600 = Color(0xFF2166D1)
val Primary500 = Color(0xFF3B82E8)
val Primary100 = Color(0xFFEAF2FF)
val Primary050 = Color(0xFFF5F8FF)

// Secondary
val Secondary700 = Color(0xFF126B69)
val Secondary500 = Color(0xFF2C9C96)
val Secondary100 = Color(0xFFE7F7F5)

// Accent
val Accent700 = Color(0xFFB45309)
val Accent500 = Color(0xFFF59E0B)
val Accent100 = Color(0xFFFFF4D6)

// Success
val Success700 = Color(0xFF18733A)
val Success500 = Color(0xFF2E9B52)
val Success100 = Color(0xFFEAF7EE)

// Warning
val Warning700 = Color(0xFF9A5B00)
val Warning500 = Color(0xFFE89A17)
val Warning100 = Color(0xFFFFF6E4)

// Error and urgent safety
val Error700 = Color(0xFFB42318)
val Error500 = Color(0xFFD92D20)
val Error100 = Color(0xFFFDECEC)
val Urgent900 = Color(0xFF8F1717)
val Urgent050 = Color(0xFFFFF7F4)

// Neutral
val Neutral950 = Color(0xFF101828)
val Neutral800 = Color(0xFF344054)
val Neutral700 = Color(0xFF475467)
val Neutral500 = Color(0xFF667085)
val Neutral300 = Color(0xFFD0D5DD)
val Neutral200 = Color(0xFFEAECF0)
val Neutral100 = Color(0xFFF2F4F7)
val Neutral050 = Color(0xFFF8FAFC)
val White = Color(0xFFFFFFFF)

// ---------------------------------------------------------------------------
// design_handoff_ailex_v1 token set — the current spec of record. New screens
// (Stage 1 onward) reference these directly. The design.md tokens above are
// kept only because screens not yet migrated to the new spec still use them;
// remove a design.md token once every reference to it has been migrated.
// The two sets share almost all of their hex values (same palette, regrouped
// with finer risk semantics), so mixing them mid-migration does not clash.
// ---------------------------------------------------------------------------

// Brand / primary
val Navy900 = Color(0xFF102A56)        // headings, brand
val Navy800 = Color(0xFF0B2545)        // voice screen background
val Navy700 = Color(0xFF174A9C)        // pressed primary, active nav label
val Blue600 = Color(0xFF2166D1)        // primary action, links, accents
val Blue100 = Color(0xFFEAF2FF)        // primary tint / active nav pill
val Blue050 = Color(0xFFF5F8FF)        // tinted card background
val BlueBorder = Color(0xFFD6E4FB)     // border on tinted cards
val NavyLine = Color(0xFF1E3E68)       // borders on the dark voice screen
val NavyCard = Color(0xFF0F2F57)       // card on the dark voice screen
val NavyMuted = Color(0xFF7FA6D4)      // muted text on dark
val NavyBody = Color(0xFFB9CEE8)       // body text on dark

// Neutrals
val Ink900 = Color(0xFF101828)         // primary text
val Ink700 = Color(0xFF344054)         // body text
val Ink600 = Color(0xFF475467)         // secondary body
val Ink500 = Color(0xFF667085)         // meta text
val Ink400 = Color(0xFF98A2B3)         // disabled text, faint meta
val Line300 = Color(0xFFD0D5DD)        // input borders, strong dividers
val Line200 = Color(0xFFEAECF0)        // card borders, standard dividers
val Line100 = Color(0xFFF2F4F7)        // chip fill, row dividers
val Surface = Color(0xFFFFFFFF)        // cards, sheets, app bars
val Background = Color(0xFFF8FAFC)     // screen background
val Canvas = Color(0xFFECEEF1)         // desk background behind the phone frame

// Risk semantics — DO NOT COLLAPSE THESE
val Danger700 = Color(0xFF8F1717)      // danger heading text
val Danger600 = Color(0xFFB42318)      // danger icon + label, pressed danger button
val Danger500 = Color(0xFFD92D20)      // danger button fill
val Danger100 = Color(0xFFFDECEC)      // danger tint
val Danger050 = Color(0xFFFFF7F4)      // danger screen background
val DangerBorder = Color(0xFFF2B8AE)   // danger border, strong
val DangerBorderLt = Color(0xFFFBD9D2) // danger border, soft

val Caution700 = Color(0xFF9A5B00)     // caution text + icon
val Caution500 = Color(0xFFE89A17)     // caution timeline dot
val Caution100 = Color(0xFFFFF6E4)     // caution tint
val CautionBorder = Color(0xFFFFE7B8)

val SuccessBorder = Color(0xFFC7E9D3)
val SuccessOnDark = Color(0xFF6EE7A8)  // toast check icon

val Preserve700 = Color(0xFF126B69)    // evidence / preserve text + icon
val Preserve500 = Color(0xFF2C9C96)    // voice orb, positive check
val Preserve100 = Color(0xFFE7F7F5)    // evidence tint
val PreserveBorder = Color(0xFFCFEEEB)

// Success700 / Success500 / Success100 above are identical in both token
// sets (0xFF18733A / 0xFF2E9B52 / 0xFFEAF7EE) — reused as-is, not redeclared.

/** Domain accent pair — pale tint for large fills, saturated ink for icons/text. */
data class DomainAccent(val tint: Color, val ink: Color)

object DomainAccents {
    val Police = DomainAccent(Blue100, Blue600)
    val Traffic = DomainAccent(Caution100, Caution700)
    val MumbaiLocal = DomainAccent(Preserve100, Preserve700)
    val Government = DomainAccent(Line100, Ink700)
    val Cyber = DomainAccent(Danger100, Danger600)
    val SomethingElse = DomainAccent(Line100, Ink600)
}

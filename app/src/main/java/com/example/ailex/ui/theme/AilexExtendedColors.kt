package com.example.ailex.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AilexExtendedColors(
    val urgentBackground: Color,
    val urgentBorder: Color,
    val urgentHeadline: Color,
    val infoBackground: Color,
    val infoBorder: Color,
    val successBackground: Color,
    val successText: Color,
    val successIcon: Color,
    val warningBackground: Color,
    val warningText: Color,
    val warningIcon: Color,
    val accentBackground: Color,
    val accentText: Color,
    val accentIcon: Color,
    val secondaryAction: Color,
    val secondarySurface: Color,
    val borderSubtle: Color,
    val borderDefault: Color,
    val textSupporting: Color,
    val appBackground: Color
)

val LightAilexExtendedColors = AilexExtendedColors(
    urgentBackground = Urgent050,
    urgentBorder = Error100,
    urgentHeadline = Urgent900,
    infoBackground = Primary050,
    infoBorder = Primary100,
    successBackground = Success100,
    successText = Success700,
    successIcon = Success500,
    warningBackground = Warning100,
    warningText = Warning700,
    warningIcon = Warning500,
    accentBackground = Accent100,
    accentText = Accent700,
    accentIcon = Accent500,
    secondaryAction = Secondary700,
    secondarySurface = Secondary100,
    borderSubtle = Neutral200,
    borderDefault = Neutral300,
    textSupporting = Neutral500,
    appBackground = Neutral050
)

// design.md only publishes a light palette and gates dark mode on being
// "fully tested" (5.10); dark theme uses the same values for V1.
val LocalAilexExtendedColors = staticCompositionLocalOf { LightAilexExtendedColors }

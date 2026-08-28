package com.example.ailex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Mapped onto design_handoff_ailex_v1 tokens — see Color.kt. Values are
// identical to (or a direct refinement of) the design.md tokens they
// replace, so this is not a visual regression for screens still pending
// migration to the new component set.
private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Surface,
    primaryContainer = Blue100,
    onPrimaryContainer = Navy900,
    secondary = Preserve700,
    onSecondary = Surface,
    secondaryContainer = Preserve100,
    onSecondaryContainer = Preserve700,
    tertiary = Caution700,
    onTertiary = Surface,
    tertiaryContainer = Caution100,
    onTertiaryContainer = Caution700,
    error = Danger500,
    onError = Surface,
    errorContainer = Danger100,
    onErrorContainer = Danger700,
    background = Background,
    onBackground = Ink900,
    surface = Surface,
    onSurface = Ink900,
    surfaceVariant = Line100,
    onSurfaceVariant = Ink700,
    outline = Line300,
    outlineVariant = Line200
)

private val AilexShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = ShapeField,
    medium = ShapeCard,
    large = ShapeCardLg,
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun AilexTheme(
    // design.md only publishes a light palette and gates dark mode on being
    // "fully tested" (5.10); both Light and System resolve to the same
    // light palette in V1 rather than guessing at unverified dark colors.
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAilexExtendedColors provides LightAilexExtendedColors) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            shapes = AilexShapes,
            content = content
        )
    }
}

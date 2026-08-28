package com.example.ailex.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.Surface as SurfaceColor

/**
 * The recurring card surface across design_handoff_ailex_v1: a flat fill
 * with a 1dp border and no elevation — borders, not shadows, separate
 * content from the background. Pass [fill]/[border] to get one of the
 * tinted variants (Blue050/BlueBorder, Danger100/DangerBorder, etc.).
 *
 * When [onClick] is set, the card lightens to [pressedFill]/[pressedBorder]
 * while pressed — "every tappable card lightens to `Blue050` with a
 * `BlueBorder` border" per the interactions spec. Override both for cards
 * that should deepen their own tint instead (danger surfaces).
 */
@Composable
fun AilexCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShapeCard,
    fill: Color = SurfaceColor,
    border: Color = Line200,
    borderWidth: Dp = 1.dp,
    pressedFill: Color = Blue050,
    pressedBorder: Color = BlueBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val isPressedClickable = onClick != null && pressed

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .let {
                if (onClick != null) {
                    it.clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick)
                } else {
                    it
                }
            },
        shape = shape,
        color = if (isPressedClickable) pressedFill else fill,
        border = BorderStroke(borderWidth, if (isPressedClickable) pressedBorder else border),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        content = content
    )
}

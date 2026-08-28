package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Surface

/**
 * The brand mark: a 56×64dp hexagon in `Navy900` with a white `balance`
 * icon, 28dp, centered. Deliberately abstract (not a shield/scales glyph
 * drawn literally) so it never reads as an official government emblem.
 *
 * Vertices match design_handoff_ailex_v1's exact
 * `clip-path:polygon(50% 0,100% 16%,100% 62%,50% 100%,0 62%,0 16%)` —
 * pointed top/bottom, flat vertical sides between 16% and 62% height.
 */
private val HexagonShape = GenericShape { size, _ ->
    moveTo(size.width * 0.5f, 0f)
    lineTo(size.width, size.height * 0.16f)
    lineTo(size.width, size.height * 0.62f)
    lineTo(size.width * 0.5f, size.height)
    lineTo(0f, size.height * 0.62f)
    lineTo(0f, size.height * 0.16f)
    close()
}

@Composable
fun AppMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 56.dp, height = 64.dp)
            .background(Navy900, HexagonShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Balance,
            contentDescription = null,
            tint = Surface,
            modifier = Modifier.size(28.dp)
        )
    }
}

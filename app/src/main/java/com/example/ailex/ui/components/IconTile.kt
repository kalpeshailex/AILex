package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.ShapeChip

/** Square tinted icon tile — domain tiles, incident rows, settings rows. */
@Composable
fun IconTile(
    icon: ImageVector,
    tint: Color,
    ink: Color,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = size * 0.5f,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .background(tint, ShapeChip),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = ink,
            modifier = Modifier.size(iconSize)
        )
    }
}

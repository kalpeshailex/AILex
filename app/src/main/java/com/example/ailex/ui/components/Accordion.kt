package com.example.ailex.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Surface

/**
 * The collapsed-section pattern used on the Result, Conversation and
 * Escalation screens: an icon/title/meta header with a chevron, an
 * `animateContentSize()` body, and an open-state header tint/border change.
 */
@Composable
fun Accordion(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    meta: String? = null,
    body: @Composable () -> Unit
) {
    AilexCard(
        modifier = modifier,
        border = if (expanded) Line300 else Line200
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onToggle)
                    .background(if (expanded) Background else Surface)
                    .padding(Spacing.cardPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.space3)
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge, color = Ink900)
                    if (meta != null) {
                        Text(text = meta, style = MaterialTheme.typography.bodySmall, color = Ink500)
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = Ink500
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(
                        start = Spacing.cardPadding,
                        end = Spacing.cardPadding,
                        bottom = Spacing.cardPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.space2)
                ) {
                    body()
                }
            }
        }
    }
}

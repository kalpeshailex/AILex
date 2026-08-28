package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Surface

/** Selectable pill filter — "All / Active / Resolved / Drafts". */
@Composable
fun AilexFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fill = if (selected) Blue100 else Surface
    val border = if (selected) Blue600 else Line300
    val ink = if (selected) Navy700 else Ink600
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = ink,
        modifier = modifier
            .background(fill, ShapePill)
            .border(1.dp, border, ShapePill)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.space4, vertical = Spacing.space2)
    )
}

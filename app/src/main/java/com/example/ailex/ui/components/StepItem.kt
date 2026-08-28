package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Success100
import com.example.ailex.ui.theme.Success500
import com.example.ailex.ui.theme.Success700
import com.example.ailex.ui.theme.SuccessBorder
import com.example.ailex.ui.theme.Surface

/**
 * A numbered, tappable action step from the Result and Conversation
 * screens — toggles between an unchecked `Blue100` badge and a checked
 * `Success100` card with a check glyph.
 */
@Composable
fun StepItem(
    number: Int,
    text: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    note: String? = null
) {
    AilexCard(
        modifier = modifier,
        fill = if (checked) Success100 else Surface,
        border = if (checked) SuccessBorder else Line200,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier.padding(Spacing.cardPadding),
            horizontalArrangement = Arrangement.spacedBy(Spacing.space3),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (checked) Success500 else Blue100, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (checked) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Surface, modifier = Modifier.size(14.dp))
                } else {
                    Text(text = number.toString(), style = MaterialTheme.typography.labelMedium, color = Navy700, fontWeight = FontWeight.Bold)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (checked) Success700 else Ink900
                )
                if (note != null) {
                    Text(text = note, style = MaterialTheme.typography.bodySmall, color = Ink500)
                }
            }
        }
    }
}

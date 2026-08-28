package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Success100
import com.example.ailex.ui.theme.Success700
import com.example.ailex.ui.theme.ShapePill

/**
 * A legal-basis source, shown inside a Result/Conversation/Escalation
 * accordion body: title, excerpt, a verified pill and "{authority} · last
 * verified {date}" meta.
 */
@Composable
fun SourceCard(
    title: String,
    excerpt: String,
    authority: String,
    lastVerified: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Background, ShapeField)
            .padding(Spacing.space3),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Navy900)
        Text(text = excerpt, style = MaterialTheme.typography.bodySmall, color = Ink600)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.space2)
        ) {
            Row(
                modifier = Modifier
                    .background(Success100, ShapePill)
                    .padding(horizontal = Spacing.space2, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Filled.Verified, contentDescription = null, tint = Success700, modifier = Modifier.size(13.dp))
                Text(text = "Verified", style = MaterialTheme.typography.labelMedium, color = Success700)
            }
            Text(
                text = "$authority · last verified $lastVerified",
                style = MaterialTheme.typography.labelMedium,
                color = Ink500
            )
        }
    }
}

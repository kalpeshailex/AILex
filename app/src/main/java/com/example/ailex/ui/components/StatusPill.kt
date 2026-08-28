package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.ui.theme.Caution100
import com.example.ailex.ui.theme.Caution700
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Line100
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Success100
import com.example.ailex.ui.theme.Success700

/**
 * Incident status pill — Active `Caution100`/`Caution700`, Resolved
 * `Success100`/`Success700`, Draft `Line100`/`Ink600`.
 */
@Composable
fun StatusPill(status: IncidentStatus, modifier: Modifier = Modifier) {
    val (fill, ink) = when (status) {
        IncidentStatus.ACTIVE -> Caution100 to Caution700
        IncidentStatus.RESOLVED -> Success100 to Success700
        IncidentStatus.DRAFT -> Line100 to Ink600
    }
    Text(
        text = status.displayName,
        style = MaterialTheme.typography.labelMedium,
        color = ink,
        modifier = modifier
            .background(fill, ShapePill)
            .padding(horizontal = Spacing.space2, vertical = Spacing.space1)
    )
}

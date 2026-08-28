package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.Spacing

/**
 * One incident-detail timeline event: a gutter dot + connector, then a
 * title/when/optional body. Pass `isLast = true` on the final event to
 * omit the connector.
 */
@Composable
fun TimelineItem(
    title: String,
    `when`: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
    body: String? = null,
    isLast: Boolean = false
) {
    Row(modifier = modifier.padding(bottom = 16.dp).height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.width(22.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(11.dp)
                    .background(dotColor, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .width(2.dp)
                        .weight(1f)
                        .background(Line200)
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = Spacing.space3),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Ink900)
            Text(text = `when`, style = MaterialTheme.typography.bodySmall, color = Ink500)
            if (body != null) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink600,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .background(Background, ShapeChip)
                        .padding(11.dp)
                )
            }
        }
    }
}

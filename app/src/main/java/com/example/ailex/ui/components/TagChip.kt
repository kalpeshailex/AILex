package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Line100
import com.example.ailex.ui.theme.ShapePill

private val TagChipTextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 16.sp)

/** Incident tag pill — `Line100` fill, `Ink600` text, 4dp/9dp padding. */
@Composable
fun TagChip(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = TagChipTextStyle,
        color = Ink600,
        modifier = modifier
            .background(Line100, ShapePill)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    )
}

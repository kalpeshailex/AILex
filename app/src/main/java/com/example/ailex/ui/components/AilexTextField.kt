package com.example.ailex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Surface

/**
 * A single-line input matching design_handoff_ailex_v1's field styling:
 * `Line300` border by default, `Blue600` border plus a 3dp `Blue100` ring
 * on focus. The ring is a fixed 3dp of transparent/`Blue100` padding around
 * the field at all times, so focusing never shifts layout.
 */
@Composable
fun AilexTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    height: Dp = 56.dp,
    shape: Shape = ShapeField,
    fill: Color = Surface,
    fontSize: TextUnit = 17.sp,
    letterSpacing: TextUnit = 0.sp,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    contentPadding: Dp = 14.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val textStyle = TextStyle(fontSize = fontSize, letterSpacing = letterSpacing, color = Ink900)
    val ringColor = if (focused) Blue100 else Color.Transparent
    val borderColor = if (focused) Blue600 else Line300

    Box(
        modifier = modifier
            .background(ringColor, shape)
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(fill, shape)
                .border(1.dp, borderColor, shape)
                .padding(horizontal = contentPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                Text(text = placeholder, style = textStyle, color = Ink400)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = textStyle,
                cursorBrush = SolidColor(Blue600),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

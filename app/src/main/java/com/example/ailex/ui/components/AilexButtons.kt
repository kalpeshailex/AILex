package com.example.ailex.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Danger500
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Surface

/**
 * Full-width filled action button, 52dp — `Blue600`, pressed `Navy700`,
 * white `labelLarge`. Disabled state is `Line200` fill / `Ink400` label,
 * not a translucent overlay, per design_handoff_ailex_v1.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = rememberPressScaleSource()
    val pressed by interactionSource.collectIsPressedAsState()
    val containerColor = when {
        !enabled || loading -> Line200
        pressed -> Navy700
        else -> Blue600
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.buttonHeightPrimary)
            .pressScale(interactionSource),
        enabled = enabled && !loading,
        shape = ShapeField,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Surface,
            disabledContainerColor = Line200,
            disabledContentColor = Ink400
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        interactionSource = interactionSource
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Surface,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(Spacing.space2))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Full-width outlined button, 48dp — white, `Line300` border, `Ink700`
 * label.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = rememberPressScaleSource()
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(Spacing.buttonHeightSecondary)
            .pressScale(interactionSource),
        enabled = enabled,
        shape = ShapeField,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Ink700,
            disabledContentColor = Ink400
        ),
        border = BorderStroke(1.dp, if (enabled) Line300 else Line200),
        interactionSource = interactionSource
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(Spacing.space2))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Full-width destructive button, 52dp — `Danger500`, pressed `Danger600`, white label. */
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = rememberPressScaleSource()
    val pressed by interactionSource.collectIsPressedAsState()
    val containerColor = when {
        !enabled -> Line200
        pressed -> Danger600
        else -> Danger500
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.buttonHeightPrimary)
            .pressScale(interactionSource),
        enabled = enabled,
        shape = ShapeField,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Surface,
            disabledContainerColor = Line200,
            disabledContentColor = Ink400
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
        interactionSource = interactionSource
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.space2))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun OutlinedAilexButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = rememberPressScaleSource()
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(Spacing.buttonHeightPrimary)
            .pressScale(interactionSource),
        enabled = enabled,
        shape = ShapeField,
        border = ButtonDefaults.outlinedButtonBorder(enabled),
        interactionSource = interactionSource
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

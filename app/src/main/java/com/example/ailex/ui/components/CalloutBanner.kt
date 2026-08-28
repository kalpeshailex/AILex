package com.example.ailex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Caution100
import com.example.ailex.ui.theme.Caution700
import com.example.ailex.ui.theme.CautionBorder
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger700
import com.example.ailex.ui.theme.DangerBorder
import com.example.ailex.ui.theme.Preserve100
import com.example.ailex.ui.theme.Preserve700
import com.example.ailex.ui.theme.PreserveBorder
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.Spacing

enum class CalloutVariant { Danger, Caution, Info, Preserve }

private data class CalloutColors(val fill: Color, val border: Color, val ink: Color)

private fun colorsFor(variant: CalloutVariant): CalloutColors = when (variant) {
    CalloutVariant.Danger -> CalloutColors(Danger100, DangerBorder, Danger700)
    CalloutVariant.Caution -> CalloutColors(Caution100, CautionBorder, Caution700)
    CalloutVariant.Info -> CalloutColors(Blue050, BlueBorder, Blue600)
    CalloutVariant.Preserve -> CalloutColors(Preserve100, PreserveBorder, Preserve700)
}

/**
 * The safety / caution / info / preserve banner, in the four semantic
 * variants used throughout the app (safety-first notices, draft-only
 * warnings, informational panels, evidence-preservation notes).
 */
@Composable
fun CalloutBanner(
    icon: ImageVector,
    text: String,
    variant: CalloutVariant,
    modifier: Modifier = Modifier,
    kicker: String? = null
) {
    val colors = colorsFor(variant)
    AilexCard(
        modifier = modifier,
        shape = ShapeCardSm,
        fill = colors.fill,
        border = colors.border
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.space3)
        ) {
            Icon(icon, contentDescription = null, tint = colors.ink, modifier = Modifier.size(19.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (kicker != null) {
                    SectionKicker(text = kicker, color = colors.ink)
                }
                Text(text = text, style = MaterialTheme.typography.bodySmall, color = colors.ink)
            }
        }
    }
}

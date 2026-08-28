package com.example.ailex.features.live_situation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.domainIcon
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Preserve100
import com.example.ailex.ui.theme.Preserve700
import com.example.ailex.ui.theme.PreserveBorder
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Surface

/**
 * "Type an answer instead" for a specific question ([domain] set — the
 * text replaces that question's answer) and Category's "Something else"
 * (no domain — a general free-form description). Voice input ("Speak it
 * instead") opens `features/conversation/VoiceScreen`, not built yet in
 * this stage — [onSpeakInstead] is a no-op until then.
 */
@Composable
fun FreeTextScreen(
    domain: LegalDomain?,
    questionText: String,
    onBack: () -> Unit,
    onSubmit: (String) -> Unit,
    onSpeakInstead: () -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
        }
        if (domain != null) {
            Row(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 16.dp)
                    .background(domain.tileBackground, ShapePill)
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(domainIcon(domain), contentDescription = null, tint = domain.accentColor, modifier = Modifier.size(16.dp))
                Text(text = domain.displayName, style = TextStyle(fontSize = 12.5.sp), color = domain.accentColor)
            }
        }
        Text(
            text = questionText,
            style = TextStyle(fontSize = 21.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
            color = Ink900,
            modifier = Modifier.padding(top = if (domain == null) 12.dp else 0.dp, bottom = 8.dp)
        )
        Text(
            text = "Answer in your own words. English, हिंदी or मराठी.",
            style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
            color = Ink500,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 130.dp)
                .background(Surface, ShapeCardSm)
                .border(1.dp, Line300, ShapeCardSm)
                .padding(13.dp)
        ) {
            if (text.isEmpty()) {
                Text(text = "Type your answer…", style = TextStyle(fontSize = 15.sp, lineHeight = 23.sp), color = Ink500)
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(fontSize = 15.sp, lineHeight = 23.sp, color = Ink900),
                cursorBrush = SolidColor(Blue600),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .background(Preserve100, ShapeField)
                .border(1.dp, PreserveBorder, ShapeField)
                .clickable(onClick = onSpeakInstead)
                .padding(vertical = 10.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(Icons.Filled.Mic, contentDescription = null, tint = Preserve700, modifier = Modifier.size(18.dp))
            Text(text = "Speak it instead", style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium), color = Preserve700)
        }

        Spacer(modifier = Modifier.weight(1f))

        AilexCard(shape = ShapeField, fill = Background, border = Line200, modifier = Modifier.padding(bottom = 14.dp)) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(Icons.Filled.VisibilityOff, contentDescription = null, tint = Ink500, modifier = Modifier.size(17.dp))
                Text(
                    text = "Leave out names, licence numbers and vehicle numbers unless they change the answer.",
                    style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                    color = Ink600
                )
            }
        }

        PrimaryButton(
            text = "Continue",
            enabled = text.trim().length > 2,
            onClick = { onSubmit(text) }
        )
    }
}

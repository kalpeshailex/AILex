package com.example.ailex.features.live_situation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.theme.Danger050
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Danger700
import com.example.ailex.ui.theme.DangerBorder
import com.example.ailex.ui.theme.DangerBorderLt
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.Success500
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun SafetyScreen(
    onClose: () -> Unit,
    onSafe: () -> Unit,
    onUrgent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Danger050)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Ink600)
            }
            Text(text = "Live help", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = Danger700)
            Spacer(modifier = Modifier.size(44.dp))
        }
        Text(
            text = "First — is anyone in immediate danger?",
            style = Typography.headlineMedium,
            color = Danger700,
            modifier = Modifier.padding(top = 12.dp, bottom = 10.dp)
        )
        Text(
            text = "I'll only ask the details that may change the guidance. Answer this one first so I can put safety ahead of paperwork.",
            style = TextStyle(fontSize = 15.sp, lineHeight = 23.sp),
            color = Ink600,
            modifier = Modifier.padding(bottom = 26.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Material Symbols' "shield_with_heart" has no equivalent in the
            // classic set; "gpp_good" (a shield with a check) reads the same way.
            SafetyOption(
                icon = Icons.Filled.GppGood,
                iconTint = Success500,
                title = "I am safe right now",
                titleColor = Ink900,
                subtitle = "Continue with the situation",
                subtitleColor = Ink500,
                fill = Surface,
                border = Line200,
                pressedBorder = Line300,
                onClick = onSafe
            )
            SafetyOption(
                icon = Icons.Filled.Sos,
                iconTint = Danger600,
                title = "I need urgent help",
                titleColor = Danger700,
                subtitle = "Show emergency numbers first",
                subtitleColor = Danger600,
                fill = Danger100,
                border = DangerBorderLt,
                pressedBorder = DangerBorder,
                onClick = onUrgent
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        AilexCard(shape = ShapeCardSm, fill = Surface, border = Line200) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = Ink500, modifier = Modifier.size(19.dp))
                Text(
                    text = "This is legal first-aid, not legal representation. In an emergency, call 112 before using the app.",
                    style = TextStyle(fontSize = 13.sp, lineHeight = 20.sp),
                    color = Ink600
                )
            }
        }
    }
}

@Composable
private fun SafetyOption(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    titleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    fill: Color,
    border: Color,
    pressedBorder: Color,
    onClick: () -> Unit
) {
    AilexCard(fill = fill, border = border, pressedFill = fill, pressedBorder = pressedBorder, onClick = onClick) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Column {
                Text(text = title, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = titleColor)
                Text(
                    text = subtitle,
                    style = TextStyle(fontSize = 13.sp),
                    color = subtitleColor,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}

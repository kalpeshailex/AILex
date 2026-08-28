package com.example.ailex.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PhoneIphone
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
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Caution100
import com.example.ailex.ui.theme.Caution700
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Preserve100
import com.example.ailex.ui.theme.Preserve700
import com.example.ailex.ui.theme.Surface

private data class PrivacyRow(val icon: ImageVector, val tint: Color, val ink: Color, val title: String, val body: String)

/**
 * The local-only version of design_handoff_ailex_v1's Privacy and data
 * screen. The prototype's copy describes a backend ("our servers"); this
 * build has none, so every row below is re-worded to describe what this
 * app actually does, per CLAUDE.md's local-only constraint — nothing here
 * is lifted verbatim.
 */
private val PrivacyRows = listOf(
    PrivacyRow(
        Icons.Filled.PhoneIphone, Blue100, Blue600,
        "Your mobile number",
        "Held on this device only, to identify you in the app. No Aadhaar, address, date of birth or profession is collected."
    ),
    PrivacyRow(
        Icons.Filled.ChatBubble, Blue100, Blue600,
        "Conversations",
        "Ask Legal AI answers entirely on this device in this preview — nothing you type is sent anywhere. A conversation only becomes a saved incident when you choose to save it."
    ),
    PrivacyRow(
        Icons.Filled.MicOff, Preserve100, Preserve700,
        "Voice",
        "There is no live speech recognition in this preview — voice screens use a fixed example. Audio is never recorded or stored."
    ),
    PrivacyRow(
        Icons.Filled.Devices, Caution100, Caution700,
        "Saved incidents",
        "Stored on this device only. Uninstalling the app or clearing its data removes them, and there is no cloud backup."
    ),
    PrivacyRow(
        Icons.Filled.FolderOff, Caution100, Caution700,
        "Evidence files",
        "The app keeps a reference to a file already on your phone. Nothing is uploaded. If you move or delete the file, the reference stops working."
    )
)

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
            }
            Text(text = "Privacy and data", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = Ink900)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Text(
                text = "What this app holds, and where it stays.",
                style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                color = Ink600,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrivacyRows.forEach { row ->
                    AilexCard {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconTile(icon = row.icon, tint = row.tint, ink = row.ink, size = 36.dp)
                            Column {
                                Text(text = row.title, style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Bold), color = Navy900)
                                Text(
                                    text = row.body,
                                    style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp),
                                    color = Ink600,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

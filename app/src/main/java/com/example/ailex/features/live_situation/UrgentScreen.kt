package com.example.ailex.features.live_situation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.SectionKicker
import com.example.ailex.ui.theme.Danger050
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Danger700
import com.example.ailex.ui.theme.DangerBorder
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.Surface

private data class EmergencyRow(val label: String, val blurb: String, val number: String, val icon: ImageVector)

private val EmergencyRows = listOf(
    EmergencyRow("Emergency response", "Police, fire, ambulance", "112", Icons.Filled.Emergency),
    EmergencyRow("Police control room", "Mumbai Police", "100", Icons.Filled.LocalPolice),
    EmergencyRow("Women’s helpline", "24x7 support", "1091", Icons.Filled.SupportAgent),
    EmergencyRow("Cyber fraud reporting", "National helpline", "1930", Icons.Filled.Security)
)

@Composable
fun UrgentScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Danger050)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink600)
        }
        AilexCard(
            fill = Danger100,
            border = DangerBorder,
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.HealthAndSafety, contentDescription = null, tint = Danger600, modifier = Modifier.size(20.dp))
                    SectionKicker(text = "Safety first", color = Danger600)
                }
                Text(
                    text = "Get to a safe, public, well-lit place if you can. Do not physically resist or argue. Call for help before working out the legal position.",
                    style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                    color = Danger700,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 24.dp)) {
            EmergencyRows.forEach { row ->
                AilexCard(
                    shape = ShapeCardSm,
                    fill = Surface,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${row.number}")))
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        IconTile(icon = row.icon, tint = Danger100, ink = Danger600, size = 38.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = row.label, style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold), color = Ink900)
                            Text(
                                text = row.blurb,
                                style = TextStyle(fontSize = 12.5.sp, lineHeight = 17.sp),
                                color = Ink500,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Text(text = row.number, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold), color = Danger600)
                    }
                }
            }
        }
        PrimaryButton(text = "I'm safe now — continue", onClick = onContinue)
    }
}

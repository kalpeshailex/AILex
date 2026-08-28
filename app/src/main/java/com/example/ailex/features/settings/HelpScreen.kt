package com.example.ailex.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Balance
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.AilexFilterChip
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Surface

@Composable
fun HelpScreen(
    initialTopic: HelpTopic,
    onBack: () -> Unit
) {
    var topic by remember { mutableStateOf(initialTopic) }

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
            Text(text = "Understanding AILex", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold), color = Ink900)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                HelpTopic.entries.forEach { entry ->
                    AilexFilterChip(label = entry.label, selected = topic == entry, onClick = { topic = entry })
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HelpContent.byTopic.getValue(topic).forEach { entry ->
                    AilexCard {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(
                                text = entry.title,
                                style = TextStyle(fontSize = 15.5.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
                                color = Navy900,
                                modifier = Modifier.padding(bottom = 7.dp)
                            )
                            Text(text = entry.body, style = TextStyle(fontSize = 13.5.sp, lineHeight = 21.sp), color = Ink600)
                        }
                    }
                }
            }

            AilexCard(fill = Blue050, border = BlueBorder, modifier = Modifier.padding(top = 18.dp)) {
                Row(modifier = Modifier.padding(15.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.Balance, contentDescription = null, tint = Blue600, modifier = Modifier.size(20.dp))
                    Column {
                        Text(
                            text = "Free legal aid",
                            style = TextStyle(fontSize = 14.5.sp, fontWeight = FontWeight.Bold),
                            color = Navy900,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = "Eligible citizens can get advice and representation at no cost through the Maharashtra State Legal Services Authority, on 15100.",
                            style = TextStyle(fontSize = 13.sp, lineHeight = 20.sp),
                            color = Ink600
                        )
                    }
                }
            }
        }
    }
}

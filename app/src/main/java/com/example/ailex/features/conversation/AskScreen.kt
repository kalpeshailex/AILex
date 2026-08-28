package com.example.ailex.features.conversation

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ailex.domain.conversation.SuggestedPrompt
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.UiStateContent
import com.example.ailex.ui.components.domainIcon
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Preserve100
import com.example.ailex.ui.theme.Preserve700
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun AskScreen(
    sessionViewModel: AskLegalAiSessionViewModel,
    modifier: Modifier = Modifier,
    viewModel: AskLegalAiViewModel = viewModel(),
    onOpenConversation: () -> Unit = {},
    onStartVoice: () -> Unit = {},
    onIncidentsClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    UiStateContent(state = state, modifier = modifier) { content ->
        AskContentView(
            content = content,
            onSend = { text ->
                sessionViewModel.sendMessage(text)
                onOpenConversation()
            },
            onStartVoice = onStartVoice,
            onIncidentsClick = onIncidentsClick
        )
    }
}

@Composable
private fun AskContentView(
    content: AskLegalAiContent,
    onSend: (String) -> Unit,
    onStartVoice: () -> Unit,
    onIncidentsClick: () -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Surface)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 14.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = "Ask Legal AI", style = Typography.titleLarge, color = Navy900)
                    Text(
                        text = "Clear, practical guidance for your situation.",
                        style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                        color = Ink500,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Surface, ShapeChip)
                        .border(1.dp, Line200, ShapeChip)
                        .clickable(onClick = onIncidentsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.FolderOpen, contentDescription = "My Incidents", tint = Ink700, modifier = Modifier.size(21.dp))
                }
            }

            AilexCard(
                fill = Blue050,
                border = BlueBorder,
                modifier = Modifier.padding(top = 18.dp, bottom = 22.dp)
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "Ask about anything involving police, traffic, Mumbai railways, government services or cyber incidents.",
                        style = TextStyle(fontSize = 14.sp, lineHeight = 22.sp),
                        color = Ink700,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Psychology, contentDescription = null, tint = Blue600, modifier = Modifier.size(17.dp))
                        Text(
                            text = "I'll ask only the facts that change the answer, not your whole life story.",
                            style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                            color = Ink600
                        )
                    }
                }
            }

            Text(
                text = "Try one of these",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content.suggestedPrompts.forEach { prompt ->
                    SuggestedPromptRow(prompt = prompt, onClick = { onSend(prompt.text) })
                }
            }
        }

        AskComposer(
            value = input,
            onValueChange = { input = it },
            onSend = {
                if (input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            },
            onMicClick = onStartVoice
        )
    }
}

@Composable
private fun SuggestedPromptRow(prompt: SuggestedPrompt, onClick: () -> Unit) {
    AilexCard(onClick = onClick) {
        Row(
            modifier = Modifier.padding(vertical = 13.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(domainIcon(prompt.domain), contentDescription = null, tint = prompt.domain.accentColor, modifier = Modifier.size(18.dp))
            Text(
                text = prompt.text,
                style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
                color = Ink700,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.NorthEast, contentDescription = null, tint = Ink400, modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
private fun AskComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Line200)
        )
        Column(
            modifier = Modifier
                .background(Background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Surface, ShapeCard)
                    .border(1.dp, Line300, ShapeCard)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(text = "Describe what happened…", style = TextStyle(fontSize = 14.5.sp, lineHeight = 20.sp), color = Ink500)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(fontSize = 14.5.sp, lineHeight = 20.sp, color = Ink900),
                    cursorBrush = SolidColor(Blue600),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Preserve100, ShapeCard)
                    .clickable(onClick = onMicClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Voice input", tint = Preserve700, modifier = Modifier.size(23.dp))
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Blue600, ShapeCard)
                    .clickable(onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Surface, modifier = Modifier.size(22.dp))
            }
        }
        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Ink500, modifier = Modifier.size(15.dp))
            Text(
                text = "Never share OTPs, PINs, passwords, CVVs or UPI PINs here.",
                style = TextStyle(fontSize = 11.5.sp, lineHeight = 16.sp),
                color = Ink500
            )
        }
        }
    }
}

package com.example.ailex.features.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.domain.conversation.UpiFraudDemoTurn
import com.example.ailex.domain.incident.Incident
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.SourceCard
import com.example.ailex.ui.components.showToast
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Danger700
import com.example.ailex.ui.theme.DangerBorder
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Preserve100
import com.example.ailex.ui.theme.Preserve700
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Surface
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Composable
fun ConversationScreen(
    sessionViewModel: AskLegalAiSessionViewModel,
    onBack: () -> Unit,
    onEscalation: () -> Unit,
    onStartVoice: () -> Unit
) {
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }

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
            Text(
                text = session.topic,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }
            items(session.messages, key = { it.id }) { message ->
                when {
                    message.isUser -> UserBubble(text = message.text)
                    message.isRichDemo -> RichDemoTurn(onEscalation = onEscalation)
                    else -> AssistantBubble(text = message.text)
                }
            }
            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        ReplyComposer(
            value = input,
            onValueChange = { input = it },
            onSend = {
                if (input.isNotBlank()) {
                    sessionViewModel.sendMessage(input)
                    input = ""
                }
            },
            onMicClick = onStartVoice
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Text(
            text = text,
            style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp),
            color = Surface,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(Blue600, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun AssistantBubble(text: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Text(
            text = text,
            style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp),
            color = Ink700,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(Surface, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                .border(1.dp, Line200, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RichDemoTurn(onEscalation: () -> Unit) {
    val incidentsViewModel = LocalIncidentsViewModel.current
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    var legalOpen by remember { mutableStateOf(false) }
    var speaking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(fraction = 0.92f),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Urgency banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Danger100, ShapeCard)
                .border(1.dp, DangerBorder, ShapeCard)
                .padding(13.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = Danger600, modifier = Modifier.size(19.dp))
            Column {
                Text(
                    text = UpiFraudDemoTurn.UrgencyKicker.uppercase(),
                    style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.06.em),
                    color = Danger600,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = UpiFraudDemoTurn.UrgencyBody,
                    style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                    color = Danger700
                )
            }
        }

        // Answer card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, ShapeCard)
                .border(1.dp, Line200, ShapeCard)
                .padding(15.dp)
        ) {
            Text(
                text = UpiFraudDemoTurn.AnswerIntro,
                style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp),
                color = Ink700,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                UpiFraudDemoTurn.Steps.forEachIndexed { index, step ->
                    Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                        Box(
                            modifier = Modifier
                                .size(23.dp)
                                .background(Blue100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${index + 1}", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = Navy700)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = step.text, style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium), color = Ink900)
                            Text(
                                text = step.note,
                                style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                                color = Ink500,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))
            Column(modifier = Modifier.padding(top = 13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Block, contentDescription = null, tint = Danger600, modifier = Modifier.size(16.dp))
                    Text(text = UpiFraudDemoTurn.WarningNoShare, style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp), color = Ink600)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Inventory2, contentDescription = null, tint = Preserve700, modifier = Modifier.size(16.dp))
                    Text(text = UpiFraudDemoTurn.WarningPreserve, style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp), color = Ink600)
                }
            }
        }

        // Legal basis accordion
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, ShapeCard)
                .border(1.dp, Line200, ShapeCard)
        ) {
            Row(
                modifier = Modifier
                    .clickable { legalOpen = !legalOpen }
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Navy900, modifier = Modifier.size(19.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Legal basis", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = Ink900)
                    Text(text = "2 verified sources", style = TextStyle(fontSize = 12.sp), color = Ink500, modifier = Modifier.padding(top = 2.dp))
                }
                Icon(
                    imageVector = if (legalOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = Ink500
                )
            }
            if (legalOpen) {
                Column(
                    modifier = Modifier.padding(start = 13.dp, end = 13.dp, bottom = 13.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    UpiFraudDemoTurn.Sources.forEach { source ->
                        SourceCard(
                            title = source.title,
                            excerpt = source.excerpt,
                            authority = source.authority,
                            lastVerified = source.lastVerified.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                        )
                    }
                }
            }
        }

        // Action chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip(
                icon = Icons.Filled.BookmarkAdd,
                label = "Save this situation",
                fill = Blue100,
                border = BlueBorder,
                ink = Navy700,
                onClick = {
                    incidentsViewModel.addIncident(
                        Incident(
                            id = UUID.randomUUID().toString(),
                            domain = LegalDomain.CYBER,
                            title = UpiFraudDemoTurn.Topic,
                            status = IncidentStatus.ACTIVE,
                            summary = UpiFraudDemoTurn.AnswerIntro
                        )
                    )
                    scope.launch { toastHost.showToast("Situation saved") }
                }
            )
            ActionChip(icon = Icons.AutoMirrored.Filled.AltRoute, label = "Escalation", fill = Surface, border = Line200, ink = Ink700, onClick = onEscalation)
            IconOnlyChip(
                icon = if (speaking) Icons.Filled.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (speaking) "Stop" else "Play",
                onClick = { speaking = !speaking }
            )
        }

        // Follow-up card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, ShapeCard)
                .border(1.dp, Line200, ShapeCard)
                .padding(15.dp)
        ) {
            Text(
                text = UpiFraudDemoTurn.FollowUpQuestion,
                style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp),
                color = Ink700,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                UpiFraudDemoTurn.FollowUpChips.forEach { chip ->
                    Text(
                        text = chip,
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        color = Ink700,
                        modifier = Modifier
                            .background(Background, ShapePill)
                            .border(1.dp, Line300, ShapePill)
                            .clickable {
                                scope.launch { toastHost.showToast("Follow-up recorded. Next fact only if it changes the answer.") }
                            }
                            .padding(horizontal = 13.dp, vertical = 9.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionChip(
    icon: ImageVector,
    label: String,
    fill: Color,
    border: Color,
    ink: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(fill, ShapePill)
            .border(1.dp, border, ShapePill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = ink, modifier = Modifier.size(16.dp))
        Text(text = label, style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = ink)
    }
}

@Composable
private fun IconOnlyChip(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(Surface, ShapePill)
            .border(1.dp, Line200, ShapePill)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = Ink700, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ReplyComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Background)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Surface, ShapeCard)
                    .border(1.dp, Line300, ShapeCard)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(text = "Reply…", style = TextStyle(fontSize = 14.5.sp, lineHeight = 20.sp), color = Ink500)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(fontSize = 14.5.sp, lineHeight = 20.sp, color = Ink900),
                    cursorBrush = SolidColor(Blue600),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (value.isBlank()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Preserve100, ShapeCard)
                        .clickable(onClick = onMicClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice input", tint = Preserve700, modifier = Modifier.size(23.dp))
                }
            } else {
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
        }
    }
}

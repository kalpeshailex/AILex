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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ailex.core.network.ConversationTurn
import com.example.ailex.domain.incident.Incident
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.SourceCard
import com.example.ailex.ui.components.showToast
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue050
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
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Surface
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ConversationScreen(
    sessionViewModel: AskLegalAiSessionViewModel,
    onBack: () -> Unit,
    onEscalation: (LegalDomain?) -> Unit,
    onStartVoice: () -> Unit
) {
    val session by sessionViewModel.session.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(session.messages.size) {
        if (session.messages.isNotEmpty()) {
            // Item indices: 0 = leading spacer, 1..size = messages, size+1 = trailing
            // spacer -- scrolling to the trailing spacer brings the true bottom of the
            // newest message into view, not just its top (answer cards can be tall).
            listState.animateScrollToItem(session.messages.size + 1)
        }
    }

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
            state = listState,
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
                    message.isPending -> PendingBubble()
                    message.answer != null -> AnswerCard(turn = message.answer, onEscalation = onEscalation)
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

@Composable
private fun PendingBubble() {
    AssistantBubble(text = "Thinking…")
}

@Composable
private fun UrgencyBanner(turn: ConversationTurn) {
    if (turn.riskLevel != "HIGH" && turn.riskLevel != "CRITICAL") return
    val kicker = if (turn.riskLevel == "CRITICAL") "Act now" else "Important safety note"
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
                text = kicker.uppercase(),
                style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.06.em),
                color = Danger600,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = turn.riskReason.ifBlank { "This situation may need urgent attention." },
                style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                color = Danger700
            )
        }
    }
}

@Composable
private fun LabeledBulletGroup(label: String, items: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(text = label, style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Bold), color = Ink900, modifier = Modifier.padding(bottom = 6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEach { item ->
                Text(text = "•  $item", style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp), color = Ink700)
            }
        }
    }
}

/**
 * Renders one real AI-pipeline answer (`ConversationTurn`, from
 * `POST /conversation/message`). Replaces the old hand-scripted UPI-fraud
 * demo turn — every field here is whatever the backend actually returned,
 * including an honest "I don't have enough verified information" summary
 * with empty rights/obligations/citations when there's no verified legal
 * evidence yet (see backend/src/legal/LegalKnowledgeService.ts).
 */
@Composable
private fun AnswerCard(turn: ConversationTurn, onEscalation: (LegalDomain?) -> Unit) {
    val incidentsViewModel = LocalIncidentsViewModel.current
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    var legalOpen by remember { mutableStateOf(false) }
    var speaking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(fraction = 0.92f),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UrgencyBanner(turn)

        // Answer card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, ShapeCard)
                .border(1.dp, Line200, ShapeCard)
                .padding(15.dp)
        ) {
            Text(
                text = turn.summary,
                style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            if (turn.situation.isNotBlank() && turn.situation != turn.summary) {
                Text(
                    text = turn.situation,
                    style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                    color = Ink600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // The single most important next step when the AI still needs
            // something from you -- shown right up front, before generic
            // actions or any save/escalate options, so it reads as "answer
            // this next" rather than an afterthought tacked onto the end.
            if (turn.needsFollowUp && !turn.nextQuestion.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 14.dp)
                        .fillMaxWidth()
                        .background(Blue050, ShapeCardSm)
                        .border(1.dp, BlueBorder, ShapeCardSm)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
                    Column {
                        Text(
                            text = "ONE MORE THING",
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.06.em),
                            color = Blue600,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                        Text(
                            text = turn.nextQuestion,
                            style = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
                            color = Ink900
                        )
                    }
                }
            }

            if (turn.actions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                    turn.actions.forEachIndexed { index, action ->
                        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                            Box(
                                modifier = Modifier.size(23.dp).background(Blue100, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${index + 1}", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = Navy700)
                            }
                            Text(
                                text = action.step,
                                style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium),
                                color = Ink900,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (turn.rights.isNotEmpty()) LabeledBulletGroup("Your rights", turn.rights)
            if (turn.obligations.isNotEmpty()) LabeledBulletGroup("Your position", turn.obligations)
            if (turn.authorityPowers.isNotEmpty()) LabeledBulletGroup("Authority powers", turn.authorityPowers)

            if (turn.avoid.isNotEmpty() || turn.preserve.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))
                Column(modifier = Modifier.padding(top = 13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    turn.avoid.forEach { item ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Block, contentDescription = null, tint = Danger600, modifier = Modifier.size(16.dp))
                            Text(text = item, style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp), color = Ink600)
                        }
                    }
                    turn.preserve.forEach { item ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Inventory2, contentDescription = null, tint = Preserve700, modifier = Modifier.size(16.dp))
                            Text(text = item, style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp), color = Ink600)
                        }
                    }
                }
            }
        }

        // Legal basis accordion — only when the pipeline actually found verified evidence.
        if (turn.citations.isNotEmpty()) {
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
                        Text(
                            text = "${turn.citations.size} verified source${if (turn.citations.size == 1) "" else "s"}",
                            style = TextStyle(fontSize = 12.sp),
                            color = Ink500,
                            modifier = Modifier.padding(top = 2.dp)
                        )
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
                        turn.citations.forEach { citation ->
                            SourceCard(
                                title = citation.title,
                                excerpt = citation.sectionReference ?: citation.officialUrl ?: "See official source for full text.",
                                authority = citation.jurisdiction,
                                lastVerified = citation.effectiveDate ?: "Not specified"
                            )
                        }
                    }
                }
            }
        }

        // Action chips — only once there's something real to save/escalate.
        // A bare clarification turn (no actions/rights/obligations/authority
        // powers/citations yet) has nothing worth these options; showing them
        // anyway reads as "the conversation is done" right when the AI is
        // still waiting on you to answer the question above.
        val hasSubstantiveContent = turn.actions.isNotEmpty() ||
            turn.rights.isNotEmpty() ||
            turn.obligations.isNotEmpty() ||
            turn.authorityPowers.isNotEmpty() ||
            turn.citations.isNotEmpty()

        if (hasSubstantiveContent) {
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
                                domain = turn.legalDomain ?: LegalDomain.POLICE,
                                title = turn.displayTitle,
                                status = IncidentStatus.ACTIVE,
                                summary = turn.summary
                            )
                        )
                        scope.launch { toastHost.showToast("Situation saved") }
                    }
                )
                ActionChip(
                    icon = Icons.AutoMirrored.Filled.AltRoute,
                    label = "Escalation",
                    fill = Surface,
                    border = Line200,
                    ink = Ink700,
                    onClick = { onEscalation(turn.legalDomain) }
                )
                IconOnlyChip(
                    icon = if (speaking) Icons.Filled.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (speaking) "Stop" else "Play",
                    onClick = { speaking = !speaking }
                )
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

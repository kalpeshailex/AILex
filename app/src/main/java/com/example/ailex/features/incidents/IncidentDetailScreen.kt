package com.example.ailex.features.incidents

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.domain.incident.Incident
import com.example.ailex.domain.incident.IncidentTimelineEvent
import com.example.ailex.ui.components.AilexBottomSheet
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.SecondaryButton
import com.example.ailex.ui.components.StatusPill
import com.example.ailex.ui.components.TimelineItem
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Danger050
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.DangerBorder
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line100
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy700
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Surface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncidentDetailScreen(
    incidentId: String,
    onBack: () -> Unit,
    onCreateComplaintDraft: (String) -> Unit,
    onEscalation: (LegalDomain) -> Unit,
    onDeleted: () -> Unit
) {
    val viewModel = LocalIncidentsViewModel.current
    val incidents by viewModel.incidents.collectAsStateWithLifecycle()
    val incident = incidents.find { it.id == incidentId } ?: return
    var noteDraft by remember(incident.id) { mutableStateOf(incident.notes) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAddEvent by remember { mutableStateOf(false) }

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
                text = "Incident",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Surface, ShapeChip)
                    .border(1.dp, Line200, ShapeChip)
                    .clickable { showDeleteConfirm = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete incident", tint = Danger600, modifier = Modifier.size(20.dp))
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(bottom = 9.dp)) {
                Text(
                    text = incident.domain.displayName.uppercase(),
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                    color = incident.domain.accentColor
                )
                StatusPill(status = incident.status)
            }
            Text(
                text = incident.title,
                style = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
                color = Navy900,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = incident.savedDetail ?: "Saved ${SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()).format(Date(incident.savedAt))}",
                style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp),
                color = Ink500,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Blue050, ShapeCardSm)
                    .border(1.dp, BlueBorder, ShapeCardSm)
                    .padding(15.dp)
            ) {
                Text(
                    text = "SUMMARY",
                    style = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.Medium),
                    color = Blue600
                )
                Text(
                    text = incident.summary.ifBlank { "No summary yet." },
                    style = TextStyle(fontSize = 14.sp, lineHeight = 22.sp),
                    color = Ink700,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            SectionTitle(text = "Key facts", modifier = Modifier.padding(top = 22.dp, bottom = 10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface, ShapeCardSm)
                    .border(1.dp, Line200, ShapeCardSm)
            ) {
                incident.keyFacts.forEachIndexed { index, (key, value) ->
                    if (index > 0) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line100))
                    }
                    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = key,
                            style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
                            color = Ink500,
                            modifier = Modifier.width(118.dp)
                        )
                        Text(
                            text = value,
                            style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium),
                            color = Ink900,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SectionTitle(text = "Timeline", modifier = Modifier.padding(top = 22.dp, bottom = 10.dp))
            Column {
                incident.timeline.forEachIndexed { index, event ->
                    TimelineItem(
                        title = event.title,
                        `when` = event.whenText,
                        dotColor = event.dotColor,
                        body = event.body?.takeIf { it.isNotBlank() },
                        isLast = index == incident.timeline.lastIndex
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Line300, ShapeField)
                    .clickable { showAddEvent = true }
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
                Text(text = "Add a timeline event", style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium), color = Blue600)
            }

            SectionTitle(text = "Notes", modifier = Modifier.padding(top = 24.dp, bottom = 10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 92.dp)
                    .background(Surface, ShapeCardSm)
                    .border(1.dp, Line300, ShapeCardSm)
                    .padding(13.dp)
            ) {
                if (noteDraft.isEmpty()) {
                    Text(text = "Add anything you want to remember…", style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp), color = Ink500)
                }
                BasicTextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    textStyle = TextStyle(fontSize = 14.sp, lineHeight = 21.sp, color = Ink900),
                    cursorBrush = SolidColor(Blue600),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 9.dp)
                    .background(Blue100, ShapeField)
                    .border(1.dp, BlueBorder, ShapeField)
                    .clickable { viewModel.updateNotes(incident.id, noteDraft) }
                    .padding(vertical = 10.dp, horizontal = 14.dp)
            ) {
                Text(text = "Save note", style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium), color = Navy700)
            }

            SectionTitle(text = "Evidence on this device", modifier = Modifier.padding(top = 24.dp, bottom = 10.dp))
            if (incident.evidence.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Line300, ShapeCardSm)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No files linked yet. Photos, receipts and screenshots you attach stay on this device.",
                        style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp),
                        color = Ink500
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    incident.evidence.forEach { evidence ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (evidence.available) Surface else Danger050, ShapeCardSm)
                                .border(1.dp, if (evidence.available) Line200 else DangerBorder, ShapeCardSm)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            Icon(
                                imageVector = if (evidence.available) Icons.Filled.Description else Icons.Filled.BrokenImage,
                                contentDescription = null,
                                tint = if (evidence.available) Blue600 else Danger600,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = evidence.displayName,
                                    style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium),
                                    color = Ink900,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = evidence.meta,
                                    style = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
                                    color = if (evidence.available) Ink500 else Danger600,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = "These are references to files already on your phone. Nothing is uploaded.",
                style = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
                color = Ink500,
                modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton(
                    text = "Create complaint draft",
                    onClick = { onCreateComplaintDraft(incident.id) },
                    leadingIcon = Icons.Filled.Description
                )
                SecondaryButton(
                    text = "View escalation route",
                    onClick = { onEscalation(incident.domain) },
                    leadingIcon = Icons.AutoMirrored.Filled.AltRoute,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this incident?") },
            text = { Text("This removes it and its notes, timeline and draft from this device. There is no recovery.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteIncident(incident.id)
                    showDeleteConfirm = false
                    onDeleted()
                }) { Text("Delete", color = Danger600) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showAddEvent) {
        var eventText by remember { mutableStateOf("") }
        AilexBottomSheet(onDismissRequest = { showAddEvent = false }) {
            Text(
                text = "Add a timeline event",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .background(Background, ShapeCardSm)
                    .border(1.dp, Line300, ShapeCardSm)
                    .padding(13.dp)
            ) {
                if (eventText.isEmpty()) {
                    Text(text = "What happened?", style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp), color = Ink500)
                }
                BasicTextField(
                    value = eventText,
                    onValueChange = { eventText = it },
                    textStyle = TextStyle(fontSize = 14.sp, lineHeight = 21.sp, color = Ink900),
                    cursorBrush = SolidColor(Blue600),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            PrimaryButton(
                text = "Add",
                enabled = eventText.trim().length > 2,
                onClick = {
                    viewModel.addTimelineEvent(
                        incident.id,
                        IncidentTimelineEvent(title = eventText.trim(), whenText = "Just now", dotColor = Blue600)
                    )
                    showAddEvent = false
                }
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
        color = Ink900,
        modifier = modifier
    )
}

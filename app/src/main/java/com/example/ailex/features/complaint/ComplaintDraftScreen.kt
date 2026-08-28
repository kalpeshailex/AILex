package com.example.ailex.features.complaint

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.domain.complaint.ComplaintDraftTemplates
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.ui.components.CalloutBanner
import com.example.ailex.ui.components.CalloutVariant
import com.example.ailex.ui.components.SectionKicker
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.Surface
import android.content.ClipData
import android.content.Intent
import kotlinx.coroutines.launch

@Composable
fun ComplaintDraftScreen(
    incidentId: String,
    onBack: () -> Unit
) {
    val incidentsViewModel = LocalIncidentsViewModel.current
    val incidents by incidentsViewModel.incidents.collectAsStateWithLifecycle()
    val incident = incidents.find { it.id == incidentId } ?: return
    val template = ComplaintDraftTemplates.byDomain[incident.domain].orEmpty()
    var editing by remember { mutableStateOf(false) }
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val sections = template.mapIndexed { index, section ->
        section.label to (incident.complaintEdits[index] ?: section.text)
    }
    val isEdited = incident.complaintEdits.isNotEmpty()

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
                text = "Draft · ${incident.domain.displayName}",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .background(Surface, ShapeChip)
                    .border(1.dp, Line200, ShapeChip)
                    .clickable { editing = !editing }
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(text = if (editing) "Done" else "Edit", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = Blue600)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Line200))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            CalloutBanner(
                icon = Icons.Filled.EditNote,
                text = "Draft only. Fill in every square bracket and review it before sharing. The app does not file complaints for you.",
                variant = CalloutVariant.Caution,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Text(
                text = "Generated for: ${incident.title}",
                style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                color = Ink500,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (isEdited) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blue050, ShapeChip)
                        .border(1.dp, BlueBorder, ShapeChip)
                        .padding(horizontal = 13.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(Icons.Filled.History, contentDescription = null, tint = Blue600, modifier = Modifier.size(17.dp))
                    Text(
                        text = "You have edited this draft.",
                        style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                        color = Ink700,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Reset",
                        style = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline),
                        color = Blue600,
                        modifier = Modifier.clickable { incidentsViewModel.resetComplaintEdits(incident.id) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface, ShapeCard)
                    .border(1.dp, Line200, ShapeCard)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sections.forEachIndexed { index, (label, text) ->
                    Column {
                        SectionKicker(text = label, color = Ink400, modifier = Modifier.padding(bottom = 7.dp))
                        if (editing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 80.dp)
                                    .background(Blue050, ShapeChip)
                                    .border(1.dp, Blue600, ShapeChip)
                                    .padding(11.dp)
                            ) {
                                BasicTextField(
                                    value = text,
                                    onValueChange = { incidentsViewModel.updateComplaintSection(incident.id, index, it) },
                                    textStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, color = Ink900),
                                    cursorBrush = SolidColor(Blue600),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Text(text = text, style = TextStyle(fontSize = 14.sp, lineHeight = 22.sp), color = Ink700)
                        }
                    }
                }
            }

            val draftText = sections.joinToString("\n\n") { (label, text) -> "$label:\n$text" }
            Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(Surface, ShapeCard)
                        .border(1.dp, Line300, ShapeCard)
                        .clickable {
                            scope.launch {
                                clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Complaint draft", draftText)))
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Ink700, modifier = Modifier.size(19.dp))
                    Text(text = "Copy", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium), color = Ink700)
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(Blue600, ShapeCard)
                        .clickable {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, draftText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share complaint draft"))
                        },
                    horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = Surface, modifier = Modifier.size(19.dp))
                    Text(text = "Share", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = Surface)
                }
            }
            Text(
                text = "This draft states facts you provided. It does not allege that any officer or department acted illegally.",
                style = TextStyle(fontSize = 11.5.sp, lineHeight = 17.sp),
                color = Ink400,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

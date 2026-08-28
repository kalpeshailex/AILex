package com.example.ailex.features.live_situation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.domain.legal.ActionStep
import com.example.ailex.domain.legal.RiskLevel
import com.example.ailex.domain.legal.SectionId
import com.example.ailex.domain.legal.SectionItemIcon
import com.example.ailex.domain.legal.SituationResult
import com.example.ailex.domain.incident.Incident
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.ui.components.Accordion
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.CalloutBanner
import com.example.ailex.ui.components.CalloutVariant
import com.example.ailex.ui.components.LocalToastHostState
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.SectionKicker
import com.example.ailex.ui.components.SourceCard
import com.example.ailex.ui.components.StepItem
import com.example.ailex.ui.components.showToast
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Caution700
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line100
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Preserve700
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Success500
import com.example.ailex.ui.theme.Surface
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(
    viewModel: LiveSituationViewModel,
    onBack: () -> Unit,
    onEscalation: () -> Unit,
    onDone: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val incidentsViewModel = LocalIncidentsViewModel.current
    val toastHost = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val result = state.result

    val title = when {
        state.isGeneral -> "Something else"
        result != null -> result.title
        else -> "Result"
    }

    fun saveIncident() {
        val domain = state.domain
        if (domain != null && result != null) {
            incidentsViewModel.addIncident(
                Incident(
                    id = UUID.randomUUID().toString(),
                    domain = domain,
                    title = result.title,
                    dateLocation = viewModel.locationAnswer(),
                    status = IncidentStatus.ACTIVE,
                    summary = result.situationSummary,
                    keyFacts = viewModel.answeredFacts()
                )
            )
        }
        scope.launch { toastHost.showToast("Situation saved") }
        onDone()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky app bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(start = 8.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink700)
            }
            Text(
                text = title,
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
                    .clickable(onClick = viewModel::toggleSpeaking),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (state.speaking) Icons.Filled.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = if (state.speaking) "Stop speaking" else "Read aloud",
                    tint = Blue600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Line200)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            if (state.isGeneral || result == null) {
                GeneralResultContent(
                    description = state.generalDescription,
                    onEscalation = onEscalation,
                    onSave = ::saveIncident,
                    onNotNow = onDone
                )
            } else {
                DomainResultContent(
                    result = result,
                    completedSteps = state.completedSteps,
                    expandedSections = state.expandedSections,
                    onToggleStep = viewModel::toggleStepDone,
                    onToggleSection = viewModel::toggleSection,
                    onEscalation = onEscalation,
                    onSave = ::saveIncident
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DomainResultContent(
    result: SituationResult,
    completedSteps: Set<Int>,
    expandedSections: Set<SectionId>,
    onToggleStep: (Int) -> Unit,
    onToggleSection: (SectionId) -> Unit,
    onEscalation: () -> Unit,
    onSave: () -> Unit
) {
    if (result.risk == RiskLevel.HIGH && result.safetyNote != null) {
        CalloutBanner(
            icon = Icons.Filled.HealthAndSafety,
            text = result.safetyNote,
            variant = CalloutVariant.Danger,
            kicker = "Safety first",
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    AilexCard(modifier = Modifier.padding(bottom = 18.dp)) {
        Column(modifier = Modifier.padding(15.dp)) {
            SectionKicker(text = "What I understand", color = Ink400, modifier = Modifier.padding(bottom = 8.dp))
            Text(
                text = result.situationSummary,
                style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp),
                color = Ink700,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                result.factChips.forEach { fact ->
                    Text(
                        text = fact,
                        style = TextStyle(fontSize = 12.sp),
                        color = Ink700,
                        modifier = Modifier
                            .background(Line100, ShapePill)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Icon(Icons.Filled.Checklist, contentDescription = null, tint = Blue600, modifier = Modifier.size(20.dp))
        Text(text = "What to do now", style = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold), color = Ink900)
    }
    Column(verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.padding(bottom = 14.dp)) {
        result.actionSteps.forEachIndexed { index, step: ActionStep ->
            StepItem(
                number = index + 1,
                text = step.text,
                note = step.note,
                checked = index in completedSteps,
                onToggle = { onToggleStep(index) }
            )
        }
    }
    Text(
        text = "Tap a step to mark it done. ${completedSteps.size} of ${result.actionSteps.size} done.",
        style = TextStyle(fontSize = 12.5.sp),
        color = Ink500,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 18.dp)) {
        result.sections.forEach { section ->
            val (icon, ink) = sectionIconAndInk(section.id)
            Accordion(
                icon = icon,
                iconTint = ink,
                title = section.title,
                meta = section.meta,
                expanded = section.id in expandedSections,
                onToggle = { onToggleSection(section.id) }
            ) {
                section.items.forEach { item ->
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        val (itemIcon, itemInk) = itemIconAndInk(item.icon)
                        Icon(itemIcon, contentDescription = null, tint = itemInk, modifier = Modifier.size(17.dp))
                        Text(text = item.text, style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp), color = Ink700)
                    }
                }
                section.sources.forEach { source ->
                    SourceCard(
                        title = source.title,
                        excerpt = source.excerpt,
                        authority = source.authority,
                        lastVerified = source.lastVerified.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                    )
                }
                if (section.caveat != null) {
                    CalloutBanner(icon = Icons.AutoMirrored.Filled.Help, text = section.caveat, variant = CalloutVariant.Caution)
                }
            }
        }
    }

    EscalationRow(blurb = result.escalationBlurb, onClick = onEscalation)
    Spacer(modifier = Modifier.height(10.dp))
    PrimaryButton(text = "Save this situation", onClick = onSave, leadingIcon = Icons.Filled.BookmarkAdd)

    FeedbackAndDisclaimer()
}

@Composable
private fun GeneralResultContent(
    description: String,
    onEscalation: () -> Unit,
    onSave: () -> Unit,
    onNotNow: () -> Unit
) {
    AilexCard(modifier = Modifier.padding(bottom = 18.dp)) {
        Column(modifier = Modifier.padding(15.dp)) {
            SectionKicker(text = "What I understand", color = Ink400, modifier = Modifier.padding(bottom = 8.dp))
            Text(
                text = description.ifBlank { "You described a situation that doesn't match the five covered areas yet." },
                style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp),
                color = Ink700
            )
        }
    }
    Text(
        text = "I don't have enough verified information to answer this reliably.",
        style = TextStyle(fontSize = 14.5.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium),
        color = Ink900,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Try describing it under the closest of Police, Traffic, Mumbai Local, Government / RTS or Cyber instead — or contact the Maharashtra State Legal Services Authority for free legal advice.",
        style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
        color = Ink600,
        modifier = Modifier.padding(bottom = 20.dp)
    )
    EscalationRow(blurb = "Maharashtra State Legal Services Authority — 15100", onClick = onEscalation)
    Spacer(modifier = Modifier.height(10.dp))
    PrimaryButton(text = "Save this situation", onClick = onSave, leadingIcon = Icons.Filled.BookmarkAdd)
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "Not now",
        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
        color = Blue600,
        modifier = Modifier.clickable(onClick = onNotNow)
    )
}

@Composable
private fun EscalationRow(blurb: String, onClick: () -> Unit) {
    AilexCard(
        shape = ShapeCardSm,
        fill = Blue050,
        border = BlueBorder,
        pressedFill = Blue100,
        pressedBorder = BlueBorder,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = null, tint = Blue600, modifier = Modifier.size(21.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Where to escalate", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold), color = Navy900)
                Text(
                    text = blurb,
                    style = TextStyle(fontSize = 12.5.sp, lineHeight = 17.sp),
                    color = Ink600,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Blue600)
        }
    }
}

@Composable
private fun FeedbackAndDisclaimer() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Line200)
        )
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "Was this useful?",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                color = Ink700,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeedbackButton(icon = Icons.Filled.ThumbUp, label = "Yes", modifier = Modifier.weight(1f))
                FeedbackButton(icon = Icons.Filled.ThumbDown, label = "No", modifier = Modifier.weight(1f))
                FeedbackButton(icon = Icons.Filled.Flag, label = "Incorrect", ink = Danger600)
            }
            Text(
                text = "AILex gives legal first-aid based on verified sources. It is not a lawyer and does not guarantee any outcome.",
                style = TextStyle(fontSize = 11.5.sp, lineHeight = 17.sp),
                color = Ink400,
                modifier = Modifier.padding(top = 14.dp)
            )
        }
    }
}

@Composable
private fun FeedbackButton(icon: ImageVector, label: String, modifier: Modifier = Modifier, ink: Color = Ink700) {
    Row(
        modifier = modifier
            .background(Surface, ShapeChip)
            .border(1.dp, Line200, ShapeChip)
            .clickable { }
            .padding(vertical = 12.dp, horizontal = 13.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ink, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Medium), color = ink)
    }
}

private fun sectionIconAndInk(id: SectionId): Pair<ImageVector, Color> = when (id) {
    SectionId.POSITION -> Icons.Filled.AssignmentInd to Ink700
    SectionId.RIGHTS -> Icons.Filled.Gavel to Blue600
    SectionId.POWERS -> Icons.Filled.Shield to Caution700
    SectionId.IMPROPER -> Icons.Filled.Report to Danger600
    SectionId.AVOID -> Icons.Filled.DoNotDisturbOn to Danger600
    SectionId.PRESERVE -> Icons.Filled.Inventory2 to Preserve700
    SectionId.LEGAL -> Icons.AutoMirrored.Filled.MenuBook to Navy900
}

private fun itemIconAndInk(icon: SectionItemIcon): Pair<ImageVector, Color> = when (icon) {
    SectionItemIcon.CHECK -> Icons.Filled.Check to Success500
    SectionItemIcon.CLOSE -> Icons.Filled.Close to Danger600
    SectionItemIcon.ARROW_RIGHT -> Icons.AutoMirrored.Filled.KeyboardArrowRight to Ink500
}

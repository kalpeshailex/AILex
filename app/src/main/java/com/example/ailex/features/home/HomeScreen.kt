package com.example.ailex.features.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.core.common.LocalAppViewModel
import com.example.ailex.domain.incident.Incident
import com.example.ailex.features.incidents.LocalIncidentsViewModel
import com.example.ailex.features.settings.LocalNotificationsViewModel
import com.example.ailex.ui.components.AilexBottomSheet
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.DangerButton
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.components.SectionKicker
import com.example.ailex.ui.components.PrimaryButton
import com.example.ailex.ui.components.UiStateContent
import com.example.ailex.ui.components.domainIcon
import com.example.ailex.ui.theme.Background
import com.example.ailex.ui.theme.Blue050
import com.example.ailex.ui.theme.Blue100
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.BlueBorder
import com.example.ailex.ui.theme.Danger050
import com.example.ailex.ui.theme.Danger100
import com.example.ailex.ui.theme.Danger500
import com.example.ailex.ui.theme.Danger600
import com.example.ailex.ui.theme.Danger700
import com.example.ailex.ui.theme.DangerBorderLt
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.Preserve500
import com.example.ailex.ui.theme.ShapeCardLg
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.ShapePill
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Surface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onStartLiveSituation: () -> Unit = {},
    onDomainClick: (String) -> Unit = {},
    onIncidentClick: (String) -> Unit = {},
    onSeeAllIncidentsClick: () -> Unit = {},
    onAskLegalAiClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    UiStateContent(state = state, modifier = modifier) { content ->
        HomeContentView(
            content = content,
            onStartLiveSituation = onStartLiveSituation,
            onDomainClick = onDomainClick,
            onIncidentClick = onIncidentClick,
            onSeeAllIncidentsClick = onSeeAllIncidentsClick,
            onAskLegalAiClick = onAskLegalAiClick,
            onNotificationsClick = onNotificationsClick
        )
    }
}

@Composable
private fun HomeContentView(
    content: HomeContent,
    onStartLiveSituation: () -> Unit,
    onDomainClick: (String) -> Unit,
    onIncidentClick: (String) -> Unit,
    onSeeAllIncidentsClick: () -> Unit,
    onAskLegalAiClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val appState by LocalAppViewModel.current.state.collectAsStateWithLifecycle()
    val incidents by LocalIncidentsViewModel.current.incidents.collectAsStateWithLifecycle()
    val unreadCount by LocalNotificationsViewModel.current.unreadCount.collectAsStateWithLifecycle()
    var showCoverageSheet by remember { mutableStateOf(false) }
    val firstName = appState.displayName.trim().substringBefore(' ').ifBlank { null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.screenPaddingTop, start = Spacing.screenHorizontal, end = Spacing.screenHorizontal, bottom = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = if (firstName != null) "Hello, $firstName" else "Hello",
                    style = TextStyle(fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
                    color = Navy900
                )
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { showCoverageSheet = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.Place, contentDescription = null, tint = Ink500, modifier = Modifier.size(15.dp))
                    Text(text = "Mumbai / MMR", style = TextStyle(fontSize = 13.sp), color = Ink500)
                    Icon(Icons.Filled.Info, contentDescription = null, tint = Ink500, modifier = Modifier.size(15.dp))
                }
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Surface, ShapeField)
                    .border(1.dp, Line200, ShapeField)
                    .clickable(onClick = onNotificationsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Ink700, modifier = Modifier.size(21.dp))
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(8.dp)
                            .background(Danger500, CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live help
        AilexCard(shape = ShapeCardLg, fill = Danger050, border = DangerBorderLt) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Material Symbols' "emergency_home" glyph has no equivalent in the
                    // classic Material Icons set material-icons-extended ships; "emergency" is the closest match.
                    Icon(Icons.Filled.Emergency, contentDescription = null, tint = Danger600, modifier = Modifier.size(20.dp))
                    SectionKicker(text = "Live help", color = Danger600)
                }
                Text(
                    text = "Something happening right now?",
                    style = TextStyle(fontSize = 22.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp),
                    color = Danger700,
                    modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                )
                Text(
                    text = "Get step-by-step help for your situation.",
                    style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                    color = Ink600,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                DangerButton(
                    text = "Start live help",
                    onClick = onStartLiveSituation,
                    leadingIcon = Icons.Filled.Bolt
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Quick guidance", "Know your rights", "Stay safe").forEach { cue ->
                        Text(
                            text = cue,
                            style = TextStyle(fontSize = 11.5.sp, fontWeight = FontWeight.Medium),
                            color = Danger700,
                            modifier = Modifier
                                .weight(1f)
                                .background(Danger100, ShapePill)
                                .padding(vertical = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Browse by area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = "Browse by area", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold), color = Ink900)
            Text(text = "5 areas covered", style = TextStyle(fontSize = 12.sp), color = Ink500)
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(content.domains, key = { it.id }) { domain ->
                DomainGridTile(domain = domain, modifier = Modifier.width(230.dp), onClick = { onDomainClick(domain.id) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ask Legal AI
        AilexCard(fill = Blue050, border = BlueBorder, pressedFill = Blue100, pressedBorder = BlueBorder, onClick = onAskLegalAiClick) {
            Row(
                modifier = Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Filled.Forum, contentDescription = null, tint = Blue600, modifier = Modifier.size(22.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ask Legal AI", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold), color = Navy900)
                    Text(
                        text = "Type or speak a question. English, हिंदी or मराठी.",
                        style = TextStyle(fontSize = 12.5.sp, lineHeight = 17.sp),
                        color = Ink600,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Blue600)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent incidents
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = "Recent incidents", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold), color = Ink900)
            Text(
                text = "See all",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = Blue600,
                modifier = Modifier.clickable(onClick = onSeeAllIncidentsClick)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (incidents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Line300, ShapeCardSm)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nothing saved yet. Situations appear here only when you choose to save them.",
                    style = TextStyle(fontSize = 13.5.sp, lineHeight = 20.sp),
                    color = Ink500,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                incidents.sortedByDescending { it.savedAt }.take(2).forEach { incident ->
                    RecentIncidentRow(incident = incident, onClick = { onIncidentClick(incident.id) })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Preserve500, modifier = Modifier.size(15.dp))
            Text(
                text = "You decide what becomes an incident. Conversations are not saved on their own.",
                style = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
                color = Ink500
            )
        }
    }

    if (showCoverageSheet) {
        AilexBottomSheet(onDismissRequest = { showCoverageSheet = false }) {
            Text(
                text = "Coverage: Mumbai / MMR",
                style = TextStyle(fontSize = 21.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold),
                color = Ink900,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Guidance is built for Mumbai City, Mumbai Suburban, Thane and Navi Mumbai. Outside these areas I'll say so rather than apply Maharashtra rules to you.",
                style = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
                color = Ink600,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PrimaryButton(text = "Got it", onClick = { showCoverageSheet = false })
        }
    }
}

@Composable
private fun DomainGridTile(domain: LegalDomain, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AilexCard(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(icon = domainIcon(domain), tint = domain.tileBackground, ink = domain.accentColor, size = 40.dp, iconSize = 21.dp)
            Column {
                Text(text = domain.displayName, style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold), color = Ink900)
                Text(
                    text = domain.description,
                    style = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
                    color = Ink500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun RecentIncidentRow(incident: Incident, onClick: () -> Unit) {
    AilexCard(shape = ShapeCardSm, onClick = onClick) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(icon = domainIcon(incident.domain), tint = incident.domain.tileBackground, ink = incident.domain.accentColor, size = 34.dp, iconSize = 18.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = incident.title,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    color = Ink900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = incidentMeta(incident),
                    style = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
                    color = Ink500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Ink400, modifier = Modifier.size(18.dp))
        }
    }
}

private val DateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())

private fun incidentMeta(incident: Incident): String {
    val date = incident.dateLocation ?: DateFormatter.format(Date(incident.savedAt))
    return "$date · ${incident.domain.displayName} · ${incident.status.displayName}"
}

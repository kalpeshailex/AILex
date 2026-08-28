package com.example.ailex.features.incidents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ailex.core.common.UiState
import com.example.ailex.domain.incident.Incident
import com.example.ailex.ui.components.AilexCard
import com.example.ailex.ui.components.AilexFilterChip
import com.example.ailex.ui.components.IconTile
import com.example.ailex.ui.components.StatusPill
import com.example.ailex.ui.components.TagChip
import com.example.ailex.ui.components.domainIcon
import com.example.ailex.ui.theme.Blue600
import com.example.ailex.ui.theme.Ink400
import com.example.ailex.ui.theme.Ink500
import com.example.ailex.ui.theme.Ink600
import com.example.ailex.ui.theme.Ink700
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.Line200
import com.example.ailex.ui.theme.Line300
import com.example.ailex.ui.theme.Navy900
import com.example.ailex.ui.theme.ShapeCard
import com.example.ailex.ui.theme.ShapeCardSm
import com.example.ailex.ui.theme.ShapeChip
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Surface
import com.example.ailex.ui.theme.Typography

@Composable
fun IncidentListScreen(
    onIncidentClick: (String) -> Unit
) {
    val viewModel = LocalIncidentsViewModel.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val totalCount by viewModel.incidents.collectAsStateWithLifecycle()
    var searchOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(top = 14.dp, start = 20.dp, end = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = "My Incidents", style = Typography.titleLarge, color = Navy900)
                    Text(
                        text = "Saved situations, notes and drafts.",
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
                        .clickable {
                            searchOpen = !searchOpen
                            if (!searchOpen) viewModel.setSearchQuery("")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = Ink700, modifier = Modifier.size(21.dp))
                }
            }

            if (searchOpen) {
                Row(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth()
                        .background(Surface, ShapeField)
                        .border(1.dp, Line300, ShapeField)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Ink500, modifier = Modifier.size(19.dp))
                    Box(modifier = Modifier.weight(1f).padding(vertical = 14.dp)) {
                        if (query.isEmpty()) {
                            Text(text = "Search title, area or tag", style = TextStyle(fontSize = 14.5.sp), color = Ink500)
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = viewModel::setSearchQuery,
                            textStyle = TextStyle(fontSize = 14.5.sp, color = Ink900),
                            cursorBrush = SolidColor(Blue600),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (query.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Clear search",
                            tint = Ink400,
                            modifier = Modifier
                                .size(19.dp)
                                .clickable { viewModel.setSearchQuery("") }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 18.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                IncidentFilter.entries.forEach { entry ->
                    AilexFilterChip(
                        label = entry.displayName,
                        selected = filter == entry,
                        onClick = { viewModel.setFilter(entry) }
                    )
                }
            }

            val resultCountText = when (val current = state) {
                is UiState.Success -> if (filter == IncidentFilter.ALL && query.isBlank()) {
                    "${current.data.size} saved"
                } else {
                    "${current.data.size} of ${totalCount.size}"
                }
                else -> if (filter == IncidentFilter.ALL && query.isBlank()) "0 saved" else "0 of ${totalCount.size}"
            }
            Text(text = resultCountText, style = TextStyle(fontSize = 12.sp), color = Ink400, modifier = Modifier.padding(bottom = 9.dp))
        }

        val incidents = (state as? UiState.Success)?.data.orEmpty()
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            if (incidents.isEmpty()) {
                item { EmptyIncidents() }
            } else {
                items(incidents, key = { it.id }) { incident ->
                    IncidentRow(incident = incident, onClick = { onIncidentClick(incident.id) })
                }
            }
            item { StorageNote(modifier = Modifier.padding(top = 7.dp)) }
        }
    }
}

@Composable
private fun EmptyIncidents() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Line300, ShapeCard)
            .padding(vertical = 26.dp, horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Ink400, modifier = Modifier.size(26.dp))
        Text(
            text = "Nothing here",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
            color = Ink700,
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
        )
        Text(
            text = "No saved incident matches this filter or search.",
            style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp),
            color = Ink500
        )
    }
}

@Composable
private fun StorageNote(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Line300, ShapeCardSm)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Filled.FolderOpen, contentDescription = null, tint = Ink500, modifier = Modifier.size(18.dp))
        Text(
            text = "Incidents are stored on this device only. Uninstalling the app or clearing its data removes them.",
            style = TextStyle(fontSize = 12.5.sp, lineHeight = 19.sp),
            color = Ink600
        )
    }
}

@Composable
private fun IncidentRow(incident: Incident, onClick: () -> Unit) {
    AilexCard(shape = ShapeCard, onClick = onClick) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(icon = domainIcon(incident.domain), tint = incident.domain.tileBackground, ink = incident.domain.accentColor, size = 38.dp)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = incident.domain.displayName.uppercase(),
                        style = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.07.em),
                        color = incident.domain.accentColor
                    )
                    StatusPill(status = incident.status)
                }
                Text(
                    text = incident.title,
                    style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
                    color = Ink900,
                    modifier = Modifier.padding(top = 3.dp)
                )
                if (incident.dateLocation != null) {
                    Text(
                        text = incident.dateLocation,
                        style = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
                        color = Ink500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (incident.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        incident.tags.forEach { tag -> TagChip(label = tag) }
                    }
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Ink400, modifier = Modifier.size(19.dp))
        }
    }
}

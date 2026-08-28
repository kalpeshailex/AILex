package com.example.ailex.features.incidents

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.core.common.UiState
import com.example.ailex.domain.incident.Incident
import com.example.ailex.domain.incident.IncidentSeedData
import com.example.ailex.domain.incident.IncidentTimelineEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class IncidentFilter(val displayName: String) {
    ALL("All"), ACTIVE("Active"), RESOLVED("Resolved"), DRAFTS("Drafts")
}

/**
 * Activity-scoped (provided once via [LocalIncidentsViewModel]) so Home,
 * the Incidents tab, Live Situation, and Ask Legal AI all share one
 * in-memory list of saved incidents. Seeded with the four demo incidents
 * from design_handoff_ailex_v1 — no persistence yet (see `CLAUDE.md`),
 * this is the natural seam for a future repository-backed data layer.
 */
class IncidentsViewModel : ViewModel() {
    private val _incidents = MutableStateFlow(IncidentSeedData.all)
    val incidents: StateFlow<List<Incident>> = _incidents

    private val _filter = MutableStateFlow(IncidentFilter.ALL)
    val filter: StateFlow<IncidentFilter> = _filter

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val uiState: StateFlow<UiState<List<Incident>>> = combine(
        _incidents, _filter, _searchQuery
    ) { incidents, filter, query ->
        val filtered = incidents
            .filter { incident ->
                when (filter) {
                    IncidentFilter.ALL -> true
                    IncidentFilter.ACTIVE -> incident.status == IncidentStatus.ACTIVE
                    IncidentFilter.RESOLVED -> incident.status == IncidentStatus.RESOLVED
                    IncidentFilter.DRAFTS -> incident.status == IncidentStatus.DRAFT
                }
            }
            .filter { incident ->
                if (query.isBlank()) return@filter true
                incident.title.contains(query, ignoreCase = true) ||
                    incident.domain.displayName.contains(query, ignoreCase = true) ||
                    incident.dateLocation?.contains(query, ignoreCase = true) == true ||
                    incident.tags.any { it.contains(query, ignoreCase = true) }
            }
            .sortedByDescending { it.savedAt }
        if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Empty)

    fun setFilter(filter: IncidentFilter) {
        _filter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getIncident(id: String): Incident? = _incidents.value.find { it.id == id }

    fun addIncident(incident: Incident) {
        _incidents.value = _incidents.value + incident
    }

    fun updateNotes(id: String, notes: String) {
        updateIncident(id) { it.copy(notes = notes) }
    }

    fun addTimelineEvent(id: String, event: IncidentTimelineEvent) {
        updateIncident(id) { it.copy(timeline = it.timeline + event) }
    }

    fun updateComplaintSection(id: String, sectionIndex: Int, text: String) {
        updateIncident(id) { it.copy(complaintEdits = it.complaintEdits + (sectionIndex to text)) }
    }

    fun resetComplaintEdits(id: String) {
        updateIncident(id) { it.copy(complaintEdits = emptyMap()) }
    }

    fun deleteIncident(id: String) {
        _incidents.value = _incidents.value.filterNot { it.id == id }
    }

    fun clearAll() {
        _incidents.value = emptyList()
    }

    private fun updateIncident(id: String, transform: (Incident) -> Incident) {
        _incidents.value = _incidents.value.map { if (it.id == id) transform(it) else it }
    }
}

val LocalIncidentsViewModel = staticCompositionLocalOf<IncidentsViewModel> {
    error("No IncidentsViewModel provided")
}

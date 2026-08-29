package com.example.ailex.features.incidents

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.core.common.UiState
import com.example.ailex.core.network.IncidentsApi
import com.example.ailex.core.network.SessionTokenHolder
import com.example.ailex.domain.incident.Incident
import com.example.ailex.domain.incident.IncidentTimelineEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class IncidentFilter(val displayName: String) {
    ALL("All"), ACTIVE("Active"), RESOLVED("Resolved"), DRAFTS("Drafts")
}

/**
 * Activity-scoped (provided once via [LocalIncidentsViewModel]) so Home,
 * the Incidents tab, Live Situation, and Ask Legal AI all share one list of
 * incidents. Backed by the Cloudflare Worker's /incidents API (see
 * backend/README.md) — reactively (re)loads whenever [SessionTokenHolder]'s
 * token changes (sign-in fetches, sign-out clears), rather than the design.md
 * era's hardcoded seed list.
 *
 * Writes (add/update/delete) are optimistic: local state updates immediately
 * and the network call fires in the background. A failed write is not
 * retried or rolled back yet — see BUILD_LOG.md known limitations.
 */
class IncidentsViewModel : ViewModel() {
    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    val incidents: StateFlow<List<Incident>> = _incidents

    private val _loadState = MutableStateFlow<UiState<Unit>>(UiState.Loading)

    private val _filter = MutableStateFlow(IncidentFilter.ALL)
    val filter: StateFlow<IncidentFilter> = _filter

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            SessionTokenHolder.accessToken.collect { token ->
                if (token != null) {
                    refresh(token)
                } else {
                    _incidents.value = emptyList()
                    _loadState.value = UiState.Empty
                }
            }
        }
    }

    private suspend fun refresh(token: String) {
        _loadState.value = UiState.Loading
        IncidentsApi.list(token)
            .onSuccess {
                _incidents.value = it
                _loadState.value = UiState.Success(Unit)
            }
            .onFailure { e ->
                _loadState.value = UiState.Error(e.message ?: "Couldn't load your incidents.")
            }
    }

    /** Retries the initial load after a failure — see ErrorState's "Try again". */
    fun retry() {
        val token = SessionTokenHolder.accessToken.value ?: return
        viewModelScope.launch { refresh(token) }
    }

    val uiState: StateFlow<UiState<List<Incident>>> = combine(
        _incidents, _filter, _searchQuery, _loadState
    ) { incidents, filter, query, loadState ->
        if (loadState is UiState.Error) return@combine loadState
        if (loadState is UiState.Loading && incidents.isEmpty()) return@combine UiState.Loading
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun setFilter(filter: IncidentFilter) {
        _filter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getIncident(id: String): Incident? = _incidents.value.find { it.id == id }

    fun addIncident(incident: Incident) {
        val token = SessionTokenHolder.accessToken.value ?: return
        viewModelScope.launch {
            IncidentsApi.create(token, incident).onSuccess { created ->
                _incidents.value = _incidents.value + created
            }
        }
    }

    fun updateNotes(id: String, notes: String) {
        patch(id, JSONObject().put("notes", notes))
    }

    fun addTimelineEvent(id: String, event: IncidentTimelineEvent) {
        val updatedTimeline = getIncident(id)?.let { it.timeline + event } ?: return
        patch(id, JSONObject().put("timeline", IncidentsApi.timelineToJson(updatedTimeline)))
    }

    fun updateComplaintSection(id: String, sectionIndex: Int, text: String) {
        val updatedEdits = (getIncident(id)?.complaintEdits ?: emptyMap()) + (sectionIndex to text)
        patch(id, JSONObject().put("complaint_edits", IncidentsApi.complaintEditsToJson(updatedEdits)))
    }

    fun resetComplaintEdits(id: String) {
        patch(id, JSONObject().put("complaint_edits", JSONObject()))
    }

    fun deleteIncident(id: String) {
        val token = SessionTokenHolder.accessToken.value ?: return
        _incidents.value = _incidents.value.filterNot { it.id == id }
        viewModelScope.launch { IncidentsApi.delete(token, id) }
    }

    fun clearAll() {
        val token = SessionTokenHolder.accessToken.value
        val ids = _incidents.value.map { it.id }
        _incidents.value = emptyList()
        if (token != null) {
            viewModelScope.launch {
                ids.forEach { id -> IncidentsApi.delete(token, id) }
            }
        }
    }

    /** Optimistic PATCH: applies the server's response back over local state once it returns. */
    private fun patch(id: String, fields: JSONObject) {
        val token = SessionTokenHolder.accessToken.value ?: return
        viewModelScope.launch {
            IncidentsApi.patch(token, id, fields).onSuccess { updated ->
                _incidents.value = _incidents.value.map { if (it.id == id) updated else it }
            }
        }
    }
}

val LocalIncidentsViewModel = staticCompositionLocalOf<IncidentsViewModel> {
    error("No IncidentsViewModel provided")
}

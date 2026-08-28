package com.example.ailex.domain.incident

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.ailex.core.common.IncidentStatus
import com.example.ailex.core.common.LegalDomain

/** One timeline entry. [dotColor] carries meaning — see design_handoff_ailex_v1 §17: Blue600 for a normal event, Success500 for a resolved step, Caution500 for a problem, Ink400 for bookkeeping. */
data class IncidentTimelineEvent(
    val title: String,
    val whenText: String,
    val dotColor: Color,
    val body: String? = null
)

/**
 * A reference to a file already on the user's device — never a copy. [uri]
 * is null for the seeded demo incidents (which predate any real file
 * picker use); a real attachment stores the persisted document URI here
 * and [available] reflects whether it can still be resolved.
 */
data class EvidenceRef(
    val displayName: String,
    val meta: String,
    val available: Boolean = true,
    val uri: Uri? = null
)

data class Incident(
    val id: String,
    val domain: LegalDomain,
    val title: String,
    val status: IncidentStatus = IncidentStatus.ACTIVE,
    val tags: List<String> = emptyList(),
    /** "22 Aug 2026 · Sion Circle" — the list row's and Home's meta line. */
    val dateLocation: String? = null,
    /** "Saved 22 Aug 2026, 6:40 pm · Sion Circle, Mumbai" — the detail header; falls back to formatting [savedAt] when null. */
    val savedDetail: String? = null,
    val summary: String = "",
    val keyFacts: List<Pair<String, String>> = emptyList(),
    val timeline: List<IncidentTimelineEvent> = emptyList(),
    val evidence: List<EvidenceRef> = emptyList(),
    val notes: String = "",
    /** Section index → edited text; generated draft text is in `domain/complaint`. Reset clears this map. */
    val complaintEdits: Map<Int, String> = emptyMap(),
    val savedAt: Long = System.currentTimeMillis()
)

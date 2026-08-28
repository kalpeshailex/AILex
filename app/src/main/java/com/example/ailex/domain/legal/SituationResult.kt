package com.example.ailex.domain.legal

import com.example.ailex.core.common.LegalDomain
import java.time.LocalDate

enum class RiskLevel { STANDARD, HIGH }

data class ActionStep(val text: String, val note: String? = null)

/** Matches design_handoff_ailex_v1: `check` for rights, `close` for avoid/improper, `arrow_right` otherwise. */
enum class SectionItemIcon { CHECK, CLOSE, ARROW_RIGHT }

data class SectionItem(val icon: SectionItemIcon, val text: String)

data class LegalSource(
    val title: String,
    val excerpt: String,
    val authority: String,
    val lastVerified: LocalDate
)

/**
 * The seven-section id set. Order, icon and ink are fixed by id — see
 * design_handoff_ailex_v1 §12 — while [ResultSection.title]/[meta] vary per
 * domain (e.g. cyber's POWERS section reads "What the bank and police can
 * do", not "Authority powers").
 */
enum class SectionId { POSITION, RIGHTS, POWERS, IMPROPER, AVOID, PRESERVE, LEGAL }

data class ResultSection(
    val id: SectionId,
    val title: String,
    val meta: String,
    val items: List<SectionItem>,
    val caveat: String? = null,
    val sources: List<LegalSource> = emptyList()
)

data class SituationResult(
    val domain: LegalDomain,
    val title: String,
    val risk: RiskLevel,
    val safetyNote: String?,
    val situationSummary: String,
    val factChips: List<String>,
    val actionSteps: List<ActionStep>,
    val sections: List<ResultSection>,
    val escalationBlurb: String
)

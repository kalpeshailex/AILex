package com.example.ailex.features.live_situation

import androidx.lifecycle.ViewModel
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.domain.legal.DomainQuestion
import com.example.ailex.domain.legal.LiveSituationQuestionSets
import com.example.ailex.domain.legal.LiveSituationResults
import com.example.ailex.domain.legal.SectionId
import com.example.ailex.domain.legal.SituationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LiveSituationState(
    val domain: LegalDomain? = null,
    /** True once the user picked "Something else" on the category screen — no domain, no question set. */
    val isGeneral: Boolean = false,
    val generalDescription: String = "",
    val questionIndex: Int = 0,
    val answers: Map<Int, String> = emptyMap(),
    val completedSteps: Set<Int> = emptySet(),
    val expandedSections: Set<SectionId> = emptySet(),
    val speaking: Boolean = false
) {
    val questions: List<DomainQuestion> get() = domain?.let { LiveSituationQuestionSets.byDomain[it] }.orEmpty()
    val currentQuestion: DomainQuestion? get() = questions.getOrNull(questionIndex)
    val questionsAnswered: Boolean get() = domain != null && questionIndex >= questions.size
    val result: SituationResult? get() = domain?.let { LiveSituationResults.byDomain[it] }
}

/**
 * Graph-scoped to the Live Situation nav graph. Drives Safety → Urgent →
 * Category → Question(s)/FreeText → Result. See
 * design_handoff_ailex_v1 §State management.
 */
class LiveSituationViewModel : ViewModel() {
    private val _state = MutableStateFlow(LiveSituationState())
    val state: StateFlow<LiveSituationState> = _state.asStateFlow()

    fun selectDomain(domain: LegalDomain) {
        _state.value = LiveSituationState(domain = domain)
    }

    fun selectGeneral() {
        _state.value = LiveSituationState(isGeneral = true)
    }

    fun submitGeneralDescription(text: String) {
        _state.value = _state.value.copy(generalDescription = text)
    }

    /** Used by both the Question screen's option tap and FreeText's submit — both just answer the current question. */
    fun answerCurrentQuestion(answer: String) {
        val current = _state.value
        _state.value = current.copy(
            answers = current.answers + (current.questionIndex to answer),
            questionIndex = current.questionIndex + 1
        )
    }

    fun goToPreviousQuestion() {
        _state.value = _state.value.copy(questionIndex = (_state.value.questionIndex - 1).coerceAtLeast(0))
    }

    fun toggleStepDone(index: Int) {
        val current = _state.value.completedSteps
        _state.value = _state.value.copy(
            completedSteps = if (index in current) current - index else current + index
        )
    }

    fun toggleSection(id: SectionId) {
        val current = _state.value.expandedSections
        _state.value = _state.value.copy(
            expandedSections = if (id in current) current - id else current + id
        )
    }

    fun toggleSpeaking() {
        _state.value = _state.value.copy(speaking = !_state.value.speaking)
    }

    /**
     * The saved-incident location segment — the domain's location question's
     * answer, or null if the domain has none (Government, Cyber) or it
     * wasn't reached. Never substitutes another answer for a missing one.
     */
    fun locationAnswer(): String? {
        val current = _state.value
        val domain = current.domain ?: return null
        val index = LiveSituationQuestionSets.locationQuestionIndex[domain] ?: return null
        return current.answers[index]
    }

    /** (question text, answer) rows, in question order — the key is the question, never "Answer 1/2/3". */
    fun answeredFacts(): List<Pair<String, String>> {
        val current = _state.value
        return current.questions.mapIndexedNotNull { index, question ->
            current.answers[index]?.let { answer -> question.text to answer }
        }
    }

    fun reset() {
        _state.value = LiveSituationState()
    }
}

package com.example.ailex.features.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ailex.core.network.ConversationApi
import com.example.ailex.core.network.ConversationTurn
import com.example.ailex.core.network.SessionTokenHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isUser: Boolean,
    /** Non-null for a real assistant answer — see ConversationScreen's AnswerCard. */
    val answer: ConversationTurn? = null,
    /** True only for the transient "thinking" placeholder while a request is in flight. */
    val isPending: Boolean = false
)

data class ConversationSession(
    val id: String = UUID.randomUUID().toString(),
    val messages: List<Message> = emptyList()
) {
    /** The latest resolved scenario/domain once one exists, else the generic title. */
    val topic: String
        get() = messages.lastOrNull { it.answer != null }?.answer?.displayTitle ?: "Ask Legal AI"
}

/**
 * Graph-scoped to the Ask Legal AI nav graph so the conversation survives
 * navigating between the landing screen and the conversation screen.
 *
 * Calls the real backend AI pipeline (`POST /conversation/message` — see
 * `core/network/ConversationApi.kt` and `06_AI_ARCHITECTURE.md`). Each
 * request round-trips the previous turn's opaque context/domain/scenario so
 * the pipeline doesn't re-ask facts already established within a session.
 */
class AskLegalAiSessionViewModel : ViewModel() {
    private val _session = MutableStateFlow(ConversationSession())
    val session: StateFlow<ConversationSession> = _session.asStateFlow()

    private var lastContext: JSONObject? = null
    private var lastDomain: String? = null
    private var lastScenario: String? = null

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        val pendingId = UUID.randomUUID().toString()
        _session.value = _session.value.copy(
            messages = _session.value.messages +
                Message(text = trimmed, isUser = true) +
                Message(id = pendingId, isUser = false, isPending = true)
        )

        val token = SessionTokenHolder.accessToken.value
        if (token == null) {
            replacePending(pendingId, Message(id = pendingId, isUser = false, text = "Please sign in again to use Ask Legal AI."))
            return
        }

        viewModelScope.launch {
            ConversationApi.sendMessage(
                token = token,
                message = trimmed,
                previousContext = lastContext,
                previousDomain = lastDomain,
                previousScenario = lastScenario
            ).onSuccess { turn ->
                lastContext = turn.rawContext
                lastDomain = turn.domain.takeIf { it != "UNKNOWN" }
                lastScenario = turn.scenario.takeIf { it.isNotBlank() }
                replacePending(pendingId, Message(id = pendingId, isUser = false, answer = turn))
            }.onFailure { e ->
                replacePending(
                    pendingId,
                    Message(id = pendingId, isUser = false, text = e.message ?: "Something went wrong. Please try again.")
                )
            }
        }
    }

    private fun replacePending(id: String, replacement: Message) {
        _session.value = _session.value.copy(
            messages = _session.value.messages.map { if (it.id == id) replacement else it }
        )
    }

    fun reset() {
        _session.value = ConversationSession()
        lastContext = null
        lastDomain = null
        lastScenario = null
    }
}

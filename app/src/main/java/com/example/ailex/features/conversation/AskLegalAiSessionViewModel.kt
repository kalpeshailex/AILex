package com.example.ailex.features.conversation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

private const val PLAIN_PLACEHOLDER_REPLY =
    "Ask Legal AI isn't available yet — this is a preview of the conversation experience."

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    /** True only for the one scripted, fully-worked demo turn — see [UpiFraudDemoTurn]. */
    val isRichDemo: Boolean = false
)

data class ConversationSession(
    val id: String = UUID.randomUUID().toString(),
    val messages: List<Message> = emptyList()
) {
    /** "UPI fraud" once the scripted demo turn has appeared, else the generic title. */
    val topic: String get() = if (messages.any { it.isRichDemo }) "UPI fraud" else "Ask Legal AI"
}

/**
 * Graph-scoped to the Ask Legal AI nav graph so the conversation survives
 * navigating between the landing screen and the conversation screen.
 *
 * There is no real model behind this (see `CLAUDE.md`). The very first
 * assistant reply in a session is the one fully-worked example the design
 * ships (`UpiFraudDemoTurn`) — it demonstrates the intended UI shape, not
 * an answer to whatever was actually asked. Every reply after that is the
 * same honest, generic placeholder the app has always used. Never
 * generated, never tailored.
 */
class AskLegalAiSessionViewModel : ViewModel() {
    private val _session = MutableStateFlow(ConversationSession())
    val session: StateFlow<ConversationSession> = _session.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val current = _session.value
        val isFirstMessage = current.messages.isEmpty()
        val userMessage = Message(text = text, isUser = true)
        val reply = if (isFirstMessage) {
            Message(text = "", isUser = false, isRichDemo = true)
        } else {
            Message(text = PLAIN_PLACEHOLDER_REPLY, isUser = false)
        }
        _session.value = current.copy(messages = current.messages + userMessage + reply)
    }

    fun reset() {
        _session.value = ConversationSession()
    }
}

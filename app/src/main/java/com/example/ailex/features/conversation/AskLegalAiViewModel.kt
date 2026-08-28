package com.example.ailex.features.conversation

import androidx.lifecycle.ViewModel
import com.example.ailex.core.common.UiState
import com.example.ailex.domain.conversation.SuggestedPrompt
import com.example.ailex.domain.conversation.SuggestedPrompts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AskLegalAiContent(val suggestedPrompts: List<SuggestedPrompt> = SuggestedPrompts)

class AskLegalAiViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AskLegalAiContent>>(UiState.Success(AskLegalAiContent()))
    val uiState: StateFlow<UiState<AskLegalAiContent>> = _uiState.asStateFlow()
}

package com.example.ailex.features.home

import androidx.lifecycle.ViewModel
import com.example.ailex.core.common.LegalDomain
import com.example.ailex.core.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeContent(val domains: List<LegalDomain> = LegalDomain.entries)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HomeContent>>(UiState.Success(HomeContent()))
    val uiState: StateFlow<UiState<HomeContent>> = _uiState.asStateFlow()
}

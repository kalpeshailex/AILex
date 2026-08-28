package com.example.ailex.core.common

/**
 * Generic wrapper so every screen can represent loading, success, empty and
 * error states in the same way. Real data sources will populate this later.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}

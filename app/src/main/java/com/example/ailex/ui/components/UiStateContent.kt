package com.example.ailex.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ailex.core.common.UiState

/**
 * Renders a [UiState] consistently across screens. Success/empty/error slots
 * control their own layout, so they are free to fill the available space.
 */
@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    emptyContent: @Composable () -> Unit = { EmptyState(title = "Nothing here yet") },
    errorContent: @Composable (String) -> Unit = { ErrorState(message = it) },
    successContent: @Composable (T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Crossfade(targetState = state, label = "uiState") { current ->
            when (current) {
                is UiState.Loading -> loadingContent()
                is UiState.Empty -> emptyContent()
                is UiState.Error -> errorContent(current.message)
                is UiState.Success -> successContent(current.data)
            }
        }
    }
}

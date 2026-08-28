package com.example.ailex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ailex.ui.theme.Ink900
import com.example.ailex.ui.theme.ShapeField
import com.example.ailex.ui.theme.Spacing
import com.example.ailex.ui.theme.Surface as SurfaceColor
import com.example.ailex.ui.theme.SuccessOnDark
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Provided once at the app root (see `AilexApp`) so any screen can trigger
 * a toast that survives navigating away — the host lives above the
 * NavHost, not inside whichever screen requested it.
 */
val LocalToastHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided — wrap content in AilexApp's Scaffold")
}

/** Shows the app's success toast: "Incident saved", "Draft copied", etc. Auto-dismisses at 2.6s. */
suspend fun SnackbarHostState.showToast(message: String) = coroutineScope {
    val dismissAfterDelay = launch {
        delay(2_600)
        currentSnackbarData?.dismiss()
    }
    showSnackbar(message = message, duration = SnackbarDuration.Indefinite)
    dismissAfterDelay.cancel()
}

/** `Ink900` fill, check icon, white label — pass as a Scaffold's `snackbarHost` content. */
@Composable
fun Toast(data: SnackbarData) {
    Surface(
        modifier = Modifier.padding(horizontal = Spacing.space4, vertical = Spacing.space6),
        shape = ShapeField,
        color = Ink900
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.space4, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.space2)
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = SuccessOnDark,
                modifier = Modifier.size(19.dp)
            )
            Text(text = data.visuals.message, color = SurfaceColor)
        }
    }
}

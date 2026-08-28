package com.example.ailex.core.common

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val displayName: String, val nativeLabel: String) {
    ENGLISH("English", "Recommended for this release"),
    HINDI("Hindi", "हिंदी"),
    MARATHI("Marathi", "मराठी")
}

enum class ThemeMode { LIGHT, SYSTEM }

enum class TextSize { STANDARD, LARGE, EXTRA_LARGE }

data class AppSessionState(
    val displayName: String = "",
    val maskedMobile: String = "",
    val maskedEmail: String = "",
    /** The verified Supabase Auth session's access token, or null if signed out. */
    val accessToken: String? = null,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val textSize: TextSize = TextSize.STANDARD,
    val voiceAutoPlay: Boolean = true,
    val voiceSpeechRate: Float = 1.0f,
    val voiceReplayEnabled: Boolean = true,
    val remindersEnabled: Boolean = true
)

/**
 * Activity-scoped, in-memory only (no DataStore/Room yet — see design.md
 * build plan). Holds the user's local profile and app preferences so they
 * are set once and read from anywhere via [LocalAppViewModel].
 */
class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppSessionState())
    val state: StateFlow<AppSessionState> = _state.asStateFlow()

    fun setMobileNumber(rawNumber: String) {
        val masked = if (rawNumber.length >= 4) {
            "•••••• ${rawNumber.takeLast(4)}"
        } else {
            rawNumber
        }
        _state.value = _state.value.copy(maskedMobile = masked)
    }

    /** "te••••@example.com" — first 2 chars of the local part, domain untouched. */
    fun setEmail(email: String) {
        val at = email.indexOf('@')
        val masked = if (at > 2) {
            "${email.take(2)}${"•".repeat((at - 2).coerceAtLeast(2))}${email.substring(at)}"
        } else {
            email
        }
        _state.value = _state.value.copy(maskedEmail = masked)
    }

    /** The access token from a just-verified Supabase Auth session (see SupabaseAuthApi). */
    fun setSession(accessToken: String) {
        _state.value = _state.value.copy(accessToken = accessToken)
    }

    fun setUserProfile(name: String, language: AppLanguage) {
        _state.value = _state.value.copy(displayName = name, language = language)
    }

    fun setLanguage(language: AppLanguage) {
        _state.value = _state.value.copy(language = language)
    }

    fun setTheme(themeMode: ThemeMode) {
        _state.value = _state.value.copy(themeMode = themeMode)
    }

    fun setTextSize(textSize: TextSize) {
        _state.value = _state.value.copy(textSize = textSize)
    }

    fun setVoicePreferences(autoPlay: Boolean, speechRate: Float, replayEnabled: Boolean) {
        _state.value = _state.value.copy(
            voiceAutoPlay = autoPlay,
            voiceSpeechRate = speechRate,
            voiceReplayEnabled = replayEnabled
        )
    }

    fun setRemindersEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(remindersEnabled = enabled)
    }

    /** Wipes the local profile back to a fresh, signed-out state — used by Delete my data. */
    fun clearSession() {
        _state.value = AppSessionState()
    }
}

val LocalAppViewModel = staticCompositionLocalOf<AppViewModel> {
    error("No AppViewModel provided")
}

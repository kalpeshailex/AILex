package com.example.ailex.core.common

import android.app.Application
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ailex.core.network.SessionStore
import com.example.ailex.core.network.SessionTokenHolder
import com.example.ailex.core.network.SupabaseAuthApi
import com.example.ailex.core.network.SupabaseSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
 * Activity-scoped. Holds the user's local profile and app preferences so
 * they are set once and read from anywhere via [LocalAppViewModel].
 *
 * The verified session survives process death: [SessionStore] persists the
 * Supabase refresh token (never the short-lived access token) plus the
 * onboarding profile fields, and [restoreSession] exchanges it for a fresh
 * access token on every cold start — see [sessionRestoreComplete].
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application)

    private val _state = MutableStateFlow(AppSessionState())
    val state: StateFlow<AppSessionState> = _state.asStateFlow()

    /** False until the startup session-restore attempt (network refresh) finishes. */
    private val _sessionRestoreComplete = MutableStateFlow(false)
    val sessionRestoreComplete: StateFlow<Boolean> = _sessionRestoreComplete.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        val stored = sessionStore.load()
        if (stored == null) {
            _sessionRestoreComplete.value = true
            return
        }
        viewModelScope.launch {
            SupabaseAuthApi.refreshSession(stored.refreshToken)
                .onSuccess { session ->
                    sessionStore.updateRefreshToken(session.refreshToken)
                    _state.value = _state.value.copy(
                        displayName = stored.displayName,
                        maskedMobile = stored.maskedMobile,
                        maskedEmail = stored.maskedEmail,
                        language = stored.language,
                        accessToken = session.accessToken
                    )
                    SessionTokenHolder.set(session.accessToken)
                }
                .onFailure {
                    // Refresh token expired or revoked — fall back to signed-out.
                    sessionStore.clear()
                }
            _sessionRestoreComplete.value = true
        }
    }

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

    /** A just-verified Supabase Auth session (see SupabaseAuthApi) — persisted so it survives app restarts. */
    fun setSession(session: SupabaseSession) {
        _state.value = _state.value.copy(accessToken = session.accessToken)
        SessionTokenHolder.set(session.accessToken)
        sessionStore.save(
            refreshToken = session.refreshToken,
            displayName = _state.value.displayName,
            maskedMobile = _state.value.maskedMobile,
            maskedEmail = _state.value.maskedEmail,
            language = _state.value.language
        )
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

    /** Wipes the local profile back to a fresh, signed-out state — used by Log out and Delete my data. */
    fun clearSession() {
        _state.value = AppSessionState()
        SessionTokenHolder.set(null)
        sessionStore.clear()
    }
}

val LocalAppViewModel = staticCompositionLocalOf<AppViewModel> {
    error("No AppViewModel provided")
}

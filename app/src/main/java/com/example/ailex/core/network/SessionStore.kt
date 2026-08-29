package com.example.ailex.core.network

import android.content.Context
import com.example.ailex.core.common.AppLanguage

/**
 * Persists just enough of a verified session to survive process death and
 * app restarts. The access token itself is never stored here — it's
 * short-lived, so app start always exchanges the stored refresh token for a
 * fresh one (see AppViewModel.restoreSession) rather than trusting a cached
 * access token.
 */
class SessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(refreshToken: String, displayName: String, maskedMobile: String, maskedEmail: String, language: AppLanguage) {
        prefs.edit()
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_MASKED_MOBILE, maskedMobile)
            .putString(KEY_MASKED_EMAIL, maskedEmail)
            .putString(KEY_LANGUAGE, language.name)
            .apply()
    }

    /** Supabase may rotate the refresh token on every use — keep the stored one current. */
    fun updateRefreshToken(refreshToken: String) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun load(): StoredSession? {
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null) ?: return null
        val language = prefs.getString(KEY_LANGUAGE, null)
            ?.let { name -> AppLanguage.entries.find { it.name == name } }
            ?: AppLanguage.ENGLISH
        return StoredSession(
            refreshToken = refreshToken,
            displayName = prefs.getString(KEY_DISPLAY_NAME, "").orEmpty(),
            maskedMobile = prefs.getString(KEY_MASKED_MOBILE, "").orEmpty(),
            maskedEmail = prefs.getString(KEY_MASKED_EMAIL, "").orEmpty(),
            language = language
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "ailex_session"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_MASKED_MOBILE = "masked_mobile"
        const val KEY_MASKED_EMAIL = "masked_email"
        const val KEY_LANGUAGE = "language"
    }
}

data class StoredSession(
    val refreshToken: String,
    val displayName: String,
    val maskedMobile: String,
    val maskedEmail: String,
    val language: AppLanguage
)

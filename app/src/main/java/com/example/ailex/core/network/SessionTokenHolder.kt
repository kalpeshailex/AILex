package com.example.ailex.core.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The current Supabase access token, shared outside Compose so plain
 * ViewModels (IncidentsViewModel, ...) can react to sign-in/sign-out
 * without depending on AppViewModel directly. Written only by
 * AppViewModel.setSession() / clearSession().
 */
object SessionTokenHolder {
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    fun set(token: String?) {
        _accessToken.value = token
    }
}

package com.example.ailex.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Patterns
import com.example.ailex.core.common.AppLanguage
import com.example.ailex.core.network.SupabaseAuthApi
import com.example.ailex.core.network.SupabaseSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthMethod { PHONE, EMAIL }

/** Must match the OTP length configured in the Supabase project (Authentication → Providers → Email/Phone). */
const val OtpLength = 8

data class AuthFormState(
    val method: AuthMethod = AuthMethod.PHONE,
    val mobileNumber: String = "",
    val email: String = "",
    val otp: String = "",
    val name: String = "",
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isSending: Boolean = false,
    val isVerifying: Boolean = false,
    val errorMessage: String? = null,
    val session: SupabaseSession? = null
) {
    // design_handoff_ailex_v1: phone exactly 10 digits, OTP exactly 6, name
    // trimmed length > 1. Buttons are disabled on failure, never
    // error-flagged — onboarding never shows a red validation message.
    // (Network failures are the one exception — see errorMessage.)
    val isPhoneValid: Boolean get() = mobileNumber.length == 10
    val isEmailValid: Boolean get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isOtpValid: Boolean get() = otp.length == OtpLength
    val isNameValid: Boolean get() = name.trim().length > 1

    /** "+91 " + first 2 digits + "XXX XX" + last 3 digits, e.g. "+91 98XXX XX210". */
    val maskedMobileNumber: String
        get() = if (mobileNumber.length == 10) {
            "+91 ${mobileNumber.take(2)}XXX XX${mobileNumber.takeLast(3)}"
        } else {
            "+91 98XXX XX210"
        }

    /** "te••••@example.com" — first 2 chars of the local part, domain untouched. */
    val maskedEmail: String
        get() {
            val at = email.indexOf('@')
            return if (at > 2) {
                "${email.take(2)}${"•".repeat((at - 2).coerceAtLeast(2))}${email.substring(at)}"
            } else {
                email
            }
        }

    val maskedContact: String get() = if (method == AuthMethod.EMAIL) maskedEmail else maskedMobileNumber

    /** E.164 — Supabase's phone auth expects a leading "+" and country code. */
    val e164Phone: String get() = "+91$mobileNumber"
}

/**
 * Graph-scoped to the auth nav graph. Sends and verifies a real one-time
 * code via Supabase Auth (see SupabaseAuthApi) — either by phone SMS or by
 * email, the user's choice on the Welcome screen. Phone requires an SMS
 * provider to be configured in the Supabase project (see
 * backend/README.md); until then, only email actually delivers a code.
 */
class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state.asStateFlow()

    fun selectMethod(method: AuthMethod) {
        _state.value = _state.value.copy(method = method, errorMessage = null)
    }

    fun updateMobileNumber(value: String) {
        _state.value = _state.value.copy(mobileNumber = value.filter { it.isDigit() }.take(10), errorMessage = null)
    }

    fun updateEmail(value: String) {
        _state.value = _state.value.copy(email = value.trim(), errorMessage = null)
    }

    fun updateOtp(value: String) {
        _state.value = _state.value.copy(otp = value.filter { it.isDigit() }.take(OtpLength))
    }

    fun resetOtp() {
        _state.value = _state.value.copy(otp = "")
    }

    fun updateName(value: String) {
        _state.value = _state.value.copy(name = value.take(40))
    }

    fun updateLanguage(language: AppLanguage) {
        _state.value = _state.value.copy(language = language)
    }

    /** Sends (or resends) the one-time code for whichever method is selected. */
    fun sendCode(onSuccess: () -> Unit) {
        val current = _state.value
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true, errorMessage = null)
            val result = when (current.method) {
                AuthMethod.EMAIL -> SupabaseAuthApi.sendEmailOtp(current.email)
                AuthMethod.PHONE -> SupabaseAuthApi.sendPhoneOtp(current.e164Phone)
            }
            result
                .onSuccess {
                    _state.value = _state.value.copy(isSending = false)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isSending = false, errorMessage = e.message)
                }
        }
    }

    fun verifyCode(onSuccess: () -> Unit) {
        val current = _state.value
        viewModelScope.launch {
            _state.value = _state.value.copy(isVerifying = true, errorMessage = null)
            val result = when (current.method) {
                AuthMethod.EMAIL -> SupabaseAuthApi.verifyEmailOtp(current.email, current.otp)
                AuthMethod.PHONE -> SupabaseAuthApi.verifyPhoneOtp(current.e164Phone, current.otp)
            }
            result
                .onSuccess { session ->
                    _state.value = _state.value.copy(isVerifying = false, session = session)
                    onSuccess()
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(isVerifying = false, errorMessage = e.message)
                }
        }
    }
}

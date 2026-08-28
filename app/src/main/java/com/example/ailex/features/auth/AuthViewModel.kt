package com.example.ailex.features.auth

import androidx.lifecycle.ViewModel
import com.example.ailex.core.common.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthFormState(
    val mobileNumber: String = "",
    val otp: String = "",
    val name: String = "",
    val language: AppLanguage = AppLanguage.ENGLISH
) {
    // design_handoff_ailex_v1: phone exactly 10 digits, OTP exactly 6, name
    // trimmed length > 1. Buttons are disabled on failure, never
    // error-flagged — onboarding never shows a red validation message.
    val isPhoneValid: Boolean get() = mobileNumber.length == 10
    val isOtpValid: Boolean get() = otp.length == 6
    val isNameValid: Boolean get() = name.trim().length > 1

    /** "+91 " + first 2 digits + "XXX XX" + last 3 digits, e.g. "+91 98XXX XX210". */
    val maskedMobileNumber: String
        get() = if (mobileNumber.length == 10) {
            "+91 ${mobileNumber.take(2)}XXX XX${mobileNumber.takeLast(3)}"
        } else {
            "+91 98XXX XX210"
        }
}

/**
 * Graph-scoped to the auth nav graph. Entirely local: no SMS, no backend
 * call, no stored session — only basic input-shape validation.
 */
class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state.asStateFlow()

    fun updateMobileNumber(value: String) {
        _state.value = _state.value.copy(mobileNumber = value.filter { it.isDigit() }.take(10))
    }

    fun updateOtp(value: String) {
        _state.value = _state.value.copy(otp = value.filter { it.isDigit() }.take(6))
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
}

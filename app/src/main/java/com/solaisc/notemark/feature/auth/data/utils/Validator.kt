package com.solaisc.notemark.feature.auth.data.utils

import android.util.Patterns

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validatePassword(password: String): PasswordValidationState {
    val hasMinLength = password.length >= 8
    val hasDigitorSymbol = password.any { it.isDigit() || !it.isLetterOrDigit() }

    return PasswordValidationState(
        hasMinLength = hasMinLength,
        hasNumber = hasDigitorSymbol
    )
}

data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasNumber: Boolean = false
) {
    val isValidPassword: Boolean
        get() = hasMinLength && hasNumber
}
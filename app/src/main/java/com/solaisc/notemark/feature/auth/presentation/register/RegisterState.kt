package com.solaisc.notemark.feature.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState

data class RegisterState(
    val emailText: TextFieldState = TextFieldState(),
    val usernameText: TextFieldState = TextFieldState(),
    val passwordText: TextFieldState = TextFieldState(),
    val repeatPasswordText: TextFieldState = TextFieldState(),
    val isButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null
)

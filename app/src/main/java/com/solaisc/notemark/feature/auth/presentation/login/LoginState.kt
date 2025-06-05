package com.solaisc.notemark.feature.auth.presentation.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginState(
    val emailText: TextFieldState = TextFieldState(),
    val passwordText: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val isLoading: Boolean = false
)

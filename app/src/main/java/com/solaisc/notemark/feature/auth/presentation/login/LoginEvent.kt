package com.solaisc.notemark.feature.auth.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
    data class Error(val message: String): LoginEvent
}
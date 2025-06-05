package com.solaisc.notemark.feature.auth.presentation.register

sealed interface RegisterEvent {
    data object Success: RegisterEvent
    data class Error(val message: String): RegisterEvent
}
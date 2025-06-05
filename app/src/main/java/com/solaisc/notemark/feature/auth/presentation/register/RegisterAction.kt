package com.solaisc.notemark.feature.auth.presentation.register

sealed interface RegisterAction {
    data object OnRegisterClick: RegisterAction
    data object OnLoginClick: RegisterAction
    data object OnTooglePasswordVisibility: RegisterAction
    data object OnToogleRepeatPasswordVisibility: RegisterAction
}
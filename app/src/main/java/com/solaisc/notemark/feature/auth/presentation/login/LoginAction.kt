package com.solaisc.notemark.feature.auth.presentation.login

sealed interface LoginAction {
    data object OnTooglePasswordVisibility: LoginAction
    data object OnLoginClick: LoginAction
    data object OnRegisterClick: LoginAction
}
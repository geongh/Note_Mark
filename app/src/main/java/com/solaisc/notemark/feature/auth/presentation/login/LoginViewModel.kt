package com.solaisc.notemark.feature.auth.presentation.login

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.feature.auth.data.utils.isValidEmail
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<LoginEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            snapshotFlow { _state.value.emailText.text }
                .collectLatest { email ->
                    snapshotFlow { _state.value.passwordText.text }
                        .collectLatest { password ->
                            if (isValidEmail(email.toString().trim()) && password.isNotEmpty()) {
                                _state.update { it.copy(
                                    isButtonEnabled = true
                                ) }
                            } else {
                                _state.update { it.copy(
                                    isButtonEnabled = false
                                ) }
                            }
                        }
                }
        }
    }

    fun onAction(action: LoginAction) {
        when(action) {
            LoginAction.OnLoginClick -> {
                login()
            }
            LoginAction.OnTooglePasswordVisibility -> {
                _state.update { it.copy(
                    isPasswordVisible = !state.value.isPasswordVisible
                ) }
            }
            LoginAction.OnRegisterClick -> Unit
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }
            val result = repository.login(
                email = _state.value.emailText.text.toString().trim(),
                password = _state.value.passwordText.text.toString()
            )
            _state.update { it.copy(
                isLoading = false
            ) }

            when(result) {
                is Result.Error -> {
                    if(result.error == DataError.Network.UNAUTHORIZED) {
                        eventChannel.send(LoginEvent.Error(
                            "Invalid login credentials."
                        ))
                    } else {
                        eventChannel.send(LoginEvent.Error(
                            "There's a problem occured, check your internet connection or try again later."
                        ))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(LoginEvent.Success)
                }
            }
        }
    }
}
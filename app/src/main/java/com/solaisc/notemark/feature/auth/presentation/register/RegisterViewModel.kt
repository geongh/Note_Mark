package com.solaisc.notemark.feature.auth.presentation.register

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.util.isValidEmail
import com.solaisc.notemark.util.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            snapshotFlow { _state.value.emailText.text }
                .collectLatest { email ->
                    if (email.toString().trim().isNotEmpty() && !isValidEmail(email.toString().trim())) {
                        _state.update { it.copy(
                            emailError = "Invalid email provided",
                            isButtonEnabled = false
                        ) }
                    } else {
                        _state.update { it.copy(
                            emailError = null,
                            isButtonEnabled = ((email.toString().trim().isNotEmpty() && isValidEmail(email.toString().trim()))
                                    && (state.value.usernameText.text.isNotEmpty() && state.value.usernameError == null)
                                    && (state.value.passwordText.text.isNotEmpty() && state.value.passwordError == null)
                                    && (state.value.repeatPasswordText.text.isNotEmpty() && state.value.repeatPasswordError == null))
                        ) }
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { _state.value.usernameText.text }
                .collectLatest { username ->
                    if (username.toString().trim().isNotEmpty() && username.length < 3) {
                        _state.update { it.copy(
                            usernameError = "Username must be at least 3 characters",
                            isButtonEnabled = false
                        ) }
                    }  else {
                        _state.update { it.copy(
                            usernameError = null,
                            isButtonEnabled = ((state.value.emailText.text.isNotEmpty() && state.value.emailError == null)
                                    && (username.toString().trim().isNotEmpty() && username.length >= 3 && username.length < 21)
                                    && (state.value.passwordText.text.isNotEmpty() && state.value.passwordError == null)
                                    && (state.value.repeatPasswordText.text.isNotEmpty() && state.value.repeatPasswordError == null))
                        ) }
                    }
                    if (username.length > 20) {
                        _state.update { it.copy(
                            usernameError = "Username can't be longer than 20 characters",
                            isButtonEnabled = false
                        ) }
                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { _state.value.passwordText.text }
                .collectLatest { password ->
                    if (password.toString().isNotEmpty() && !validatePassword(password.toString()).isValidPassword) {
                        _state.update { it.copy(
                            passwordError = "Password must be at least 8 characters and include a number or symbol",
                            isButtonEnabled = false
                        ) }
                    }  else {
                        if (password.toString() == _state.value.repeatPasswordText.text) {
                            _state.update { it.copy(
                                repeatPasswordError = null
                            ) }
                        } else {
                            _state.update { it.copy(
                                repeatPasswordError = "Passwords do not match"
                            ) }
                        }

                        _state.update { it.copy(
                            passwordError = null,
                            isButtonEnabled = ((state.value.emailText.text.isNotEmpty() && state.value.emailError == null)
                                    && (state.value.usernameText.text.isNotEmpty() && state.value.usernameError == null)
                                    && (password.toString().isNotEmpty() && validatePassword(password.toString()).isValidPassword)
                                    && (state.value.repeatPasswordText.text.isNotEmpty() && state.value.repeatPasswordError == null))
                        ) }

                    }
                }
        }

        viewModelScope.launch {
            snapshotFlow { _state.value.repeatPasswordText.text }
                .collectLatest { repeat ->
                    if (repeat.toString().isNotEmpty() && repeat.toString() != state.value.passwordText.text) {
                        _state.update { it.copy(
                            repeatPasswordError = "Passwords do not match",
                            isButtonEnabled = false
                        ) }
                    }  else {
                        _state.update { it.copy(
                            repeatPasswordError = null,
                            isButtonEnabled = ((state.value.emailText.text.isNotEmpty() && state.value.emailError == null)
                                    && (state.value.usernameText.text.isNotEmpty() && state.value.usernameError == null)
                                    && (state.value.passwordText.text.isNotEmpty() && state.value.passwordError == null)
                                    && (repeat.toString().isNotEmpty() && repeat.toString() == state.value.passwordText.text))
                        ) }
                    }
                }
        }
    }

    fun onAction(action: RegisterAction) {
        when(action) {
            RegisterAction.OnLoginClick -> Unit
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTooglePasswordVisibility -> {
                _state.update { it.copy(
                    isPasswordVisible = !state.value.isPasswordVisible
                ) }
            }
            RegisterAction.OnToogleRepeatPasswordVisibility -> {
                _state.update { it.copy(
                    isRepeatPasswordVisible = !state.value.isRepeatPasswordVisible
                ) }
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }
            val result = repository.register(
                username = _state.value.usernameText.text.toString(),
                email = _state.value.emailText.text.toString().trim(),
                password = _state.value.passwordText.text.toString()
            )
            _state.update { it.copy(
                isLoading = false
            ) }

            when(result) {
                is Result.Error -> {
                    if(result.error == DataError.Network.CONFLICT) {
                        eventChannel.send(RegisterEvent.Error(
                            "Username or Email already exists."
                        ))
                    } else {
                        eventChannel.send(RegisterEvent.Error(
                            "There's a problem occured, check your internet connection or try again later."
                        ))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.Success)
                }
            }
        }
    }
}
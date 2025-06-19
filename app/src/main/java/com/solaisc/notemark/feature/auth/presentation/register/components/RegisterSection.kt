package com.solaisc.notemark.feature.auth.presentation.register.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.auth.presentation.register.RegisterAction
import com.solaisc.notemark.feature.auth.presentation.register.RegisterState
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NoteMarkPasswordTextField
import com.solaisc.notemark.util.components.NoteMarkTextField

@Composable
fun ColumnScope.RegisterSection(
    state: State<RegisterState>,
    onAction: (RegisterAction) -> Unit
) {
    NoteMarkTextField(
        state = state.value.usernameText,
        endIcon = null,
        hint = "John.doe",
        title = "Username",
        error = state.value.usernameError,
        info = "Use between 3 and 20 characters for your username."
    )
    Spacer(Modifier.height(16.dp))
    NoteMarkTextField(
        state = state.value.emailText,
        endIcon = null,
        hint = "john.doe@example.com",
        title = "Email",
        error = state.value.emailError
    )
    Spacer(Modifier.height(16.dp))
    NoteMarkPasswordTextField(
        state = state.value.passwordText,
        isPasswordVisible = state.value.isPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(RegisterAction.OnTooglePasswordVisibility)
        },
        hint = "Password",
        title = "Password",
        error = state.value.passwordError,
        info = "Use 8+ characters with a number or symbol for better security."
    )
    Spacer(Modifier.height(16.dp))
    NoteMarkPasswordTextField(
        state = state.value.repeatPasswordText,
        isPasswordVisible = state.value.isRepeatPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(RegisterAction.OnToogleRepeatPasswordVisibility)
        },
        hint = "Password",
        title = "Repeat Password",
        error = state.value.repeatPasswordError
    )
    Spacer(Modifier.height(24.dp))
    NoteMarkButton(
        text = "Create account",
        color = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.primary,
        onClick = {
            onAction(RegisterAction.OnRegisterClick)
        },
        enabled = state.value.isButtonEnabled,
        isLoading = state.value.isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(24.dp))
    Text(
        text = "Already have an account?",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onAction(RegisterAction.OnLoginClick)
            }
    )
}
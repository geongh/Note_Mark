package com.solaisc.notemark.feature.auth.presentation.login.components

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
import com.solaisc.notemark.feature.auth.presentation.login.LoginAction
import com.solaisc.notemark.feature.auth.presentation.login.LoginState
import com.solaisc.notemark.util.components.NoteMarkButton
import com.solaisc.notemark.util.components.NoteMarkPasswordTextField
import com.solaisc.notemark.util.components.NoteMarkTextField

@Composable
fun ColumnScope.LoginSection(
    state: State<LoginState>,
    onAction: (LoginAction) -> Unit
) {
    NoteMarkTextField(
        state = state.value.emailText,
        endIcon = null,
        hint = "john.doe@example.com",
        title = "Email"
    )
    Spacer(Modifier.height(16.dp))
    NoteMarkPasswordTextField(
        state = state.value.passwordText,
        isPasswordVisible = state.value.isPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(LoginAction.OnTooglePasswordVisibility)
        },
        hint = "Password",
        title = "Password"
    )
    Spacer(Modifier.height(24.dp))
    NoteMarkButton(
        text = "Log In",
        color = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.primary,
        onClick = {
            onAction(LoginAction.OnLoginClick)
        },
        enabled = state.value.isButtonEnabled,
        isLoading = state.value.isLoading
    )
    Spacer(Modifier.height(24.dp))
    Text(
        text = "Don't have an account?",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onAction(LoginAction.OnRegisterClick)
            }
    )
}
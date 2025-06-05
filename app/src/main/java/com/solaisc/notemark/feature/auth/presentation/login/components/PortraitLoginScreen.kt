package com.solaisc.notemark.feature.auth.presentation.login.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.auth.presentation.login.LoginAction
import com.solaisc.notemark.feature.auth.presentation.login.LoginState
import com.solaisc.notemark.util.components.NoteMarkHeader
import com.solaisc.notemark.util.components.PortraitBackground

@Composable
fun PortraitLoginScreen(
    state: State<LoginState>,
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier
) {
    PortraitBackground(
        modifier = modifier
    ) {
        NoteMarkHeader("Log In")
        Spacer(Modifier.height(36.dp))
        LoginSection(
            state = state,
            onAction = onAction
        )
    }
}
package com.solaisc.notemark.feature.auth.presentation.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.solaisc.notemark.feature.auth.presentation.login.LoginAction
import com.solaisc.notemark.feature.auth.presentation.login.LoginState
import com.solaisc.notemark.util.components.LandscapeBackground
import com.solaisc.notemark.util.components.NoteMarkHeader

@Composable
fun LandscapeLoginScreen(
    state: State<LoginState>,
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LandscapeBackground(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            NoteMarkHeader("Log In")
        }
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            LoginSection(
                state = state,
                onAction = onAction
            )
        }
    }
}
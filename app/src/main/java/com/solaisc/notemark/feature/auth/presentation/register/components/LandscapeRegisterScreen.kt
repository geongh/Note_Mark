package com.solaisc.notemark.feature.auth.presentation.register.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.auth.presentation.register.RegisterAction
import com.solaisc.notemark.feature.auth.presentation.register.RegisterState
import com.solaisc.notemark.util.components.LandscapeBackground
import com.solaisc.notemark.util.components.NoteMarkHeader

@Composable
fun LandscapeRegisterScreen(
    state: State<RegisterState>,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LandscapeBackground(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            NoteMarkHeader("Create account")
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            RegisterSection (
                state = state,
                onAction = onAction
            )
        }
    }
}
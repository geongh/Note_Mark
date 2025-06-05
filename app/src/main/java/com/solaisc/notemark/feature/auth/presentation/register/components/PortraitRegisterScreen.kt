package com.solaisc.notemark.feature.auth.presentation.register.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.auth.presentation.register.RegisterAction
import com.solaisc.notemark.feature.auth.presentation.register.RegisterState
import com.solaisc.notemark.util.components.NoteMarkHeader
import com.solaisc.notemark.util.components.PortraitBackground

@Composable
fun PortraitRegisterScreen(
    state: State<RegisterState>,
    onAction: (RegisterAction) -> Unit,
    modifier: Modifier = Modifier
) {
    PortraitBackground(
        modifier = modifier
    ) {
        NoteMarkHeader("Create account")
        Spacer(Modifier.height(36.dp))
        RegisterSection (
            state = state,
            onAction = onAction
        )
    }
}
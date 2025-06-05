package com.solaisc.notemark.feature.auth.presentation.landing.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.util.components.NoteMarkButton

@Composable
fun ColumnScope.UserSection(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = "Your Own Collection\nof Notes",
        textAlign = align,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Capture your thoughts and ideas.",
        textAlign = align,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
    Spacer(Modifier.height(32.dp))
    NoteMarkButton(
        text = "Get Started",
        color = MaterialTheme.colorScheme.onPrimary,
        backgroundColor = MaterialTheme.colorScheme.primary,
        onClick = {
            onRegisterClick()
        },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    NoteMarkButton(
        text = "Log In",
        color = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.onPrimary,
        onClick = {
            onLoginClick()
        },
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.primary
    )
}
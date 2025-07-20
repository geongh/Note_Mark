package com.solaisc.notemark.feature.note.presentation.input_note.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.note.presentation.input_note.NoteAction

@Composable
fun ClearIcon(
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.Clear,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .clickable {
                onClick()
            }
    )
}
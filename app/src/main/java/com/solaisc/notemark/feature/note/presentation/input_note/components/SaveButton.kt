package com.solaisc.notemark.feature.note.presentation.input_note.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solaisc.notemark.feature.note.presentation.input_note.NoteAction

@Composable
fun SaveButton(
    isSaving: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSaving) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier
                .size(20.dp)
        )
    }
    /*else {
        Text(
            text = "SAVE NOTE",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable {
                    onClick()
                }
                .padding(4.dp)
        )
    }*/
}
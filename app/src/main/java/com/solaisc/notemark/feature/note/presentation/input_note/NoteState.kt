package com.solaisc.notemark.feature.note.presentation.input_note

import androidx.compose.foundation.text.input.TextFieldState
import com.solaisc.notemark.feature.note.domain.model.Note

data class NoteState(
    val id: String = "",
    val titleText: TextFieldState = TextFieldState(),
    val contentText: TextFieldState = TextFieldState(),
    val createdAt: String = "",
    val isLoading: Boolean = false,
    val isUpdate: Boolean = false,
    val isDialogShow: Boolean = false,
    val note: Note? = null
)

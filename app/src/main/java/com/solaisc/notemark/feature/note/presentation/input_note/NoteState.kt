package com.solaisc.notemark.feature.note.presentation.input_note

import androidx.compose.foundation.text.input.TextFieldState
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.presentation.utils.NoteMode

data class NoteState(
    val id: String = "",
    val titleText: TextFieldState = TextFieldState(),
    val contentText: TextFieldState = TextFieldState(),
    val createdAt: String = "",
    val lastEdited: String = "",
    val isLoading: Boolean = false,
    val isUpdate: Boolean = false,
    val isDialogShow: Boolean = false,
    val note: Note? = null,
    val noteMode: NoteMode = NoteMode.Input_Mode,
    val isReaderModeSelected: Boolean = false
)

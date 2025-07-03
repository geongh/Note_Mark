package com.solaisc.notemark.feature.note.presentation.input_note

import com.solaisc.notemark.util.NoteMode

sealed interface NoteAction {
    data object OnNoteSaveClick: NoteAction
    data object OnBackClick: NoteAction
    data object OnDismissDialog: NoteAction
    data class OnDiscardChangeClick(val id: String): NoteAction
    data class OnModeChangeClick(val noteMode: NoteMode): NoteAction
}
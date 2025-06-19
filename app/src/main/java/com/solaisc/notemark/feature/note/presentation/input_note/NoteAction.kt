package com.solaisc.notemark.feature.note.presentation.input_note

sealed interface NoteAction {
    data object OnNoteSaveClick: NoteAction
    data object OnBackClick: NoteAction
    data object OnDismissDialog: NoteAction
    data class OnDiscardChangeClick(val id: String): NoteAction
}
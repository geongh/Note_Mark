package com.solaisc.notemark.feature.note.presentation.list_note

sealed interface NotesAction {
    data object OnNewNotesClick: NotesAction
    data class OnEditNoteClick(val id: String): NotesAction
    data class OnItemLongTap(val id: String): NotesAction
    data class OnDeleteClick(val id: String): NotesAction
}
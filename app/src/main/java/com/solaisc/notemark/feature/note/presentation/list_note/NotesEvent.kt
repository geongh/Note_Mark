package com.solaisc.notemark.feature.note.presentation.list_note

import com.solaisc.notemark.feature.note.domain.model.Note

sealed interface NotesEvent {
    data class Navigate(val id: String): NotesEvent
}
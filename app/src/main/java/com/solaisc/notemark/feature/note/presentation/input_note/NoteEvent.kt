package com.solaisc.notemark.feature.note.presentation.input_note

sealed interface NoteEvent {
    data object Navigate: NoteEvent
}
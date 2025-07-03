package com.solaisc.notemark.feature.note.presentation.input_note

import com.solaisc.notemark.util.NoteMode

sealed interface NoteEvent {
    data object Navigate: NoteEvent
    data class ChangeMode(val noteMode: NoteMode): NoteEvent
}
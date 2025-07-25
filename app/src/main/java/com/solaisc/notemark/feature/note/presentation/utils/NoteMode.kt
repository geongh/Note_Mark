package com.solaisc.notemark.feature.note.presentation.utils

sealed class NoteMode {
    data object View_Mode: NoteMode()
    data object Reader_Mode: NoteMode()
    data object Input_Mode: NoteMode()
}
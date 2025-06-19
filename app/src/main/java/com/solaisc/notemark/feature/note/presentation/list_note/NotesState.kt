package com.solaisc.notemark.feature.note.presentation.list_note

import com.solaisc.notemark.feature.note.domain.model.Note

data class NotesState(
    val initial: String = "",
    val list: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val isDialogShow: Boolean = false,
    val idToDelete: String? = null
)

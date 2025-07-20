package com.solaisc.notemark.feature.note.data.network

import kotlinx.serialization.Serializable

@Serializable
data class NotesDto(
    val notes: List<NoteDto>
)

@Serializable
data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val lastEditedAt: String
)

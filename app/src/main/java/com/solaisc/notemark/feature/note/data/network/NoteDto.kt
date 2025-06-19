package com.solaisc.notemark.feature.note.data.network

data class NoteDto(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val lastEditedAt: String
)

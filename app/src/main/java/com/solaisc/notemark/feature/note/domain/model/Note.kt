package com.solaisc.notemark.feature.note.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val lastEditedAt: String
)

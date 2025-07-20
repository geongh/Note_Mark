package com.solaisc.notemark.feature.note.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotePendingSyncEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val lastEditedAt: String,
    val isUpdate: Boolean,
    val username: String
)

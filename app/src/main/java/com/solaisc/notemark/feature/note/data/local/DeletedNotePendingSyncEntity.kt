package com.solaisc.notemark.feature.note.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeletedNotePendingSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val noteId: String,
    val username: String
)

package com.solaisc.notemark.feature.note.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SyncDateEntity(
    val syncDate: String,
    @PrimaryKey(autoGenerate = true) val id: Int
)

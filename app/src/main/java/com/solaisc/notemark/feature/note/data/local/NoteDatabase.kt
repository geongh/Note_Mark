package com.solaisc.notemark.feature.note.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val noteDao: NoteDao
}
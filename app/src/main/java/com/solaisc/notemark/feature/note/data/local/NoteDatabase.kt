package com.solaisc.notemark.feature.note.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class, NotePendingSyncEntity::class, DeletedNotePendingSyncEntity::class, SyncDateEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NoteDatabase: RoomDatabase() {
    abstract val noteDao: NoteDao
}
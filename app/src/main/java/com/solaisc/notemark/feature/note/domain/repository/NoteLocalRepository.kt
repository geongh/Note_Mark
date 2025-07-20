package com.solaisc.notemark.feature.note.domain.repository

import androidx.room.Query
import androidx.room.Upsert
import com.solaisc.notemark.feature.note.data.local.DeletedNotePendingSyncEntity
import com.solaisc.notemark.feature.note.data.local.NotePendingSyncEntity
import com.solaisc.notemark.feature.note.data.local.SyncDateEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.flow.Flow

interface NoteLocalRepository {
    suspend fun getSyncDate(): SyncDateEntity?

    suspend fun upsertSyncDate(syncDate: SyncDateEntity)

    fun getNotes(): Flow<List<Note>>

    suspend fun getNote(id: String): Note?

    suspend fun upsertNote(note: Note): Result<Note, DataError.Local>

    suspend fun deleteNote(id: String)

    suspend fun deleteNotes()

    suspend fun getNotePendingSyncs(username: String): List<NotePendingSyncEntity>

    suspend fun getNotePendingSync(id: String): NotePendingSyncEntity?

    suspend fun upsertNotePendingSync(note: NotePendingSyncEntity)

    suspend fun deleteNotePendingSync(id: String)

    suspend fun getDeletedNotePendingSyncs(username: String): List<DeletedNotePendingSyncEntity>

    suspend fun upsertDeletedNoteSync(deletedNote: DeletedNotePendingSyncEntity)

    suspend fun deleteDeletedNoteSync(id: String)
}
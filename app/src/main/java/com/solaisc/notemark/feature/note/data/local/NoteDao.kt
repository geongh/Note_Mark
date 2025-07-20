package com.solaisc.notemark.feature.note.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM syncdateentity")
    suspend fun getSyncDate(): SyncDateEntity?

    @Upsert
    suspend fun upsertSyncDate(syncDate: SyncDateEntity)

    @Query("SELECT * FROM noteentity ORDER BY createdAt DESC")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM noteentity WHERE id = :id")
    suspend fun getNote(id: String): NoteEntity?

    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Query("DELETE FROM noteentity WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("DELETE FROM noteentity")
    suspend fun deleteNotes()

    @Query("SELECT * FROM notependingsyncentity WHERE username = :username")
    suspend fun getNotePendingSyncs(username: String): List<NotePendingSyncEntity>

    @Query("SELECT * FROM notependingsyncentity WHERE id = :id")
    suspend fun getNotePendingSync(id: String): NotePendingSyncEntity?

    @Upsert
    suspend fun upsertNotePendingSync(note: NotePendingSyncEntity)

    @Query("DELETE FROM notependingsyncentity WHERE id = :id")
    suspend fun deleteNotePendingSync(id: String)

    @Query("SELECT * FROM deletednotependingsyncentity WHERE username = :username")
    suspend fun getDeletedNotePendingSyncs(username: String): List<DeletedNotePendingSyncEntity>

    @Upsert
    suspend fun upsertDeletedNoteSync(deletedNote: DeletedNotePendingSyncEntity)

    @Query("DELETE FROM deletednotependingsyncentity WHERE noteId = :id")
    suspend fun deleteDeletedNoteSync(id: String)
}
package com.solaisc.notemark.feature.note.data.local.repository

import android.database.sqlite.SQLiteFullException
import com.solaisc.notemark.feature.note.data.local.DeletedNotePendingSyncEntity
import com.solaisc.notemark.feature.note.data.local.NoteDao
import com.solaisc.notemark.feature.note.data.local.NoteEntity
import com.solaisc.notemark.feature.note.data.local.NotePendingSyncEntity
import com.solaisc.notemark.feature.note.data.local.SyncDateEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRoomRepositoryImpl(
    private val noteDao: NoteDao
) : NoteLocalRepository {
    override suspend fun getSyncDate(): SyncDateEntity? {
        return noteDao.getSyncDate()
    }

    override suspend fun upsertSyncDate(syncDate: SyncDateEntity) {
        noteDao.upsertSyncDate(syncDate)
    }

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes().map { note ->
            note.map {
                Note(
                    id = it.id,
                    title = it.title,
                    content = it.content,
                    createdAt = it.createdAt,
                    lastEditedAt = it.lastEditedAt
                )
            }
        }
    }

    override suspend fun getNote(id: String): Note? {
        val noteEntity =  noteDao.getNote(id)
        return Note(
            id = noteEntity?.id ?: "",
            title = noteEntity?.title ?: "",
            content = noteEntity?.content ?: "",
            createdAt = noteEntity?.createdAt ?: "",
            lastEditedAt = noteEntity?.lastEditedAt ?: ""
        )
    }

    override suspend fun upsertNote(note: Note): Result<Note, DataError.Local> {
        return try {
            noteDao.upsertNote(
                NoteEntity(
                    title = note.title,
                    content = note.content,
                    createdAt = note.createdAt,
                    lastEditedAt = note.lastEditedAt,
                    id = note.id
                )
            )
            Result.Success(note)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteNote(id: String) {
        noteDao.deleteNote(id)
    }

    override suspend fun deleteNotes() {
        noteDao.deleteNotes()
    }

    override suspend fun getNotePendingSyncs(username: String): List<NotePendingSyncEntity> {
        return noteDao.getNotePendingSyncs(username)
    }

    override suspend fun getNotePendingSync(id: String): NotePendingSyncEntity? {
        return noteDao.getNotePendingSync(id)
    }

    override suspend fun upsertNotePendingSync(note: NotePendingSyncEntity) {
        noteDao.upsertNotePendingSync(note)
    }

    override suspend fun deleteNotePendingSync(id: String) {
        noteDao.deleteNotePendingSync(id)
    }

    override suspend fun getDeletedNotePendingSyncs(username: String): List<DeletedNotePendingSyncEntity> {
        return noteDao.getDeletedNotePendingSyncs(username)
    }

    override suspend fun upsertDeletedNoteSync(deletedNote: DeletedNotePendingSyncEntity) {
        noteDao.upsertDeletedNoteSync(deletedNote)
    }

    override suspend fun deleteDeletedNoteSync(id: String) {
        noteDao.deleteDeletedNoteSync(id)
    }
}
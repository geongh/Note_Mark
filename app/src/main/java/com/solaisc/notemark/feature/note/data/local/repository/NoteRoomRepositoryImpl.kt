package com.solaisc.notemark.feature.note.data.local.repository

import android.database.sqlite.SQLiteFullException
import com.solaisc.notemark.feature.note.data.local.NoteDao
import com.solaisc.notemark.feature.note.data.local.NoteEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRoomRepositoryImpl(
    private val noteDao: NoteDao
) : NoteLocalRepository {
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
}
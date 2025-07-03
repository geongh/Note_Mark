package com.solaisc.notemark.feature.note.domain.repository

import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.flow.Flow

interface NoteLocalRepository {
    fun getNotes(): Flow<List<Note>>

    suspend fun getNote(id: String): Note?

    suspend fun upsertNote(note: Note): Result<Note, DataError.Local>

    suspend fun deleteNote(id: String)

    suspend fun deleteNotes()
}
package com.solaisc.notemark.feature.note.domain.repository

import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.EmptyResult
import com.solaisc.notemark.util.result.Result

interface NoteNetworkRepository {
    suspend fun getNotes(): Result<List<Note>, DataError.Network>

    suspend fun postNote(note: Note): Result<Note, DataError.Network>

    suspend fun putNote(note: Note): Result<Note, DataError.Network>

    suspend fun deleteNote(id: String): EmptyResult<DataError.Network>
}

package com.solaisc.notemark.feature.note.data.network.repository

import com.solaisc.notemark.feature.note.data.network.NoteDto
import com.solaisc.notemark.feature.note.data.network.NoteRequest
import com.solaisc.notemark.feature.note.data.network.NoteResponse
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.util.networking.delete
import com.solaisc.notemark.util.networking.get
import com.solaisc.notemark.util.networking.post
import com.solaisc.notemark.util.networking.put
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.EmptyResult
import com.solaisc.notemark.util.result.Result
import com.solaisc.notemark.util.result.map
import io.ktor.client.HttpClient

class NoteKtorRepositoryImpl(
    private val httpClient: HttpClient
) : NoteNetworkRepository {
    override suspend fun getNotes(): Result<List<Note>, DataError.Network> {
        return httpClient.get<List<NoteDto>>(
            route = "api/notes"
        ).map { dto ->
            dto.map {
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

    override suspend fun postNote(note: Note): Result<Note, DataError.Network> {
        val result = httpClient.post<NoteRequest, NoteResponse>(
            route = "/api/notes",
            body = NoteRequest(
                id = note.id,
                title = note.title,
                content = note.content,
                createdAt = note.createdAt,
                lastEditedAt = note.lastEditedAt
            )
        )

        return result.map {
            Note(
                id = it.id,
                title = it.title,
                content = it.content,
                createdAt = it.createdAt,
                lastEditedAt = it.lastEditedAt
            )
        }
    }

    override suspend fun putNote(note: Note): Result<Note, DataError.Network> {
        val result = httpClient.put<NoteRequest, NoteResponse>(
            route = "/api/notes",
            body = NoteRequest(
                id = note.id,
                title = note.title,
                content = note.content,
                createdAt = note.createdAt,
                lastEditedAt = note.lastEditedAt
            )
        )

        return result.map {
            Note(
                id = it.id,
                title = it.title,
                content = it.content,
                createdAt = it.createdAt,
                lastEditedAt = it.lastEditedAt
            )
        }
    }

    override suspend fun deleteNote(id: String): EmptyResult<DataError.Network> {
        return httpClient.delete(
            route = "/api/notes",
            parameter = id
        )
    }

}
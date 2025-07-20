package com.solaisc.notemark.util.networking.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.solaisc.notemark.feature.note.data.network.NotesDto
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result

class FetchNotesWorker(
    context: Context,
    params: WorkerParameters,
    private val networkRepository: NoteNetworkRepository,
    private val localRepository: NoteLocalRepository
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }
        return when(val result = networkRepository.getNotes()) {
            is com.solaisc.notemark.util.result.Result.Error<*> -> {
                when(result.error) {
                    DataError.Local.DISK_FULL -> Result.failure()
                    DataError.Network.REQUEST_TIMEOUT -> Result.retry()
                    DataError.Network.UNAUTHORIZED -> Result.retry()
                    DataError.Network.CONFLICT -> Result.retry()
                    DataError.Network.TOO_MANY_REQUEST -> Result.retry()
                    DataError.Network.NO_INTERNET -> Result.retry()
                    DataError.Network.PAYLOAD_TOO_LARGE -> Result.failure()
                    DataError.Network.SERVER_ERROR -> Result.retry()
                    DataError.Network.SERIALIZATION -> Result.failure()
                    else -> Result.failure()
                }
            }
            is com.solaisc.notemark.util.result.Result.Success<NotesDto> -> {
                val notes = result.data
                notes.notes.forEach { note ->
                    localRepository.upsertNote(
                        Note(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            createdAt = note.createdAt,
                            lastEditedAt = note.lastEditedAt
                        )
                    )
                }
                Result.success()
            }
        }
    }
}
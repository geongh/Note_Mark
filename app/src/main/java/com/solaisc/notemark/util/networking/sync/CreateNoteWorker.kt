package com.solaisc.notemark.util.networking.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.Result

class CreateNoteWorker(
    context: Context,
    private val params: WorkerParameters,
    private val localRepository: NoteLocalRepository,
    private val networkRepository: NoteNetworkRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        val pendingId = params.inputData.getString("id") ?: return Result.failure()
        val notePendingEntity = localRepository.getNotePendingSync(pendingId)
            ?: return Result.failure()

        return when(val result = networkRepository.postNote(
            Note(
                id = notePendingEntity.id,
                title = notePendingEntity.title,
                content = notePendingEntity.content,
                createdAt = notePendingEntity.createdAt,
                lastEditedAt = notePendingEntity.lastEditedAt
            )
        )) {
            is com.solaisc.notemark.util.result.Result.Error<*> -> {
                when(result.error) {
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
            is com.solaisc.notemark.util.result.Result.Success<*> -> {
                localRepository.deleteNotePendingSync(pendingId)
                Result.success()
            }
        }
    }
}
package com.solaisc.notemark.util.networking.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.solaisc.notemark.feature.note.data.local.DeletedNotePendingSyncEntity
import com.solaisc.notemark.feature.note.data.local.NoteDao
import com.solaisc.notemark.feature.note.data.local.NotePendingSyncEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.util.authentication.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncNoteWorkerScheduler(
    private val context: Context,
    private val dao: NoteDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : SyncNoteScheduler {

    val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: SyncNoteScheduler.SyncType) {
        when(type) {
            is SyncNoteScheduler.SyncType.CreateNote -> scheduleCreateNoteWorker(
                note = type.note
            )
            is SyncNoteScheduler.SyncType.DeleteNote -> scheduleDeleteNoteWorker(
                noteId = type.noteId
            )
            is SyncNoteScheduler.SyncType.FetchNotes -> scheduleFetchNotesWorker(
                interval = type.interval
            )
        }
    }

    override suspend fun cancelAllSync() {
        WorkManager.getInstance(context)
            .cancelAllWork()
            .await()
    }

    private suspend fun scheduleCreateNoteWorker(note: NotePendingSyncEntity) {
        //val username = sessionStorage.get()?.username ?: return
        dao.upsertNotePendingSync(note)

        val workRequest = OneTimeWorkRequestBuilder<CreateNoteWorker>()
            .addTag("create_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString("id", note.id)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleDeleteNoteWorker(noteId: String) {
        val username = sessionStorage.get()?.username ?: return
        val pendingDeletedNote = DeletedNotePendingSyncEntity(
            noteId = noteId,
            username = username
        )
        dao.upsertDeletedNoteSync(pendingDeletedNote)

        val workRequest = OneTimeWorkRequestBuilder<DeleteNoteWorker>()
            .addTag("delete_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString("id", noteId)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleFetchNotesWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }

        if (isSyncScheduled) {
            return
        }

        val workRequest = PeriodicWorkRequestBuilder<FetchNotesWorker>(
            repeatInterval = interval.toJavaDuration()
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = 15,
                timeUnit = TimeUnit.MINUTES
            )
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }
}
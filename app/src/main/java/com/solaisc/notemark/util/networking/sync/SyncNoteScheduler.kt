package com.solaisc.notemark.util.networking.sync

import com.solaisc.notemark.feature.note.data.local.NotePendingSyncEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import kotlin.time.Duration

interface SyncNoteScheduler {
    suspend fun scheduleSync(type: SyncType)

    suspend fun cancelAllSync()

    sealed interface SyncType {
        data class FetchNotes(val interval: Duration): SyncType

        data class DeleteNote(val noteId: String): SyncType

        class CreateNote(val note: NotePendingSyncEntity): SyncType
    }
}
package com.solaisc.notemark.feature.note.presentation.list_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.feature.note.data.local.DeletedNotePendingSyncEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.networking.sync.SyncNoteScheduler
import com.solaisc.notemark.util.result.Result
import com.solaisc.notemark.util.result.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

class NotesViewModel(
    private val localRepository: NoteLocalRepository,
    private val networkRepository: NoteNetworkRepository,
    private val sessionStorage: SessionStorage,
    private val syncNoteScheduler: SyncNoteScheduler,
    private val applicationScope: CoroutineScope,
): ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<NotesEvent>()
    val events = eventChannel.receiveAsFlow()

    var username: String = ""

    init {
        viewModelScope.launch {
            val username = sessionStorage.get()?.username

            val initialName = if (username != null) {
                val words = username.split(" ")
                if (words.size == 1) {
                    "${words[0].get(0)}${words[0].get(1)}"
                } else if (words.size == 2) {
                    "${words[0].get(0)}${words[1].get(0)}"
                } else {
                    "${words[0].get(0)}${words[words.lastIndex].get(0)}"
                }
            } else ""

            _state.update { it.copy(
                initial = initialName.uppercase()
            ) }
        }

        viewModelScope.launch {
            syncNoteScheduler.scheduleSync(
                type = SyncNoteScheduler.SyncType.FetchNotes(15.minutes)
            )
        }

        localRepository.getNotes().onEach { list ->
            _state.update { it.copy(
                list = list
            ) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                username = sessionStorage.get()?.username ?: return@withContext

                val createdNotes = async {
                    localRepository.getNotePendingSyncs(username)
                }
                val deletedRuns = async {
                    localRepository.getDeletedNotePendingSyncs(username)
                }

                val createdJobs = createdNotes
                    .await()
                    .map {
                        launch {
                            if (!it.isUpdate) {
                                when(networkRepository.postNote(Note(
                                    id = it.id,
                                    title = it.title,
                                    content = it.content,
                                    createdAt = it.createdAt,
                                    lastEditedAt = it.lastEditedAt
                                ))) {
                                    is Result.Error<*> -> Unit
                                    is Result.Success<*> -> {
                                        applicationScope.launch {
                                            localRepository.deleteNotePendingSync(it.id)
                                        }.join()
                                    }
                                }
                            } else {
                                when(networkRepository.putNote(Note(
                                    id = it.id,
                                    title = it.title,
                                    content = it.content,
                                    createdAt = it.createdAt,
                                    lastEditedAt = it.lastEditedAt
                                ))) {
                                    is Result.Error<*> -> Unit
                                    is Result.Success<*> -> {
                                        applicationScope.launch {
                                            localRepository.deleteNotePendingSync(it.id)
                                        }.join()
                                    }
                                }
                            }
                        }
                    }

                val deletedJobs = deletedRuns
                    .await()
                    .map {
                        launch {
                            when(networkRepository.deleteNote(it.noteId)) {
                                is Result.Error<*> -> Unit
                                is Result.Success<*> -> {
                                    applicationScope.launch {
                                        localRepository.deleteDeletedNoteSync(it.noteId)
                                    }.join()
                                }
                            }
                        }
                    }

                createdJobs.forEach { it.join() }
                deletedJobs.forEach { it.join() }
            }

            val localNotes = localRepository.getNotePendingSyncs(username)

            localNotes.forEach { note ->
                if (!note.isUpdate) {
                    networkRepository.postNote(
                        Note(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            createdAt = note.createdAt,
                            lastEditedAt = note.lastEditedAt
                        )
                    )
                } else {
                    networkRepository.putNote(
                        Note(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            createdAt = note.createdAt,
                            lastEditedAt = note.lastEditedAt
                        )
                    )
                }
                localRepository.deleteNotePendingSync(note.id)
            }

            val deletedLocalNotes = localRepository.getDeletedNotePendingSyncs(username)

            deletedLocalNotes.forEach { note ->
                networkRepository.deleteNote(note.noteId)
                localRepository.deleteDeletedNoteSync(note.noteId)
            }

            networkRepository.getNotes().map { notes ->
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
            }
        }
    }

    fun onAction(action: NotesAction) {
        when(action) {
            is NotesAction.OnEditNoteClick -> {
                viewModelScope.launch {
                    eventChannel.send(NotesEvent.Navigate(action.id, false))
                }
            }
            NotesAction.OnNewNotesClick -> {
                val uniqueId = UUID.randomUUID().toString()

                val note = Note(
                    id = uniqueId,
                    title = "Note Title",
                    content = "",
                    createdAt = nowStringTimeStamp(),
                    lastEditedAt = nowStringTimeStamp()
                )

                viewModelScope.launch {
                    localRepository.upsertNote(note)
                    eventChannel.send(NotesEvent.Navigate(uniqueId, true))

                    /*val result = localRepository.upsertNote(note)

                    if (result is Result.Success){
                        eventChannel.send(NotesEvent.Navigate(result.data.id, true))

                        viewModelScope.launch {
                            val networkResult = networkRepository.postNote(note)
                        }
                    }*/
                }
            }

            is NotesAction.OnDeleteClick -> {
                viewModelScope.launch {

                    localRepository.deleteNote(action.id)
                    localRepository.upsertDeletedNoteSync(
                        DeletedNotePendingSyncEntity(
                            noteId = action.id,
                            username = username
                        )
                    )

                    _state.update { it.copy(
                        isDialogShow = !state.value.isDialogShow,
                        idToDelete = null
                    ) }

                    //viewModelScope.launch {
                    //    val networkResult = networkRepository.deleteNote(action.id)
                    //}
                }
            }
            is NotesAction.OnItemLongTap -> {
                _state.update { it.copy(
                    isDialogShow = !state.value.isDialogShow,
                    idToDelete = if (state.value.isDialogShow == true) {
                        null
                    } else action.id
                ) }
            }
        }
    }

    private fun nowStringTimeStamp(): String {
        return Instant.parse(Instant.now().toString()).toString()
    }
}
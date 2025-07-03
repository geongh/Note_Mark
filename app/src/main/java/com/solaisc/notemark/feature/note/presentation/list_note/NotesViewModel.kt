package com.solaisc.notemark.feature.note.presentation.list_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class NotesViewModel(
    private val localRepository: NoteLocalRepository,
    private val networkRepository: NoteNetworkRepository,
    private val sessionStorage: SessionStorage
): ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<NotesEvent>()
    val events = eventChannel.receiveAsFlow()

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

        localRepository.getNotes().onEach { list ->
            _state.update { it.copy(
                list = list
            ) }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: NotesAction) {
        when(action) {
            is NotesAction.OnEditNoteClick -> {
                viewModelScope.launch {
                    eventChannel.send(NotesEvent.Navigate(action.id, false))
                }
            }
            NotesAction.OnNewNotesClick -> {
                val note = Note(
                    id = UUID.randomUUID().toString(),
                    title = "Note Title",
                    content = "",
                    createdAt = nowStringTimeStamp(),
                    lastEditedAt = nowStringTimeStamp()
                )

                viewModelScope.launch {
                    val result = localRepository.upsertNote(note)

                    if (result is Result.Success){
                        eventChannel.send(NotesEvent.Navigate(result.data.id, true))

                        viewModelScope.launch {
                            val networkResult = networkRepository.postNote(note)
                        }
                    }
                }
            }

            is NotesAction.OnDeleteClick -> {
                viewModelScope.launch {

                    localRepository.deleteNote(action.id)

                    _state.update { it.copy(
                        isDialogShow = !state.value.isDialogShow,
                        idToDelete = null
                    ) }

                    viewModelScope.launch {
                        val networkResult = networkRepository.deleteNote(action.id)
                    }
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
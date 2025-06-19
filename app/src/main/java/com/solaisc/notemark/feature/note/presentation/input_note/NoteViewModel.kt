package com.solaisc.notemark.feature.note.presentation.input_note

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class NoteViewModel(
    private val localRepository: NoteLocalRepository,
    private val networkRepository: NoteNetworkRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(NoteState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<NoteEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        val noteId = savedStateHandle.get<String>("id") ?: ""

        viewModelScope.launch {
            val note = localRepository.getNote(noteId)

            note?.let {
                _state.update { it.copy(
                    id = note.id,
                    titleText = TextFieldState(note.title),
                    contentText = TextFieldState(note.content),
                    createdAt = note.createdAt,
                    isUpdate = if (note.content.isEmpty()) { false } else true,
                    note = note
                ) }
            }

            //networkRepository.postNote(note!!)
        }

    }

    fun onAction(action: NoteAction) {
        when(action) {
            NoteAction.OnBackClick -> {
                if (_state.value.titleText.text.toString() != _state.value.note!!.title || _state.value.contentText.text.toString() != _state.value.note!!.content) {
                    _state.update { it.copy(
                        isDialogShow = !state.value.isDialogShow
                    ) }
                } else {
                    if (state.value.isUpdate == true) {
                        viewModelScope.launch {
                            eventChannel.send(NoteEvent.Navigate)
                        }
                    } else {
                        deleteNote(state.value.id)
                    }
                }
            }
            NoteAction.OnNoteSaveClick -> {
                saveNote(
                    title = _state.value.titleText.text.toString(),
                    content = _state.value.contentText.text.toString(),
                )
            }

            is NoteAction.OnDiscardChangeClick -> {
                _state.update { it.copy(
                    isDialogShow = !state.value.isDialogShow
                ) }

                deleteNote(action.id)
            }

            NoteAction.OnDismissDialog -> {
                _state.update { it.copy(
                    isDialogShow = !state.value.isDialogShow
                ) }
            }
        }
    }

    private fun deleteNote(id: String) {
        if (state.value.isUpdate == false) {
            viewModelScope.launch {
                localRepository.deleteNote(id)
                networkRepository.deleteNote(id)

                eventChannel.send(NoteEvent.Navigate)
            }
        } else {
            viewModelScope.launch {
                eventChannel.send(NoteEvent.Navigate)
            }
        }
    }

    private fun saveNote(
        title: String,
        content: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }

            val note = Note(
                id = state.value.id,
                title = title,
                content = content,
                createdAt = state.value.createdAt,
                lastEditedAt = nowStringTimeStamp()
            )
            val localResult = localRepository.upsertNote(note)

            if (localResult !is Result.Success) {
                return@launch
            }

            val networkResult = networkRepository.putNote(note)

            _state.update { it.copy(
                isLoading = false
            ) }

            eventChannel.send(NoteEvent.Navigate)
        }
    }

    private fun nowStringTimeStamp(): String {
        return Instant.parse(Instant.now().toString()).toString()
    }
}
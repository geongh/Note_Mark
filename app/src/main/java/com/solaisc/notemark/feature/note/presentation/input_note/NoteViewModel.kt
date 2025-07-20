package com.solaisc.notemark.feature.note.presentation.input_note

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.feature.note.data.local.NotePendingSyncEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.feature.note.presentation.utils.NoteMode
import com.solaisc.notemark.util.result.Result
import com.solaisc.notemark.feature.note.presentation.utils.toDateTimeString
import com.solaisc.notemark.util.authentication.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NoteViewModel(
    private val localRepository: NoteLocalRepository,
    private val networkRepository: NoteNetworkRepository,
    private val sessionStorage: SessionStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(NoteState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<NoteEvent>()
    val events = eventChannel.receiveAsFlow()

    var username: String = ""

    init {
        val noteId = savedStateHandle.get<String>("id") ?: ""
        val inputMode = savedStateHandle.get<Boolean>("input_mode") ?: false

        //Log.d("check note mode", "The input mode is ${inputMode}")

        viewModelScope.launch {
            username = sessionStorage.get()?.username ?: ""

            val note = localRepository.getNote(noteId)

            note?.let {
                _state.update { it.copy(
                    id = note.id,
                    titleText = TextFieldState(note.title),
                    contentText = TextFieldState(note.content),
                    createdAt = note.createdAt,
                    lastEdited = lastEditedString(note.lastEditedAt),
                    isUpdate = if (note.content.isEmpty()) { false } else true,
                    note = note
                ) }
            }
        }

        if (inputMode == true) {
            NoteAction.OnModeChangeClick(noteMode = NoteMode.Input_Mode)
        } else {
            _state.update { it.copy(
                noteMode = NoteMode.View_Mode
            ) }
        }
    }

    fun onAction(action: NoteAction) {
        when(action) {
            NoteAction.OnBackClick -> {
                /*if (_state.value.titleText.text.toString() != _state.value.note!!.title || _state.value.contentText.text.toString() != _state.value.note!!.content) {
                    _state.update { it.copy(
                        isDialogShow = !state.value.isDialogShow
                    ) }
                } else {
                    if (state.value.isUpdate == true) {
                        viewModelScope.launch {
                            eventChannel.send(NoteEvent.ChangeMode(NoteMode.View_Mode))
                        }
                    } else {
                        deleteNote(state.value.id)
                    }
                }*/
                if (state.value.isUpdate == true) {
                    viewModelScope.launch {
                        eventChannel.send(NoteEvent.ChangeMode(NoteMode.View_Mode))
                    }
                } else {
                    if (_state.value.contentText.text.toString().isEmpty()) {
                        deleteNote(state.value.id)
                    } else {
                        viewModelScope.launch {
                            eventChannel.send(NoteEvent.ChangeMode(NoteMode.View_Mode))
                        }
                    }
                }
            }
            NoteAction.OnNoteSaveClick -> {
                autoSave(
                    title = _state.value.titleText,
                    content = _state.value.contentText,
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

            is NoteAction.OnModeChangeClick -> {
                _state.update { it.copy(
                    noteMode = action.noteMode,
                    isReaderModeSelected = if (action.noteMode == NoteMode.Reader_Mode) true else false
                ) }
            }
        }
    }

    private fun deleteNote(id: String) {
        if (state.value.isUpdate == false) {
            viewModelScope.launch {
                localRepository.deleteNote(id)
                //networkRepository.deleteNote(id)

                eventChannel.send(NoteEvent.Navigate)
            }
        } else {
            viewModelScope.launch {
                _state.update { it.copy(
                    titleText = TextFieldState(state.value.note?.title ?: ""),
                    contentText = TextFieldState(state.value.note?.content ?: "")
                ) }

                eventChannel.send(NoteEvent.ChangeMode(NoteMode.View_Mode))
            }
        }
    }

    private fun saveNote(
        title: String,
        content: String
    ) {
        if (title.trim().isNotEmpty()) { //&& content.trim().isNotEmpty()) {
            viewModelScope.launch {
                _state.update { it.copy(
                    isLoading = true
                ) }

                val lastEdited = nowStringTimeStamp()

                val note = Note(
                    id = state.value.id,
                    title = title,
                    content = content,
                    createdAt = state.value.createdAt,
                    lastEditedAt = lastEdited
                )

                val pendingNote = NotePendingSyncEntity(
                    id = state.value.id,
                    title = title,
                    content = content,
                    createdAt = state.value.createdAt,
                    lastEditedAt = lastEdited,
                    isUpdate = state.value.isUpdate,
                    username = username
                )

                delay(500)
                localRepository.upsertNote(note)
                localRepository.upsertNotePendingSync(pendingNote)

                _state.update { it.copy(
                    isLoading = false,
                    note = note,
                    lastEdited = lastEditedString(lastEdited)
                ) }

                /*val localResult = localRepository.upsertNote(note)

                if (localResult !is Result.Success) {
                    return@launch
                }

                val networkResult = networkRepository.putNote(note)

                _state.update { it.copy(
                    isLoading = false,
                    note = note,
                    lastEdited = lastEditedString(lastEdited)
                ) }*/

                //onAction(NoteAction.OnModeChangeClick(NoteMode.View_Mode))
                //eventChannel.send(NoteEvent.ChangeMode(NoteMode.View_Mode))
            }
        }
    }

    private fun lastEditedString(isoDateTimeString: String): String {
        val instant = Instant.parse(isoDateTimeString)
        val systemZoneId = ZoneId.systemDefault()
        val dateTimeInSystemZone = LocalDateTime.ofInstant(instant, systemZoneId)
        val nowInSystemZone = LocalDateTime.now(systemZoneId)
        val duration = Duration.between(dateTimeInSystemZone, nowInSystemZone)
        return if (duration.toMinutes() < 5 && !duration.isNegative) {
            "Just now"
        } else {
            isoDateTimeString.toDateTimeString()
        }
    }

    private fun nowStringTimeStamp(): String {
        return Instant.parse(Instant.now().toString()).toString()
    }

    private fun TextFieldState.textAsFlow() = snapshotFlow { text }

    private fun autoSave(
        title: TextFieldState,
        content: TextFieldState
    ) {
        title.textAsFlow()
            .distinctUntilChanged()
            .drop(1)
            .debounce(1000).onEach {
                saveNote(
                    title = title.text.toString(),
                    content = content.text.toString(),
                )
            }
            .launchIn(viewModelScope)

        content.textAsFlow()
            .distinctUntilChanged()
            .drop(1)
            .debounce(1000).onEach {
                saveNote(
                    title = title.text.toString(),
                    content = content.text.toString(),
                )
            }
            .launchIn(viewModelScope)
    }
}
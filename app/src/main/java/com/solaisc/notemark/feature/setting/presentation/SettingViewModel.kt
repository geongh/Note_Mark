package com.solaisc.notemark.feature.setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.feature.note.data.local.SyncDateEntity
import com.solaisc.notemark.feature.note.domain.model.Note
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.feature.note.domain.repository.NoteNetworkRepository
import com.solaisc.notemark.feature.note.presentation.utils.toDateTimeString
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class SettingViewModel(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteLocalRepository,
    private val networkRepository: NoteNetworkRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val eventChannel = Channel<SettingEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(SettingState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val syncDate = noteRepository.getSyncDate()

            if (syncDate != null) {
                _state.update { it.copy(
                    syncDateText = "Last sync: ${lastSyncDateString(syncDate.syncDate)}"
                ) }
            } else {
                _state.update { it.copy(
                    syncDateText = "Never synced"
                ) }
            }
        }
    }

    fun onAction(action: SettingAction) {
        when(action) {
            SettingAction.OnLogoutClick -> {
                viewModelScope.launch {
                    val username = sessionStorage.get()?.username ?: ""

                    val localNotes = noteRepository.getNotePendingSyncs(username)
                    val deletedLocalNotes = noteRepository.getDeletedNotePendingSyncs(username)

                    if (localNotes.size > 0 || deletedLocalNotes.size > 0) {
                        _state.update { it.copy(
                            isDialogShown = true
                        ) }
                    } else {
                        val refreshToken = sessionStorage.get()?.refreshToken ?: ""
                        val result = authRepository.logout(refreshToken)

                        when(result) {
                            is Result.Error -> {
                                eventChannel.send(SettingEvent.Error(
                                    "There's a problem occured, check your internet connection or try again later."
                                ))
                            }
                            is Result.Success -> {
                                viewModelScope.launch {
                                    noteRepository.deleteNotes()
                                }

                                sessionStorage.set(info = null)
                                eventChannel.send(SettingEvent.Navigate)
                            }
                        }
                    }
                }
            }

            SettingAction.OnErrorShown -> {
                viewModelScope.launch {
                    eventChannel.send(SettingEvent.Error(
                        "You need an internet connection \nto log out."
                    ))
                }
            }

            SettingAction.OnSyncClick -> {
                viewModelScope.launch {
                    _state.update { it.copy(
                        isSync = true
                    ) }

                    val username = sessionStorage.get()?.username ?: ""

                    val localNotes = noteRepository.getNotePendingSyncs(username)

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

                        noteRepository.deleteNotePendingSync(note.id)
                    }

                    val deletedLocalNotes = noteRepository.getDeletedNotePendingSyncs(username)

                    deletedLocalNotes.forEach { note ->
                        networkRepository.deleteNote(note.noteId)
                        noteRepository.deleteDeletedNoteSync(note.noteId)
                    }

                    noteRepository.upsertSyncDate(
                        SyncDateEntity(
                            syncDate = nowStringTimeStamp(),
                            id = 1
                        )
                    )

                    val syncDate = noteRepository.getSyncDate()

                    _state.update { it.copy(
                        isSync = false,
                        syncDateText = "Last sync: ${lastSyncDateString(syncDate!!.syncDate)}"
                    ) }
                }
            }

            SettingAction.OnConfirmNotSyncClick -> {
                _state.update { it.copy(
                    isDialogShown = false
                ) }

                viewModelScope.launch {
                    val refreshToken = sessionStorage.get()?.refreshToken ?: ""
                    val result = authRepository.logout(refreshToken)

                    when(result) {
                        is Result.Error -> {
                            eventChannel.send(SettingEvent.Error(
                                "There's a problem occured, check your internet connection or try again later."
                            ))
                        }
                        is Result.Success -> {
                            viewModelScope.launch {
                                noteRepository.deleteNotes()
                            }

                            sessionStorage.set(info = null)
                            eventChannel.send(SettingEvent.Navigate)
                        }
                    }
                }
            }
            SettingAction.OnConfirmSyncClick -> {
                _state.update { it.copy(
                    isDialogShown = false
                ) }

                viewModelScope.launch {
                    val refreshToken = sessionStorage.get()?.refreshToken ?: ""
                    val result = authRepository.logout(refreshToken)

                    when(result) {
                        is Result.Error -> {
                            eventChannel.send(SettingEvent.Error(
                                "There's a problem occured, check your internet connection or try again later."
                            ))
                        }
                        is Result.Success -> {
                            viewModelScope.launch {
                                val username = sessionStorage.get()?.username ?: ""

                                val localNotes = noteRepository.getNotePendingSyncs(username)

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

                                    noteRepository.deleteNotePendingSync(note.id)
                                }

                                val deletedLocalNotes = noteRepository.getDeletedNotePendingSyncs(username)

                                deletedLocalNotes.forEach { note ->
                                    networkRepository.deleteNote(note.noteId)
                                    noteRepository.deleteDeletedNoteSync(note.noteId)
                                }

                                noteRepository.deleteNotes()
                            }

                            sessionStorage.set(info = null)
                            eventChannel.send(SettingEvent.Navigate)
                        }
                    }
                }
            }

            SettingAction.OnDismissDialog -> {
                _state.update { it.copy(
                    isDialogShown = false
                ) }
            }
        }
    }

    private fun nowStringTimeStamp(): String {
        return Instant.parse(Instant.now().toString()).toString()
    }

    private fun lastSyncDateString(isoDateTimeString: String): String {
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
}
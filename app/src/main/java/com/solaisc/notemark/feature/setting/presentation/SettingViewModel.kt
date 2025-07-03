package com.solaisc.notemark.feature.setting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.feature.auth.presentation.login.LoginEvent
import com.solaisc.notemark.feature.note.domain.repository.NoteLocalRepository
import com.solaisc.notemark.util.authentication.AuthInfo
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.result.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteLocalRepository,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val eventChannel = Channel<SettingEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: SettingAction) {
        when(action) {
            SettingAction.OnLogoutClick -> {
                viewModelScope.launch {
                    noteRepository.deleteNotes()

                    val refreshToken = sessionStorage.get()?.refreshToken ?: ""

                    val result = authRepository.logout(refreshToken)

                    when(result) {
                        is Result.Error -> {
                            eventChannel.send(SettingEvent.Error(
                                "There's a problem occured, check your internet connection or try again later."
                            ))
                        }
                        is Result.Success -> {
                            sessionStorage.set(info = null)
                            eventChannel.send(SettingEvent.Navigate)
                        }
                    }
                }
            }
        }
    }
}
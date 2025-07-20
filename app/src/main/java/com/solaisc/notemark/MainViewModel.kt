package com.solaisc.notemark

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.networking.connection_observer.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: SessionStorage,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    init {
        viewModelScope.launch {
            //Log.d("Check access token", sessionStorage.get().toString())
            state = state.copy(isCheckingAuth = true)

            val sessionExists = sessionStorage.get() != null

            state = state.copy(
                isLoggedIn = sessionExists
            )
            state = state.copy(isCheckingAuth = false)
        }
    }

    val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )
}


data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = false
)
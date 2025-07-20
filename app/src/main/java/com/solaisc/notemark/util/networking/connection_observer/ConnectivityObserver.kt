package com.solaisc.notemark.util.networking.connection_observer

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isConnected: Flow<Boolean>
}
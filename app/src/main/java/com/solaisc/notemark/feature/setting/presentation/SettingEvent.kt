package com.solaisc.notemark.feature.setting.presentation

sealed interface SettingEvent {
    data object Navigate: SettingEvent
    data class Error(val message: String): SettingEvent
}
package com.solaisc.notemark.feature.setting.presentation

data class SettingState(
    val isSync: Boolean = false,
    val isDialogShown: Boolean = false,
    val syncDateText: String = "Never synced"
)

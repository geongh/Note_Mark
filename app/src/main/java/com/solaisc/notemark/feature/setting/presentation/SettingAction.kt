package com.solaisc.notemark.feature.setting.presentation

sealed interface SettingAction {
    data object OnLogoutClick: SettingAction
}
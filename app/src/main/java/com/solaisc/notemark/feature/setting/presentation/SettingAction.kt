package com.solaisc.notemark.feature.setting.presentation

sealed interface SettingAction {
    data object OnLogoutClick: SettingAction
    data object OnErrorShown: SettingAction
    data object OnSyncClick: SettingAction
    data object OnConfirmSyncClick: SettingAction
    data object OnConfirmNotSyncClick: SettingAction
    data object OnDismissDialog: SettingAction
}
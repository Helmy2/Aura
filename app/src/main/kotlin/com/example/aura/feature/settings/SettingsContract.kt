package com.example.aura.feature.settings


import com.example.aura.domain.model.ThemeMode

sealed interface SettingsIntent {
    data object LoadSettings : SettingsIntent
    data class UpdateThemeMode(val mode: ThemeMode) : SettingsIntent
    data class OnThemeModeLoaded(val mode: ThemeMode) : SettingsIntent
    data class OnError(val message: String) : SettingsIntent
}

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isLoading: Boolean = true,
    val error: String? = null
)
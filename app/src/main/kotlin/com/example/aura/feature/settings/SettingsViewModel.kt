package com.example.aura.feature.settings

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.model.ThemeMode
import com.example.aura.domain.repository.SettingsRepository
import com.example.aura.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : MviViewModel<SettingsState, SettingsIntent, Nothing>(
    initialState = SettingsState()
) {

    init {
        sendIntent(SettingsIntent.LoadSettings)
        observeThemeMode()
    }

    override fun reduce(
        currentState: SettingsState, intent: SettingsIntent
    ): Pair<SettingsState, Nothing?> {
        return when (intent) {
            is SettingsIntent.LoadSettings -> {
                loadThemeMode()
                currentState.copy(isLoading = true, error = null)
            }

            is SettingsIntent.OnThemeModeLoaded -> {
                currentState.copy(
                    themeMode = intent.mode, isLoading = false, error = null
                )
            }

            is SettingsIntent.UpdateThemeMode -> {
                updateThemeMode(intent.mode)
                currentState
            }

            is SettingsIntent.OnError -> {
                currentState.copy(
                    isLoading = false, error = intent.message
                )
            }
        }.only()
    }

    private fun loadThemeMode() {
        viewModelScope.launch {
            try {
                val mode = settingsRepository.getThemeMode()
                sendIntent(SettingsIntent.OnThemeModeLoaded(mode))
            } catch (e: Exception) {
                sendIntent(SettingsIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                settingsRepository.updateThemeMode(mode)
            } catch (e: Exception) {
                sendIntent(SettingsIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun observeThemeMode() {
        settingsRepository.observeThemeMode().onEach { mode ->
            sendIntent(SettingsIntent.OnThemeModeLoaded(mode))
        }.launchIn(viewModelScope)
    }
}

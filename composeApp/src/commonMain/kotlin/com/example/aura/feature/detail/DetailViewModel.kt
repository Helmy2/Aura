package com.example.aura.feature.detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.launch

class DetailViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val navigator: AppNavigator
) : MviViewModel<DetailState, DetailIntent, Nothing>(
    initialState = DetailState()
) {
    override fun reduce(
        currentState: DetailState,
        intent: DetailIntent
    ): Pair<DetailState, Nothing?> {
        return when (intent) {
            is DetailIntent.OnError -> {
                currentState.copy(isLoading = false, error = intent.message)
            }

            is DetailIntent.OnBackClicked -> {
                navigator.back()
                currentState
            }

            is DetailIntent.OnScreenOpened -> {
                loadWallpaperById(intent.wallpaperId)
                currentState.copy(isLoading = true, error = null)
            }

            is DetailIntent.OnWallpaperLoaded -> {
                currentState.copy(
                    isLoading = false,
                    wallpaper = intent.wallpaper.toUi()
                )
            }
        }.only()
    }


    fun loadWallpaperById(id: Long) {
        viewModelScope.launch {
            try {
                val result = wallpaperRepository.getWallpaperById(id)
                sendIntent(DetailIntent.OnWallpaperLoaded(result))
            } catch (e: Exception) {
                sendIntent(DetailIntent.OnError(e.message ?: "Unknown error"))
            }
        }
    }
}

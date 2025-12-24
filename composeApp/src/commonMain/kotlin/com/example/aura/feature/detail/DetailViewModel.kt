package com.example.aura.feature.detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.BaseViewModel
import com.example.aura.shared.core.mvi.ReducerResult
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.launch

class DetailViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val navigator: AppNavigator
) : BaseViewModel<DetailState, DetailIntent, Nothing>(
    initialState = DetailState()
) {
    override fun reduce(
        oldState: DetailState,
        intent: DetailIntent
    ): ReducerResult<DetailState, Nothing> {
        return when (intent) {
            is DetailIntent.OnError -> {
                ReducerResult(oldState.copy(isLoading = false, error = intent.message))
            }

            is DetailIntent.OnBackClicked -> {
                navigator.back()
                ReducerResult(
                    newState = oldState,
                )
            }

            is DetailIntent.OnScreenOpened -> {
                loadWallpaperById(intent.wallpaperId)
                ReducerResult(oldState.copy(isLoading = true, error = null))
            }

            is DetailIntent.OnWallpaperLoaded -> {
                ReducerResult(
                    oldState.copy(
                        isLoading = false,
                        wallpaper = intent.wallpaper.toUi()
                    )
                )
            }
        }
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

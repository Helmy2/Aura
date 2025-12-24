package com.example.aura.feature.home

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.BaseViewModel
import com.example.aura.shared.core.mvi.ReducerResult
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.DetailRoute
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WallpaperRepository,
    private val navigator: AppNavigator
) : BaseViewModel<HomeState, HomeIntent, Nothing>(
    initialState = HomeState()
) {

    init {
        sendIntent(HomeIntent.LoadCuratedWallpapers)
    }

    override fun reduce(
        oldState: HomeState,
        intent: HomeIntent
    ): ReducerResult<HomeState, Nothing> {
        return when (intent) {
            is HomeIntent.LoadCuratedWallpapers -> {
                viewModelScope.launch {
                    try {
                        val wallpapers = repository.getCuratedWallpapers()
                        sendIntent(HomeIntent.OnWallpapersLoaded(wallpapers))
                    } catch (e: Exception) {
                        sendIntent(HomeIntent.OnError(e.message ?: "Unknown error"))
                    }
                }
                ReducerResult(oldState.copy(isLoading = true, error = null))
            }

            is HomeIntent.OnWallpapersLoaded -> {
                ReducerResult(
                    oldState.copy(
                        isLoading = false,
                        wallpapers = intent.wallpapers.map {
                            it.toUi()
                        }
                    )
                )
            }

            is HomeIntent.OnError -> {
                ReducerResult(oldState.copy(isLoading = false, error = intent.message))
            }

            is HomeIntent.OnWallpaperClicked -> {
                navigator.navigate(DetailRoute(id = intent.wallpaperId))
                ReducerResult(
                    newState = oldState,
                )
            }
        }
    }
}

package com.example.aura.feature.home

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.DetailRoute
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WallpaperRepository,
    private val navigator: AppNavigator
) : MviViewModel<HomeState, HomeIntent, Nothing>(
    initialState = HomeState()
) {

    init {
        sendIntent(HomeIntent.LoadCuratedWallpapers)
    }

    override fun reduce(
        currentState: HomeState,
        intent: HomeIntent
    ): Pair<HomeState, Nothing?> {
        return when (intent) {
            is HomeIntent.LoadCuratedWallpapers -> {
                loadWallpapers()
                currentState.copy(isLoading = true, error = null)
            }

            is HomeIntent.OnError -> {
                currentState.copy(isLoading = false, error = intent.message)
            }

            is HomeIntent.OnWallpaperClicked -> {
                navigator.navigate(DetailRoute(id = intent.wallpaperId))
                currentState
            }

            is HomeIntent.LoadNextPage -> {
                if (!currentState.isPaginationLoading && !currentState.isEndReached) {
                    loadWallpapers(page = currentState.currentPage + 1)
                    currentState.copy(
                        isPaginationLoading = true
                    )
                } else {
                    currentState
                }
            }

            is HomeIntent.AppendWallpapers -> {
                val combinedList = currentState.wallpapers + intent.newWallpapers
                currentState.copy(
                    isLoading = false,
                    isPaginationLoading = false,
                    wallpapers = combinedList,
                    currentPage = intent.page
                )
            }

            is HomeIntent.SetEndReached -> {
                currentState.copy(
                    isEndReached = true
                )
            }
        }.only()
    }

    private fun loadWallpapers(page: Int = 1) {
        viewModelScope.launch {
            try {
                val newWallpapers = repository.getCuratedWallpapers(page = page)

                if (newWallpapers.isEmpty()) {
                    sendIntent(HomeIntent.SetEndReached)
                } else {
                    val uiWallpapers = newWallpapers.map { it.toUi() }
                    sendIntent(HomeIntent.AppendWallpapers(uiWallpapers, page))
                }
            } catch (e: Exception) {
                sendIntent(HomeIntent.OnError(e.message.orEmpty()))
            }
        }
    }
}

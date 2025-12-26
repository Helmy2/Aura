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
                currentState.copy(
                    isSearchMode = false,
                    searchQuery = "",
                    isLoading = true
                )
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
                    if (currentState.isSearchMode) {
                        performSearch(currentState.searchQuery, page = currentState.currentPage + 1)
                    } else {
                        loadWallpapers(page = currentState.currentPage + 1)
                    }
                    currentState.copy(
                        isPaginationLoading = true
                    )
                } else {
                    currentState
                }
            }

            is HomeIntent.AppendWallpapers -> {
                if (currentState.isSearchMode) {
                    currentState.copy(
                        searchWallpapers = currentState.searchWallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    )
                } else {
                    currentState.copy(
                        wallpapers = currentState.wallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    )
                }
            }

            is HomeIntent.SetEndReached -> {
                currentState.copy(
                    isEndReached = true
                )
            }

            is HomeIntent.OnSearchQueryChanged -> {
                currentState.copy(searchQuery = intent.query)
            }

            is HomeIntent.OnSearchTriggered -> {
                if (currentState.searchQuery.isBlank()) {
                    currentState
                } else {
                    performSearch(query = currentState.searchQuery, page = 1)

                    currentState.copy(
                        isSearchMode = true,
                        isLoading = true,
                        isEndReached = false,
                        currentPage = 1,
                        searchWallpapers = emptyList()
                    )
                }
            }

            is HomeIntent.OnClearSearch -> {
                currentState.copy(
                    isSearchMode = false,
                    searchQuery = "",
                    isEndReached = false,
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

    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            try {
                val results = repository.searchWallpapers(query, page)

                if (results.isEmpty()) {
                    sendIntent(HomeIntent.SetEndReached)
                } else {
                    val uiWallpapers = results.map { it.toUi() }
                    sendIntent(HomeIntent.AppendWallpapers(uiWallpapers, page))
                }
            } catch (e: Exception) {
                sendIntent(HomeIntent.OnError(e.message.orEmpty()))
            }
        }
    }
}

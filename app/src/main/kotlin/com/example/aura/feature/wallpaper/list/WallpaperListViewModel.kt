package com.example.aura.feature.wallpaper.list

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WallpaperListViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val favoritesRepository: FavoritesRepository,
    private val navigator: AppNavigator
) : MviViewModel<WallpaperListState, WallpaperListIntent, Nothing>(
    initialState = WallpaperListState()
) {

    init {
        sendIntent(WallpaperListIntent.LoadCuratedWallpapers)
        observeFavorites()
    }

    override fun reduce(
        currentState: WallpaperListState, intent: WallpaperListIntent
    ): Pair<WallpaperListState, Nothing?> {
        return when (intent) {
            is WallpaperListIntent.LoadCuratedWallpapers -> {
                loadWallpapers()
                currentState.copy(
                    isSearchMode = false, searchQuery = "", isLoading = true
                ).only()
            }

            is WallpaperListIntent.OnError -> {
                currentState.copy(isLoading = false, error = intent.message).only()
            }

            is WallpaperListIntent.OnWallpaperClicked -> {
                navigator.navigate(Destination.WallpaperDetail(wallpaper = intent.wallpaper))
                currentState.only()
            }

            is WallpaperListIntent.LoadNextPage -> {
                if (!currentState.isPaginationLoading && !currentState.isEndReached) {
                    if (currentState.isSearchMode) {
                        performSearch(currentState.searchQuery, page = currentState.currentPage + 1)
                    } else {
                        loadWallpapers(page = currentState.currentPage + 1)
                    }
                    currentState.copy(isPaginationLoading = true).only()
                } else {
                    currentState.only()
                }
            }

            is WallpaperListIntent.AppendWallpapers -> {
                if (currentState.isSearchMode) {
                    currentState.copy(
                        searchWallpapers = currentState.searchWallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    ).only()
                } else {
                    currentState.copy(
                        wallpapers = currentState.wallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    ).only()
                }
            }

            is WallpaperListIntent.SetEndReached -> {
                currentState.copy(isEndReached = true).only()
            }

            is WallpaperListIntent.OnSearchQueryChanged -> {
                currentState.copy(searchQuery = intent.query).only()
            }

            is WallpaperListIntent.OnSearchTriggered -> {
                if (currentState.searchQuery.isBlank()) {
                    currentState.only()
                } else {
                    performSearch(query = currentState.searchQuery, page = 1)
                    currentState.copy(
                        isSearchMode = true,
                        isLoading = true,
                        isEndReached = false,
                        currentPage = 1,
                        searchWallpapers = emptyList()
                    ).only()
                }
            }

            is WallpaperListIntent.OnClearSearch -> {
                currentState.copy(
                    isSearchMode = false,
                    searchQuery = "",
                    isEndReached = false,
                ).only()
            }

            is WallpaperListIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        favoritesRepository.toggleFavorite(intent.wallpaper)
                    } catch (_: Exception) {

                    }
                }
                currentState.only()
            }

            is WallpaperListIntent.FavoriteStatusUpdated -> {
                val updatedWallpapers = currentState.wallpapers.map { wallpaper ->
                    wallpaper.copy(isFavorite = intent.favoriteIds.contains(wallpaper.id))
                }
                val updatedSearchWallpapers = currentState.searchWallpapers.map { wallpaper ->
                    wallpaper.copy(isFavorite = intent.favoriteIds.contains(wallpaper.id))
                }

                currentState.copy(
                    wallpapers = updatedWallpapers,
                    searchWallpapers = updatedSearchWallpapers,
                    favoriteIds = intent.favoriteIds
                ).only()
            }

            is WallpaperListIntent.OnNavigateBack -> {
                navigator.back()
                currentState.only()
            }
        }
    }

    private fun loadWallpapers(page: Int = 1) {
        viewModelScope.launch {
            try {
                val wallpapers = wallpaperRepository.getCuratedWallpapers(page = page)

                if (wallpapers.isEmpty()) {
                    sendIntent(WallpaperListIntent.SetEndReached)
                } else {
                    sendIntent(WallpaperListIntent.AppendWallpapers(wallpapers, page))
                }
            } catch (e: Exception) {
                sendIntent(WallpaperListIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            try {
                val results = wallpaperRepository.searchWallpapers(query, page)

                if (results.isEmpty()) {
                    sendIntent(WallpaperListIntent.SetEndReached)
                } else {
                    sendIntent(WallpaperListIntent.AppendWallpapers(results, page))
                }
            } catch (e: Exception) {
                sendIntent(WallpaperListIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun observeFavorites() {
        favoritesRepository.observeFavoritesWallpapers()
            .map { favorites -> favorites.map { it.id }.toSet() }
            .onEach { favoriteIds ->
                sendIntent(WallpaperListIntent.FavoriteStatusUpdated(favoriteIds))
            }.launchIn(viewModelScope)
    }
}

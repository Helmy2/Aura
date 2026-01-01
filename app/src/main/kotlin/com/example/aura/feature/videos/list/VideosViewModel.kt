package com.example.aura.feature.videos.list

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.model.Video
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.repository.VideoRepository
import com.example.aura.feature.videos.list.VideosEffect.ShowError
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination.VideoDetail
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class VideosViewModel(
    private val videoRepository: VideoRepository,
    private val favoritesRepository: FavoritesRepository,
    private val navigator: AppNavigator,
) : MviViewModel<VideosState, VideosIntent, VideosEffect>(VideosState()) {

    init {
        sendIntent(VideosIntent.LoadPopularVideos)
        observeFavorites()
    }

    override fun reduce(
        currentState: VideosState, intent: VideosIntent
    ): Pair<VideosState, VideosEffect?> {
        return when (intent) {
            is VideosIntent.LoadPopularVideos -> {
                loadPopularVideos(1)
                currentState.copy(
                    isLoading = true, isSearchMode = false, searchQuery = ""
                ).only()
            }

            is VideosIntent.OnVideoClicked -> {
                navigator.navigate(VideoDetail(intent.video))
                currentState.only()
            }

            is VideosIntent.LoadNextPage -> {
                if (currentState.isPaginationLoading || currentState.isEndReached) {
                    currentState.only()
                } else {
                    val nextPage = currentState.currentPage + 1
                    if (currentState.isSearchMode) {
                        performSearch(currentState.searchQuery, nextPage)
                    } else {
                        loadPopularVideos(nextPage)
                    }
                    currentState.copy(isPaginationLoading = true).only()
                }
            }

            is VideosIntent.OnSearchQueryChanged -> {
                currentState.copy(searchQuery = intent.query).only()
            }

            is VideosIntent.OnSearchTriggered -> {
                if (currentState.searchQuery.isBlank()) {
                    currentState.only()
                } else {
                    performSearch(currentState.searchQuery, 1)
                    currentState.copy(
                        isSearchMode = true,
                        isLoading = true,
                        isEndReached = false,
                        currentPage = 1,
                        searchVideos = emptyList()
                    ).only()
                }
            }

            is VideosIntent.OnClearSearch -> {
                currentState.copy(
                    isSearchMode = false, searchQuery = "", isEndReached = false, currentPage = 1
                ).only()
            }

            is VideosIntent.VideosLoaded -> {
                val newVideos =
                    if (intent.page == 1) intent.newVideos else currentState.popularVideos + intent.newVideos
                currentState.copy(
                    popularVideos = newVideos,
                    isLoading = false,
                    isPaginationLoading = false,
                    currentPage = intent.page,
                    isEndReached = intent.newVideos.isEmpty()
                ).only()
            }

            is VideosIntent.SearchResultsLoaded -> {
                val newVideos =
                    if (intent.page == 1) intent.videos else currentState.searchVideos + intent.videos
                currentState.copy(
                    searchVideos = newVideos,
                    isLoading = false,
                    isPaginationLoading = false,
                    currentPage = intent.page,
                    isEndReached = intent.videos.isEmpty()
                ).only()
            }

            is VideosIntent.OnError -> {
                currentState.copy(
                    isLoading = false, isPaginationLoading = false, error = intent.message
                ).with(ShowError(intent.message))
            }

            VideosIntent.EndReached -> {
                currentState.copy(isEndReached = true).only()
            }

            VideosIntent.OnNavigateBack -> {
                navigator.back()
                currentState.only()
            }

            is VideosIntent.OnFavoriteClicked -> {
                toggleFavorite(intent.video)
                currentState.only()
            }

            is VideosIntent.FavoriteStatusUpdated -> {
                val updatedPopular = currentState.popularVideos.map {
                    if (it.id == intent.videoId) it.copy(isFavorite = intent.isFavorite) else it
                }
                val updatedSearch = currentState.searchVideos.map {
                    if (it.id == intent.videoId) it.copy(isFavorite = intent.isFavorite) else it
                }
                currentState.copy(
                    popularVideos = updatedPopular,
                    searchVideos = updatedSearch
                ).only()
            }
        }
    }

    private fun toggleFavorite(video: Video) {
        viewModelScope.launch {
            try {
                favoritesRepository.toggleFavorite(video)
            } catch (e: Exception) {
                sendIntent(VideosIntent.OnError(e.message ?: "Failed to toggle favorite"))
            }
        }
    }

    private fun loadPopularVideos(page: Int) {
        viewModelScope.launch {
            try {
                val videos = videoRepository.getPopularVideos(page)
                sendIntent(VideosIntent.VideosLoaded(videos, page))
            } catch (e: Exception) {
                sendIntent(VideosIntent.OnError(e.message ?: "Failed to load videos"))
            }
        }
    }

    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            try {
                val videos = videoRepository.searchVideos(query, page)
                sendIntent(VideosIntent.SearchResultsLoaded(videos, page))
            } catch (e: Exception) {
                sendIntent(VideosIntent.OnError(e.message ?: "Search failed"))
            }
        }
    }

    private fun observeFavorites() {
        favoritesRepository.observeFavoriteVideos()
            .map { favorites -> favorites.map { it.id }.toSet() }
            .onEach { favoriteIds ->
                val allVideos = currentState.popularVideos + currentState.searchVideos
                allVideos.forEach { video ->
                    val isFav = video.id in favoriteIds
                    if (video.isFavorite != isFav) {
                        sendIntent(VideosIntent.FavoriteStatusUpdated(video.id, isFav))
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}

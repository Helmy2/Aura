package com.example.aura.feature.videos

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.VideoRepository
import com.example.aura.feature.videos.VideosEffect.ShowError
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination
import kotlinx.coroutines.launch

class VideosViewModel(
    private val videoRepository: VideoRepository, private val navigator: AppNavigator
) : MviViewModel<VideosState, VideosIntent, VideosEffect>(VideosState()) {

    init {
        sendIntent(VideosIntent.LoadPopularVideos)
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
                navigator.navigate(Destination.VideoDetail(intent.video.id))
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
}

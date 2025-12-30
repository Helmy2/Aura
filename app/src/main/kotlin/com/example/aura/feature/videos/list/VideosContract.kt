package com.example.aura.feature.videos.list

import com.example.aura.shared.model.VideoUi


data class VideosState(
    val popularVideos: List<VideoUi> = emptyList(),
    val searchVideos: List<VideoUi> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val isEndReached: Boolean = false,
    val isSearchMode: Boolean = false,
    val searchQuery: String = ""
)

sealed class VideosIntent {
    data object LoadPopularVideos : VideosIntent()
    data object LoadNextPage : VideosIntent()
    data class VideosLoaded(val newVideos: List<VideoUi>, val page: Int) : VideosIntent()
    data object EndReached : VideosIntent()
    data class OnError(val message: String) : VideosIntent()
    data class OnVideoClicked(val video: VideoUi) : VideosIntent()
    data class OnSearchQueryChanged(val query: String) : VideosIntent()
    data object OnSearchTriggered : VideosIntent()
    data object OnClearSearch : VideosIntent()
    data object OnNavigateBack : VideosIntent()
    data class SearchResultsLoaded(val videos: List<VideoUi>, val page: Int) : VideosIntent()
}

sealed class VideosEffect {
    data class ShowError(val message: String) : VideosEffect()
}

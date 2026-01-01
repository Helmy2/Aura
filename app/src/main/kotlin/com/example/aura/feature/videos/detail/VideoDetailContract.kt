package com.example.aura.feature.videos.detail

import com.example.aura.domain.model.Video

data class VideoDetailState(
    val video: Video? = null,
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val error: String? = null
)

sealed class VideoDetailIntent {
    data class LoadVideo(val video: Video) : VideoDetailIntent()
    data class VideoLoaded(val video: Video) : VideoDetailIntent()
    data class LoadError(val message: String) : VideoDetailIntent()
    data object OnBackClicked : VideoDetailIntent()
    data object DownloadVideo : VideoDetailIntent()
    data class DownloadFinished(val success: Boolean) : VideoDetailIntent()

    data object ToggleFavorite : VideoDetailIntent()
    data class FavoriteStatusUpdated(val isFavorite: Boolean) : VideoDetailIntent()
}

sealed class VideoDetailEffect {
    data class ShowError(val message: String) : VideoDetailEffect()
    data class ShowMessage(val message: String) : VideoDetailEffect()
}

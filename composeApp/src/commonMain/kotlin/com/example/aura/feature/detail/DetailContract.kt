package com.example.aura.feature.detail

import com.example.aura.domain.model.Wallpaper
import com.example.aura.shared.model.WallpaperUi

data class DetailState(
    val isLoading: Boolean = true,
    val isDownloading: Boolean = false,
    val error: String? = null,
    val wallpaper: WallpaperUi? = null
)

sealed interface DetailIntent {
    data class OnError(val message: String) : DetailIntent
    data class OnScreenOpened(val wallpaperId: Long) : DetailIntent
    data class OnWallpaperLoaded(val wallpaper: Wallpaper) : DetailIntent
    data object OnBackClicked : DetailIntent
    data object DownloadImage : DetailIntent
    data object DownloadStarted : DetailIntent
    data class DownloadFinished(val success: Boolean) : DetailIntent
}

sealed interface DetailEffect {
    data class ShowToast(val message: String) : DetailEffect
}
package com.example.aura.feature.wallpaper.detail

import com.example.aura.shared.model.WallpaperUi

data class WallpaperDetailState(
    val wallpaper: WallpaperUi? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDownloading: Boolean = false
)

sealed interface WallpaperDetailIntent {
    data class LoadWallpaper(val wallpaperId: Long) : WallpaperDetailIntent
    data class WallpaperLoaded(val wallpaper: WallpaperUi) : WallpaperDetailIntent
    data class LoadError(val message: String) : WallpaperDetailIntent

    data class ToggleFavorite(val wallpaper: WallpaperUi) : WallpaperDetailIntent
    data class FavoriteStatusUpdated(val isFavorite: Boolean) : WallpaperDetailIntent

    data object DownloadWallpaper : WallpaperDetailIntent
    data class DownloadError(val message: String) : WallpaperDetailIntent
    data object OnBackClicked : WallpaperDetailIntent
    data class DownloadFinished(val success: Boolean) : WallpaperDetailIntent
}

sealed interface WallpaperDetailEffect {
    data class ShowError(val message: String) : WallpaperDetailEffect
}
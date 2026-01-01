package com.example.aura.feature.wallpaper.detail

import com.example.aura.domain.model.Wallpaper

data class WallpaperDetailState(
    val wallpaper: Wallpaper? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDownloading: Boolean = false
)

sealed interface WallpaperDetailIntent {
    data class LoadWallpaper(val wallpaper: Wallpaper) : WallpaperDetailIntent
    data class WallpaperLoaded(val wallpaper: Wallpaper) : WallpaperDetailIntent
    data class LoadError(val message: String) : WallpaperDetailIntent

    data class ToggleFavorite(val wallpaper: Wallpaper) : WallpaperDetailIntent
    data class FavoriteStatusUpdated(val isFavorite: Boolean) : WallpaperDetailIntent

    data object DownloadWallpaper : WallpaperDetailIntent
    data class DownloadError(val message: String) : WallpaperDetailIntent
    data object OnBackClicked : WallpaperDetailIntent
    data class DownloadFinished(val success: Boolean) : WallpaperDetailIntent
}

sealed interface WallpaperDetailEffect {
    data class ShowError(val message: String) : WallpaperDetailEffect
}
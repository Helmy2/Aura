package com.example.aura.feature.detail

import com.example.aura.domain.model.Wallpaper
import com.example.aura.shared.model.WallpaperUi

data class DetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val wallpaper: WallpaperUi? = null
)

sealed interface DetailIntent {
    data class OnError(val message: String) : DetailIntent
    data class OnScreenOpened(val wallpaperId: Long) : DetailIntent
    data class OnWallpaperLoaded(val wallpaper: Wallpaper) : DetailIntent
    data object OnBackClicked : DetailIntent
}
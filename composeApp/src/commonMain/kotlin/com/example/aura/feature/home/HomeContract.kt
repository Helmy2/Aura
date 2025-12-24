package com.example.aura.feature.home

import com.example.aura.domain.model.Wallpaper
import com.example.aura.shared.model.WallpaperUi

data class HomeState(
    val isLoading: Boolean = true,
    val wallpapers: List<WallpaperUi> = emptyList(),
    val error: String? = null
)

sealed interface HomeIntent {
    data object LoadCuratedWallpapers : HomeIntent
    data class OnWallpapersLoaded(val wallpapers: List<Wallpaper>) : HomeIntent
    data class OnError(val message: String) : HomeIntent
    data class OnWallpaperClicked(val wallpaperId: Long) : HomeIntent
}
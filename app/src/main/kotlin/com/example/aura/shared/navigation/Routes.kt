package com.example.aura.shared.navigation

import androidx.navigation3.runtime.NavKey
import com.example.aura.domain.model.Video
import com.example.aura.domain.model.Wallpaper
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination

    @Serializable
    data object Favorites : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object WallpaperList : Destination

    @Serializable
    data class WallpaperDetail(val wallpaper: Wallpaper) : Destination

    @Serializable
    data object VideoList : Destination

    @Serializable
    data class VideoDetail(val video: Video) : Destination
}
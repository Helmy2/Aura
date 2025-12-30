package com.example.aura.shared.navigation

import androidx.navigation3.runtime.NavKey
import com.example.aura.shared.model.VideoUi
import com.example.aura.shared.model.WallpaperUi
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
    data class WallpaperDetail(val wallpaper: WallpaperUi) : Destination

    @Serializable
    data object VideoList : Destination

    @Serializable
    data class VideoDetail(val video: VideoUi) : Destination
}
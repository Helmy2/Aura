package com.example.aura.shared.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination

    @Serializable
    data class Detail(val id: Long) : Destination

    @Serializable
    data object Favorites : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object Videos : Destination

    @Serializable
    data class VideoDetail(val id: Long) : Destination
}
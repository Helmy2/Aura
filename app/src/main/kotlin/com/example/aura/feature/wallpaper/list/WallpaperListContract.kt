package com.example.aura.feature.wallpaper.list

import com.example.aura.domain.model.Wallpaper

data class WallpaperListState(
    val isLoading: Boolean = true,
    val isPaginationLoading: Boolean = false,
    val currentPage: Int = 1,
    val isEndReached: Boolean = false,
    val wallpapers: List<Wallpaper> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val searchWallpapers: List<Wallpaper> = emptyList(),
    val favoriteIds: Set<Long> = emptySet()
)

sealed interface WallpaperListIntent {
    data object LoadCuratedWallpapers : WallpaperListIntent
    data object LoadNextPage : WallpaperListIntent
    data class OnError(val message: String) : WallpaperListIntent
    data class OnWallpaperClicked(val wallpaper: Wallpaper) : WallpaperListIntent

    data class AppendWallpapers(
        val newWallpapers: List<Wallpaper>,
        val page: Int
    ) : WallpaperListIntent

    data object SetEndReached : WallpaperListIntent

    data class OnSearchQueryChanged(val query: String) : WallpaperListIntent
    data object OnSearchTriggered : WallpaperListIntent
    data object OnClearSearch : WallpaperListIntent
    data object OnNavigateBack : WallpaperListIntent

    data class ToggleFavorite(val wallpaper: Wallpaper) : WallpaperListIntent
    data class FavoriteStatusUpdated(val favoriteIds: Set<Long>) : WallpaperListIntent

}
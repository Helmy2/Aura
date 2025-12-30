package com.example.aura.feature.wallpaper.detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.feature.wallpaper.detail.WallpaperDetailEffect.ShowError
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.DownloadError
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.DownloadFinished
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.DownloadWallpaper
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.FavoriteStatusUpdated
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.LoadError
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.LoadWallpaper
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.OnBackClicked
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.ToggleFavorite
import com.example.aura.feature.wallpaper.detail.WallpaperDetailIntent.WallpaperLoaded
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.core.util.ImageDownloader
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WallpaperViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val imageDownloader: ImageDownloader,
    private val navigator: AppNavigator
) : MviViewModel<WallpaperDetailState, WallpaperDetailIntent, WallpaperDetailEffect>(
    initialState = WallpaperDetailState()
) {

    override fun reduce(
        currentState: WallpaperDetailState,
        intent: WallpaperDetailIntent
    ): Pair<WallpaperDetailState, WallpaperDetailEffect?> {
        return when (intent) {
            is LoadWallpaper -> {
                loadWallpaper(intent.wallpaperId)
                currentState.copy(isLoading = true).only()
            }

            is OnBackClicked -> {
                navigator.back()
                currentState.only()
            }

            is WallpaperLoaded -> {
                observeFavoriteStatus(intent.wallpaper.id)
                currentState.copy(
                    wallpaper = intent.wallpaper,
                    isLoading = false,
                    error = null
                ).only()
            }

            is LoadError -> {
                currentState.copy(
                    isLoading = false,
                    error = intent.message
                ).with(ShowError(intent.message))
            }

            is ToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        val wallpaper = wallpaperRepository.getWallpaperById(intent.wallpaper.id)
                        wallpaperRepository.toggleFavorite(wallpaper)
                    } catch (e: Exception) {
                        sendIntent(DownloadError(e.message ?: "Failed to update favorite"))
                    }
                }
                currentState.only()
            }

            is FavoriteStatusUpdated -> {
                currentState.wallpaper?.let { wallpaper ->
                    currentState.copy(
                        wallpaper = wallpaper.copy(isFavorite = intent.isFavorite)
                    ).only()
                } ?: currentState.only()
            }

            is DownloadWallpaper -> {
                downloadWallpaper()
                currentState.copy(isDownloading = true).only()
            }

            is DownloadError -> {
                currentState.copy(isDownloading = false)
                    .with(ShowError(intent.message))
            }

            is DownloadFinished -> {
                currentState.copy(isDownloading = false).only()
            }
        }
    }

    private fun loadWallpaper(wallpaperId: Long) {
        viewModelScope.launch {
            try {
                val wallpaper = wallpaperRepository.getWallpaperById(wallpaperId)
                val isFavorite =
                    wallpaperRepository.isFavorite(wallpaperId) // ✅ Use unified repository
                sendIntent(WallpaperLoaded(wallpaper.toUi(isFavorite)))
            } catch (e: Exception) {
                sendIntent(LoadError(e.message ?: "Failed to load wallpaper"))
            }
        }
    }

    private fun observeFavoriteStatus(wallpaperId: Long) {
        wallpaperRepository.observeFavorites()
            .map { favorites -> favorites.any { it.id == wallpaperId } }
            .onEach { isFavorite ->
                sendIntent(FavoriteStatusUpdated(isFavorite))
            }
            .launchIn(viewModelScope)
    }

    private fun downloadWallpaper() {
        val wallpaper = currentState.wallpaper ?: return
        viewModelScope.launch {
            val fileName = "aura_${wallpaper.id}"
            val success = imageDownloader.downloadImage(wallpaper.imageUrl, fileName)
            sendIntent(DownloadFinished(success))
        }
    }
}

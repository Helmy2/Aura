package com.example.aura.feature.detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.util.ImageDownloader
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.launch

class DetailViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val navigator: AppNavigator,
    private val imageDownloader: ImageDownloader
) : MviViewModel<DetailState, DetailIntent, DetailEffect?>(
    initialState = DetailState()
) {
    override fun reduce(
        currentState: DetailState,
        intent: DetailIntent
    ): Pair<DetailState, DetailEffect?> {
        return when (intent) {
            is DetailIntent.OnError -> {
                currentState.copy(isLoading = false, error = intent.message).only()
            }

            is DetailIntent.OnBackClicked -> {
                navigator.back()
                currentState.only()
            }

            is DetailIntent.OnScreenOpened -> {
                loadWallpaperById(intent.wallpaperId)
                currentState.copy(isLoading = true, error = null).only()
            }

            is DetailIntent.OnWallpaperLoaded -> {
                currentState.copy(
                    isLoading = false,
                    wallpaper = intent.wallpaper.toUi()
                ).only()
            }

            is DetailIntent.DownloadImage -> {
                downloadWallpaper()
                currentState.only()
            }

            is DetailIntent.DownloadStarted -> {
                currentState.copy(isDownloading = true).only()
            }

            is DetailIntent.DownloadFinished -> {
                val message = if (intent.success) "Image Saved to Gallery" else "Download Failed"
                currentState.copy(isDownloading = false).with(DetailEffect.ShowToast(message))
            }
        }
    }


    fun loadWallpaperById(id: Long) {
        viewModelScope.launch {
            try {
                val result = wallpaperRepository.getWallpaperById(id)
                sendIntent(DetailIntent.OnWallpaperLoaded(result))
            } catch (e: Exception) {
                sendIntent(DetailIntent.OnError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun downloadWallpaper() {
        val wallpaper = currentState.wallpaper ?: return

        viewModelScope.launch {
            sendIntent(DetailIntent.DownloadStarted)

            val fileName = "aura_${wallpaper.id}"
            val success = imageDownloader.downloadImage(wallpaper.imageUrl, fileName)

            sendIntent(DetailIntent.DownloadFinished(success))
        }
    }
}

package com.example.aura.feature.video_detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.model.Video
import com.example.aura.domain.repository.VideoRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.core.util.VideoDownloader
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.launch

class VideoDetailViewModel(
    private val videoRepository: VideoRepository,
    private val navigator: AppNavigator,
    private val videoDownloader: VideoDownloader
) : MviViewModel<VideoDetailState, VideoDetailIntent, VideoDetailEffect>(VideoDetailState()) {

    override fun reduce(
        currentState: VideoDetailState,
        intent: VideoDetailIntent
    ): Pair<VideoDetailState, VideoDetailEffect?> {
        return when (intent) {
            is VideoDetailIntent.LoadVideo -> {
                loadVideo(intent.videoId)
                currentState.copy(isLoading = true).only()
            }

            is VideoDetailIntent.VideoLoaded -> {
                currentState.copy(video = intent.video, isLoading = false).only()
            }

            is VideoDetailIntent.LoadError -> {
                currentState.copy(isLoading = false, error = intent.message)
                    .with(VideoDetailEffect.ShowError(intent.message))
            }

            is VideoDetailIntent.OnBackClicked -> {
                navigator.back()
                currentState.only()
            }

            is VideoDetailIntent.DownloadVideo -> {
                if (currentState.video != null) {
                    downloadVideo(currentState.video)
                    currentState.copy(isDownloading = true).only()
                } else {
                    currentState.with(VideoDetailEffect.ShowError("Video not found"))
                }
            }

            is VideoDetailIntent.DownloadFinished -> {
                val message = if (intent.success) "Download started" else "Download failed"
                currentState.copy(isDownloading = false)
                    .with(VideoDetailEffect.ShowMessage(message))
            }
        }
    }

    private fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            try {
                val video = videoRepository.getVideoById(videoId)
                sendIntent(VideoDetailIntent.VideoLoaded(video))
            } catch (e: Exception) {
                sendIntent(VideoDetailIntent.LoadError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun downloadVideo(video: Video) {
        try {
            videoDownloader.downloadVideo(video.videoUrl, "aura_video_${video.id}")
            sendIntent(VideoDetailIntent.DownloadFinished(true))
        } catch (e: Exception) {
            sendIntent(VideoDetailIntent.DownloadFinished(false))
        }
    }
}

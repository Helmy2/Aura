package com.example.aura.shared.model

import androidx.compose.runtime.Immutable
import com.example.aura.domain.model.Video
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class VideoUi(
    val id: Long,
    val width: Int,
    val height: Int,
    val duration: Int,
    val thumbnail: String,
    val videoUrl: String,
    val photographer: String,
    val photographerUrl: String
)


fun Video.toUi(): VideoUi {
    return VideoUi(
        id = id,
        width = width,
        height = height,
        duration = duration,
        thumbnail = thumbnail,
        videoUrl = videoUrl,
        photographer = photographer,
        photographerUrl = photographerUrl
    )
}


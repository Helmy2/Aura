package com.example.aura.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponseDto(
    @SerialName("page") val page: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("videos") val videos: List<VideoDto>,
    @SerialName("total_results") val totalResults: Int,
    @SerialName("next_page") val nextPage: String? = null
)

@Serializable
data class VideoDto(
    @SerialName("id") val id: Long,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("url") val url: String,
    @SerialName("image") val image: String,
    @SerialName("duration") val duration: Int,
    @SerialName("video_files") val videoFiles: List<VideoFileDto>
)

@Serializable
data class VideoFileDto(
    @SerialName("id") val id: Int,
    @SerialName("quality") val quality: String,
    @SerialName("file_type") val fileType: String,
    @SerialName("width") val width: Int?,
    @SerialName("height") val height: Int?,
    @SerialName("link") val link: String
)

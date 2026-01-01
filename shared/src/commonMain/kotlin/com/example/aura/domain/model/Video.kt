package com.example.aura.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: Long,
    val width: Int,
    val height: Int,
    val url: String,
    val image: String,
    val duration: Int,
    val user: User,
    val videoFiles: List<VideoFile>,
    val videoPictures: List<VideoPicture>,
    var isFavorite: Boolean,
    val addedAt: Long
) {
    val videoUrl: String get() = videoFiles.firstOrNull { it.quality == "hd" }?.link ?: url
    val imageUrl: String get() = videoPictures.firstOrNull()?.picture ?: image
}

@Serializable
data class User(
    val id: Long,
    val name: String,
    val url: String
)

@Serializable
data class VideoFile(
    val id: Long,
    val quality: String,
    val fileType: String,
    val width: Int?,
    val height: Int?,
    val fps: Double?,
    val link: String
)

@Serializable
data class VideoPicture(
    val id: Long,
    val picture: String,
    val nr: Int
)
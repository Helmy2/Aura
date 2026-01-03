package com.example.aura.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    val page: Int? = 0,
    @SerialName("per_page") val perPage: Int? = 0,
    val videos: List<VideoDto> = emptyList(),
    @SerialName("total_results") val totalResults: Int? = 0,
    @SerialName("next_page") val nextPage: String? = null
)

@Serializable
data class VideoDto(
    val id: Long,
    val width: Int? = 0,   
    val height: Int? = 0,  
    val url: String,
    val image: String,
    val duration: Int? = 0, 
    val user: UserDto,
    @SerialName("video_files") val videoFiles: List<VideoFileDto>? = emptyList(), 
    @SerialName("video_pictures") val videoPictures: List<VideoPictureDto>? = emptyList() 
)

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val url: String
)

@Serializable
data class VideoFileDto(
    val id: Long,
    val quality: String? = null, 
    @SerialName("file_type") val fileType: String? = null, 
    val width: Int? = null,
    val height: Int? = null,
    val fps: Double? = null,
    val link: String
)

@Serializable
data class VideoPictureDto(
    val id: Long,
    val picture: String,
    val nr: Int
)

package com.example.aura.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponse(
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val photos: List<PhotoDto>
)

@Serializable
data class PhotoDto(
    val id: Long,
    val width: Int?,
    val height: Int?,
    val url: String,
    val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("avg_color") val avgColor: String? = null,
    val src: SrcDto
)

@Serializable
data class SrcDto(
    val original: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)
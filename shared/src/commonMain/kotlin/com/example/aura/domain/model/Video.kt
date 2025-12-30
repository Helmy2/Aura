package com.example.aura.domain.model

data class Video(
    val id: Long,
    val width: Int,
    val height: Int,
    val duration: Int,
    val thumbnail: String,
    val videoUrl: String,
    val photographer: String,
    val photographerUrl: String
)

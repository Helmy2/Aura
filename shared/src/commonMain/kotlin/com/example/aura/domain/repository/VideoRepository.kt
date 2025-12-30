package com.example.aura.domain.repository

import com.example.aura.domain.model.Video

interface VideoRepository {
    suspend fun getPopularVideos(page: Int): List<Video>
    suspend fun searchVideos(query: String, page: Int): List<Video>
    suspend fun getVideoById(id: Long): Video
}
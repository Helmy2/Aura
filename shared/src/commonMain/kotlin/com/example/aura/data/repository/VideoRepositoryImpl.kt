package com.example.aura.data.repository

import com.example.aura.data.mapper.toDomain
import com.example.aura.data.remote.PexelsRemoteDataSource
import com.example.aura.domain.model.Video
import com.example.aura.domain.repository.VideoRepository

class VideoRepositoryImpl(
    private val remoteDataSource: PexelsRemoteDataSource
) : VideoRepository {

    override suspend fun getPopularVideos(page: Int): List<Video> {
        val response = remoteDataSource.getPopularVideos(page)
        return response.videos.map { it.toDomain() }
    }

    override suspend fun searchVideos(query: String, page: Int): List<Video> {
        val response = remoteDataSource.searchVideos(query, page)
        return response.videos.map { it.toDomain() }
    }

    override suspend fun getVideoById(id: Long): Video {
        return remoteDataSource.getVideoById(id).toDomain()
    }
}
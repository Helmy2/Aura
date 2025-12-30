package com.example.aura.data.remote

import com.example.aura.data.remote.model.PexelsResponseDto
import com.example.aura.data.remote.model.PhotoDto
import com.example.aura.data.remote.model.VideoDto
import com.example.aura.data.remote.model.VideoResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class PexelsRemoteDataSource(val client: HttpClient) {

    suspend fun getCuratedWallpapers(page: Int = 1, perPage: Int = 30): PexelsResponseDto {
        return client.get("https://api.pexels.com/v1/curated") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun searchWallpapers(
        query: String,
        page: Int = 1,
        perPage: Int = 30
    ): PexelsResponseDto {
        return client.get("https://api.pexels.com/v1/search") {
            parameter("query", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun getWallpaperById(id: Long): PhotoDto {
        return client.get("https://api.pexels.com/v1/photos/$id").body()
    }

    suspend fun getPopularVideos(page: Int, perPage: Int = 20): VideoResponseDto {
        return client.get {
            url("https://api.pexels.com/videos/search")
            parameter("query", "nature")
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun searchVideos(query: String, page: Int, perPage: Int = 20): VideoResponseDto {
        return client.get {
            url("https://api.pexels.com/videos/search")
            parameter("query", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun getVideoById(id: Long): VideoDto {
        return client.get {
            url("https://api.pexels.com/videos/videos/$id")
        }.body()
    }
}

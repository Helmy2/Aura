package com.example.aura.data.remote

import com.example.aura.data.remote.model.PexelsResponseDto
import com.example.aura.data.remote.model.PhotoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PexelsApi(val client: HttpClient) {

    suspend fun getWallpapers(page: Int = 1, perPage: Int = 30): PexelsResponseDto {
        return client.get("curated") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun searchWallpapers(
        query: String,
        page: Int = 1,
        perPage: Int = 30
    ): PexelsResponseDto {
        return client.get("search") {
            parameter("query", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    suspend fun getWallpaperById(id: Long): PhotoDto {
        return client.get("photos/$id").body()
    }
}

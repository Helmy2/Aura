package com.example.aura.data.repository

import com.example.aura.data.mapper.toDomain
import com.example.aura.data.remote.PexelsApi
import com.example.aura.domain.model.Wallpaper
import com.example.aura.domain.repository.WallpaperRepository

class WallpaperRepositoryImpl(
    private val api: PexelsApi
) : WallpaperRepository {

    override suspend fun getCuratedWallpapers(page: Int): List<Wallpaper> {
        val response = api.getWallpapers(page = page)
        return response.photos.map { it.toDomain() }
    }

    override suspend fun searchWallpapers(query: String): List<Wallpaper> {
        val response = api.searchWallpapers(query = query)
        return response.photos.map { it.toDomain() }
    }

    override suspend fun getWallpaperById(id: Long): Wallpaper {
        val response = api.getWallpaperById(id = id)
        return response.toDomain()
    }
}

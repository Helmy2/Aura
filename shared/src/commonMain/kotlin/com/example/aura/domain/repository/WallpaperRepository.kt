package com.example.aura.domain.repository

import com.example.aura.domain.model.Wallpaper

interface WallpaperRepository {
    @Throws(Exception::class)
    suspend fun getCuratedWallpapers(page: Int = 1): List<Wallpaper>

    @Throws(Exception::class)
    suspend fun searchWallpapers(query: String): List<Wallpaper>

    @Throws(Exception::class)
    suspend fun getWallpaperById(id: Long): Wallpaper
}

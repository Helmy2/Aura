package com.example.aura.data.mapper

import com.example.aura.data.remote.model.PhotoDto
import com.example.aura.domain.model.Wallpaper

fun PhotoDto.toDomain(isFavorite: Boolean): Wallpaper {
    return Wallpaper(
        id = id,
        imageUrl = src.original,
        smallImageUrl = src.medium,
        photographer = photographer,
        photographerUrl = photographerUrl,
        averageColor = avgColor ?: "#CCCCCC",
        height = height ?: 0,
        width = width ?: 0,
        addedAt = 0,
        isFavorite = isFavorite,
    )
}

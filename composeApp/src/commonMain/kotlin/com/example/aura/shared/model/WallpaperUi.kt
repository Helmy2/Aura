package com.example.aura.shared.model

import androidx.compose.runtime.Immutable
import com.example.aura.domain.model.Wallpaper


@Immutable
data class WallpaperUi(
    val id: Long,
    val imageUrl: String,
    val smallImageUrl: String,
    val photographerName: String,
    val contentDescription: String,
    val aspectRatio: Float,
    val averageColor: String,
)

fun Wallpaper.toUi(): WallpaperUi {
    return WallpaperUi(
        id = this.id,
        imageUrl = this.imageUrl,
        smallImageUrl = this.smallImageUrl,
        photographerName = "Photo by ${this.photographer}",
        contentDescription = "Photo by ${this.photographer}",
        aspectRatio = if (this.height > 0) this.width.toFloat() / this.height.toFloat() else 0.7f,
        averageColor = averageColor
    )
}
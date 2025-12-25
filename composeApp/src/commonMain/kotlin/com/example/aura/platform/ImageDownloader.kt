package com.example.aura.platform


expect class ImageDownloader {
    suspend fun downloadImage(url: String, fileName: String): Boolean
}

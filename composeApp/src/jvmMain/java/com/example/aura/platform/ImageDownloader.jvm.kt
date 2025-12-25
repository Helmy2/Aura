package com.example.aura.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

actual class ImageDownloader {
    actual suspend fun downloadImage(url: String, fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val imageUrl = URL(url)
                // Determine user's Pictures or Downloads folder
                val userHome = System.getProperty("user.home")
                val targetDir = File(userHome, "Downloads")

                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }

                val targetFile = File(targetDir, "$fileName.jpg")

                // Download and save
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                // Spoof a real browser User-Agent
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    println("Failed to download: Server returned ${connection.responseCode}")
                    return@withContext false
                }

                connection.inputStream.use { input ->
                    Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

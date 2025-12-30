package com.example.aura.data.mapper

import com.example.aura.data.remote.model.VideoDto
import com.example.aura.domain.model.Video

fun VideoDto.toDomain(): Video {
    val bestFile = videoFiles.find { it.quality == "hd" && it.fileType == "video/mp4" }
        ?: videoFiles.find { it.quality == "sd" && it.fileType == "video/mp4" }
        ?: videoFiles.firstOrNull()

    return Video(
        id = id,
        width = width,
        height = height,
        duration = duration,
        thumbnail = image,
        videoUrl = bestFile?.link ?: "",
        photographer = "",
        photographerUrl = ""
    )
}

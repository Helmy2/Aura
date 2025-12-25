package com.example.aura.shared.core.extensions

import androidx.compose.ui.graphics.Color

fun String.toColor(): Color {
    val hex = this.removePrefix("#")
    val colorLong = hex.toLong(16)

    return when (hex.length) {
        6 -> Color(0xFF000000 or colorLong)
        8 -> Color(colorLong)
        else -> error("Invalid color hex: $this")
    }
}
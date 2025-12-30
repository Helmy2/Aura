package com.example.aura.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String?): ThemeMode {
            return entries.find { it.name == value } ?: SYSTEM
        }
    }
}

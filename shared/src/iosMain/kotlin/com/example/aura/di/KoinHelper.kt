package com.example.aura.di

import com.example.aura.domain.repository.SettingsRepository
import com.example.aura.domain.repository.WallpaperRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinHelper : KoinComponent {
    val wallpaperRepository: WallpaperRepository by inject()
    val settingsRepository: SettingsRepository by inject()
}

fun doInitKoin() {
    initKoin()
}
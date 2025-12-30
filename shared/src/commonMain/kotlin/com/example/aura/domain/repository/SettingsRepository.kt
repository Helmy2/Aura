package com.example.aura.domain.repository

import com.example.aura.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun getThemeMode(): ThemeMode
    suspend fun updateThemeMode(mode: ThemeMode)
}

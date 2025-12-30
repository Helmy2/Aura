package com.example.aura.data.repository

import com.example.aura.data.local.SettingsLocalDataSource
import com.example.aura.domain.model.ThemeMode
import com.example.aura.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun observeThemeMode(): Flow<ThemeMode> {
        return localDataSource.observeThemeMode()
    }

    override suspend fun getThemeMode(): ThemeMode {
        return localDataSource.getThemeMode()
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        localDataSource.updateThemeMode(mode)
    }
}

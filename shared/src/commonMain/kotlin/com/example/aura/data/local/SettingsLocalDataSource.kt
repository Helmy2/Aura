package com.example.aura.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.aura.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsLocalDataSource(
    val dataStore: DataStore<Preferences>
) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    fun observeThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            ThemeMode.fromString(preferences[THEME_MODE_KEY])
        }
    }

    suspend fun getThemeMode(): ThemeMode {
        val preferences = dataStore.data.map { it }.first()
        return ThemeMode.fromString(preferences[THEME_MODE_KEY])
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}

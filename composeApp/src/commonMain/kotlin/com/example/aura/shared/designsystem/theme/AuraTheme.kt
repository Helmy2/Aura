package com.example.aura.shared.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AuraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AuraDarkColors else AuraLightColors

    CompositionLocalProvider(
        LocalAuraDimens provides AuraDimens()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AuraTypography,
            shapes = AuraShapes,
            content = content
        )
    }
}

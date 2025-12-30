package com.example.aura.shared.component

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat

/**
 * Configures the system status bar appearance based on theme and override.
 *
 * @param isStatusBarOnDark If null, follows system theme. If true, uses light icons (dark background).
 * If false, uses dark icons (light background).
 * @param restoreOnDispose If true, restores the original status bar state when this composable leaves composition.
 */
@Composable
fun SystemBarStyle(
    isStatusBarOnDark: Boolean? = null,
    restoreOnDispose: Boolean = false
) {
    val activity = LocalActivity.current
    val isDarkTheme = isSystemInDarkTheme()

    val shouldUseLightIcons = isStatusBarOnDark ?: isDarkTheme

    val originalState = remember {
        activity?.let {
            WindowCompat.getInsetsController(it.window, it.window.decorView)
                .isAppearanceLightStatusBars
        }
    }

    DisposableEffect(shouldUseLightIcons, restoreOnDispose) {
        activity?.let {
            WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
                isAppearanceLightStatusBars = !shouldUseLightIcons
            }
        }

        onDispose {
            if (restoreOnDispose) {
                activity?.let {
                    WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
                        isAppearanceLightStatusBars = originalState ?: false
                    }
                }
            }
        }
    }
}

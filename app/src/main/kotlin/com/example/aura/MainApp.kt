package com.example.aura

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.aura.domain.model.ThemeMode
import com.example.aura.domain.repository.SettingsRepository
import com.example.aura.shared.component.SystemBarStyle
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.MainNavHost
import com.example.aura.shared.navigation.bottomNavItems
import com.example.aura.shared.theme.AuraTheme
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val navController: AppNavigator = koinInject()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()

    val currentDestination by remember {
        derivedStateOf { navController.backStack.lastOrNull() }
    }
    val baseNavigationSuiteType = remember(windowAdaptiveInfo) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    }

    val shouldShowNavigationBar = remember(baseNavigationSuiteType, currentDestination) {
        if (baseNavigationSuiteType == NavigationSuiteType.NavigationBar) {
            bottomNavItems.any { it.destination == currentDestination }
        } else {
            true
        }
    }

    val settingsRepository: SettingsRepository = koinInject()
    val themeMode by settingsRepository.observeThemeMode()
        .collectAsState(initial = ThemeMode.SYSTEM)

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    SystemBarStyle(isStatusBarOnDark = darkTheme)

    AuraTheme(darkTheme = darkTheme) {
        MainNavHost(shouldShowNavigationBar, navController, modifier)
    }
}


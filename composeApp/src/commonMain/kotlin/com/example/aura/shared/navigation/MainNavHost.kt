package com.example.aura.shared.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.aura.feature.detail.DetailScreen
import com.example.aura.feature.home.HomeScreen
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainNavHost(modifier: Modifier = Modifier) {
    val navController: AppNavigator = koinInject()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        NavDisplay(
            sceneStrategy = listDetailStrategy,
            backStack = navController.backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            modifier = modifier.padding(it),
            entryProvider = entryProvider {
                entry<HomeRoute>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    HomeScreen()
                }
                entry<DetailRoute>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { route ->
                    DetailScreen(route.id)
                }
            },
        )
    }
}

package com.example.aura.shared.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.aura.feature.detail.DetailScreen
import com.example.aura.feature.favorites.FavoritesScreen
import com.example.aura.feature.home.HomeScreen
import com.example.aura.feature.settings.SettingsScreen
import com.example.aura.feature.video_detail.VideoDetailScreen
import com.example.aura.feature.videos.VideosScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainNavHost(
    shouldSowNavigationBar: Boolean,
    navController: AppNavigator,
    modifier: Modifier = Modifier.Companion,
    listDetailStrategy: ListDetailSceneStrategy<NavKey> = rememberListDetailSceneStrategy(),
) {
    NavigationSuiteScaffoldLayout(
        navigationSuite = {
            AnimatedVisibility(shouldSowNavigationBar) {
                NavigationSuite {
                    bottomNavItems.forEach { topLevelRoute ->
                        val isSelected =
                            topLevelRoute.destination == navController.backStack.lastOrNull()
                        item(
                            selected = isSelected,
                            onClick = {
                                navController.navigateToTopLevel(topLevelRoute.destination)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) {
                                        topLevelRoute.selectedIcon
                                    } else {
                                        topLevelRoute.unselectedIcon
                                    },
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(topLevelRoute.title)
                            },
                        )
                    }
                }
            }
        }
    ) {
        NavDisplay(
            modifier = modifier.animateContentSize(),
            sceneStrategy = listDetailStrategy,
            backStack = navController.backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Destination.Home>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    HomeScreen()
                }
                entry<Destination.Favorites>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    FavoritesScreen()
                }
                entry<Destination.Detail>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { route ->
                    DetailScreen(route.id)
                }
                entry<Destination.Settings> {
                    SettingsScreen()
                }
                entry<Destination.Videos>(
                    metadata = ListDetailSceneStrategy.listPane()
                ) {
                    VideosScreen()
                }

                entry<Destination.VideoDetail>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) { route ->
                    VideoDetailScreen(route.id)
                }
            },
        )
    }
}
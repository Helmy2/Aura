package com.example.aura.feature.wallpaper.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aura.shared.component.AuraScaffold
import com.example.aura.shared.component.AuraSearchBar
import com.example.aura.shared.component.AuraTransparentTopBar
import com.example.aura.shared.component.WallpaperGallery
import com.example.aura.shared.theme.dimens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WallpaperListScreen() {
    val viewModel = koinViewModel<WallpaperListViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val listState = rememberLazyStaggeredGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 &&
                    lastVisibleIndex >= (totalItems - 5) &&
                    !state.isLoading &&
                    !state.isPaginationLoading &&
                    !state.isEndReached
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.sendIntent(WallpaperListIntent.LoadNextPage)
        }
    }

    AuraScaffold(
        topBar = {
            AuraTransparentTopBar(
                title = "Wallpapers",
                onBackClick = {
                    viewModel.sendIntent(WallpaperListIntent.OnNavigateBack)
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                WallpaperGallery(
                    contentPadding = padding,
                    listState = listState,
                    wallpapers = if (state.isSearchMode) state.searchWallpapers else state.wallpapers,
                    onWallpaperClick = {
                        viewModel.sendIntent(WallpaperListIntent.OnWallpaperClicked(it))
                    },
                    onWallpaperFavoriteClick = {
                        viewModel.sendIntent(WallpaperListIntent.ToggleFavorite(it))
                    },
                    isPaginationLoading = state.isPaginationLoading,
                    isLoading = state.isLoading,
                    searchAppBar = {
                        AuraSearchBar(
                            query = state.searchQuery,
                            onQueryChange = {
                                viewModel.sendIntent(
                                    WallpaperListIntent.OnSearchQueryChanged(
                                        it
                                    )
                                )
                            },
                            onSearch = { viewModel.sendIntent(WallpaperListIntent.OnSearchTriggered) },
                            onClearSearch = {
                                viewModel.sendIntent(WallpaperListIntent.OnClearSearch)
                            },
                            isSearchActive = state.isSearchMode,
                            modifier = Modifier.padding(bottom = MaterialTheme.dimens.md)
                        )
                    }
                )
            }
        }
    }
}
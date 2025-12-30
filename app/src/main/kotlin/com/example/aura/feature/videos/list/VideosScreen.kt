package com.example.aura.feature.videos.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.aura.shared.component.VideoGridCell
import com.example.aura.shared.theme.dimens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VideosScreen(
    viewModel: VideosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyStaggeredGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= (totalItems - 4) && !state.isLoading && !state.isPaginationLoading && !state.isEndReached
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.sendIntent(VideosIntent.LoadNextPage)
        }
    }

    AuraScaffold(
        topBar = {
            AuraTransparentTopBar(
                title = "Wallpapers",
                onBackClick = {
                    viewModel.sendIntent(VideosIntent.OnNavigateBack)
                }
            )
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val videos = if (state.isSearchMode) state.searchVideos else state.popularVideos

            if (state.isLoading && videos.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = listState,
                    contentPadding = PaddingValues(MaterialTheme.dimens.sm),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.sm),
                    verticalItemSpacing = MaterialTheme.dimens.sm,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(
                        span = StaggeredGridItemSpan.FullLine,
                    ) {
                        AuraSearchBar(
                            query = state.searchQuery,
                            onQueryChange = {
                                viewModel.sendIntent(
                                    VideosIntent.OnSearchQueryChanged(
                                        it
                                    )
                                )
                            },
                            onSearch = { viewModel.sendIntent(VideosIntent.OnSearchTriggered) },
                            onClearSearch = { viewModel.sendIntent(VideosIntent.OnClearSearch) },
                            isSearchActive = state.isSearchMode,
                            modifier = Modifier
                                .padding(MaterialTheme.dimens.md),
                        )
                    }
                    items(videos) { video ->
                        VideoGridCell(
                            video = video,
                            onClick = { viewModel.sendIntent(VideosIntent.OnVideoClicked(video)) })
                    }

                    if (state.isPaginationLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }

            if (state.error != null) {
                Text(text = "Error: ${state.error}", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

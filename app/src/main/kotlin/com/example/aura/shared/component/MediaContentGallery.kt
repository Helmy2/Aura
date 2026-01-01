package com.example.aura.shared.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.aura.domain.model.MediaContent
import com.example.aura.shared.core.extensions.plus
import com.example.aura.shared.core.extensions.shimmerEffect
import com.example.aura.shared.theme.dimens

@Composable
fun MediaContentGallery(
    items: List<MediaContent>,
    onItemClick: (MediaContent) -> Unit,
    onFavoriteClick: (MediaContent) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    isLoading: Boolean = false,
    emptyContent: (@Composable () -> Unit)? = null,
    searchAppBar: (@Composable () -> Unit)? = null,
    isPaginationLoading: Boolean = false
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass

    val minSize =
        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            250.dp
        } else if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            200.dp
        } else {
            150.dp
        }

    if (items.isEmpty() && !isLoading && !isPaginationLoading) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    contentPadding + PaddingValues(MaterialTheme.dimens.md)
                ),
        ) {
            if (searchAppBar != null)
                searchAppBar()
            if (emptyContent != null)
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    emptyContent()
                }
        }
    } else {
        LazyVerticalStaggeredGrid(
            state = listState,
            columns = StaggeredGridCells.Adaptive(minSize = minSize),
            contentPadding = contentPadding + PaddingValues(MaterialTheme.dimens.md),
            verticalItemSpacing = MaterialTheme.dimens.md,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.md),
            modifier = modifier
        ) {
            if (searchAppBar != null)
                item(span = StaggeredGridItemSpan.FullLine) {
                    searchAppBar()
                }
            if (emptyContent != null && items.isEmpty())
                item(span = StaggeredGridItemSpan.FullLine) {
                    emptyContent()
                }
            if (!isLoading)
                items(items, key = { it.id }) { item ->
                    when (item) {
                        is MediaContent.VideoContent -> VideoItem(
                            video = item.video,
                            onClick = { onItemClick(item) },
                            onFavoriteClick = { onFavoriteClick(item) },
                            modifier = Modifier.animateItem()
                        )

                        is MediaContent.WallpaperContent -> WallpaperItem(
                            wallpaper = item.wallpaper,
                            onClick = { onItemClick(item) },
                            onFavoriteClick = { onFavoriteClick(item) },
                            modifier = Modifier.animateItem()
                        )
                    }

                }
            if (isPaginationLoading || isLoading) {
                items(10) {
                    Card(
                        modifier = Modifier
                            .padding(MaterialTheme.dimens.xs)
                            .fillMaxWidth()
                            .aspectRatio(0.7f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shimmerEffect(),
                        )
                    }
                }
            }
        }
    }
}
package com.example.aura.shared.component

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.SubcomposeAsyncImage
import com.example.aura.domain.model.Wallpaper
import com.example.aura.shared.core.extensions.plus
import com.example.aura.shared.core.extensions.shimmerEffect
import com.example.aura.shared.theme.dimens

@Composable
fun WallpaperGallery(
    modifier: Modifier = Modifier.Companion,
    wallpapers: List<Wallpaper>,
    onWallpaperClick: (Wallpaper) -> Unit,
    onWallpaperFavoriteClick: (Wallpaper) -> Unit,
    isPaginationLoading: Boolean = false,
    isLoading: Boolean = false,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    searchAppBar: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null
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

    if (wallpapers.isEmpty() && !isLoading && !isPaginationLoading) {
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
            if (emptyContent != null && wallpapers.isEmpty())
                item(span = StaggeredGridItemSpan.FullLine) {
                    emptyContent()
                }
            if (!isLoading)
                items(wallpapers, key = { it.id }) { wallpaper ->
                    WallpaperItem(
                        wallpaper = wallpaper,
                        onClick = { onWallpaperClick(wallpaper) },
                        onFavoriteClick = { onWallpaperFavoriteClick(wallpaper) },
                        modifier = Modifier.animateItem()
                    )
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


@Composable
fun WallpaperItem(
    wallpaper: Wallpaper,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(wallpaper.width / wallpaper.height.toFloat())
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = wallpaper.smallImageUrl,
                contentDescription = wallpaper.photographer,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            )

            FavoriteButton(
                isFavorite = wallpaper.isFavorite,
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}
package com.example.aura.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.SubcomposeAsyncImage
import com.example.aura.shared.core.extensions.shimmerEffect
import com.example.aura.shared.designsystem.theme.dimens
import com.example.aura.shared.model.WallpaperUi
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = adaptiveInfo.windowSizeClass

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                WallpaperGrid(
                    wallpapers = state.wallpapers,
                    windowSizeClass = windowSizeClass,
                    onWallpaperClick = { id ->
                        viewModel.sendIntent(HomeIntent.OnWallpaperClicked(id))
                    }
                )
            }
        }
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun WallpaperGrid(
    wallpapers: List<WallpaperUi>,
    windowSizeClass: WindowSizeClass,
    onWallpaperClick: (Long) -> Unit,
) {
    val minSize =
        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
            250.dp
        } else if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            200.dp
        } else {
            150.dp
        }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = minSize),
        contentPadding = PaddingValues(MaterialTheme.dimens.md),
        state = rememberLazyGridState()
    ) {
        items(wallpapers, key = { it.id }) { wallpaper ->
            WallpaperItem(wallpaper, onWallpaperClick, modifier = Modifier.animateItem())
        }
    }
}

@Composable
fun WallpaperItem(
    wallpaper: WallpaperUi,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(MaterialTheme.dimens.xs)
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable { onClick(wallpaper.id) }
    ) {
        SubcomposeAsyncImage(
            model = wallpaper.smallImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            }
        )
    }
}
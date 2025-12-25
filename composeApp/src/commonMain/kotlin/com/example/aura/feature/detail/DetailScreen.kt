package com.example.aura.feature.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aura.shared.core.extensions.ObserveEffect
import com.example.aura.shared.core.extensions.toColor
import com.example.aura.shared.designsystem.component.AuraImage
import com.example.aura.shared.designsystem.component.AuraTransparentTopBar
import com.example.aura.shared.designsystem.theme.dimens
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ParamsComparedByRef")
@Composable
fun DetailScreen(
    wallpaperId: Long,
    viewModel: DetailViewModel = koinViewModel()
) {
    val snackbarState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        viewModel.sendIntent(DetailIntent.OnScreenOpened(wallpaperId))
        onDispose {

        }
    }

    ObserveEffect(flow = viewModel.effect) {
        when (it) {
            is DetailEffect.ShowToast -> {
                snackbarState.showSnackbar(it.message)
            }

            null -> {

            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                snackbarState,
                snackbar = {
                    Box(
                        modifier = Modifier
                            .clip(
                                MaterialTheme.shapes.medium
                            )
                            .background(
                                color = state.wallpaper?.averageColor?.toColor()
                                    ?: Color.Transparent
                            )
                    ) {
                        Text(
                            it.visuals.message,
                            modifier = Modifier.padding(MaterialTheme.dimens.md)
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.sendIntent(DetailIntent.DownloadImage) },
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                if (state.isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(MaterialTheme.dimens.lg),
                        color = Color.Black,
                        strokeWidth = MaterialTheme.dimens.xxs
                    )
                } else {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .background(color = state.wallpaper?.averageColor?.toColor() ?: Color.Transparent)
        ) {
            AuraImage(
                imageUrl = state.wallpaper?.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

            AuraTransparentTopBar(
                title = "Details", onBackClick = {
                    viewModel.sendIntent(DetailIntent.OnBackClicked)
                }, modifier = Modifier.align(Alignment.TopCenter)
            )

            Box(
                modifier = Modifier.fillMaxWidth().height(
                    MaterialTheme.dimens.bottomOverlayHeight
                ).align(Alignment.BottomCenter).background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
            )

            AnimatedVisibility(
                state.wallpaper != null, modifier =
                    Modifier.align(Alignment.BottomStart)
                        .padding(MaterialTheme.dimens.screenPadding)
                        .padding(bottom = padding.calculateBottomPadding())
            ) {
                Text(
                    text = state.wallpaper!!.photographerName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }
    }
}
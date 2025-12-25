package com.example.aura.feature.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aura.shared.core.extensions.toColor
import com.example.aura.shared.designsystem.component.AuraImage
import com.example.aura.shared.designsystem.component.AuraTransparentTopBar
import com.example.aura.shared.designsystem.theme.dimens
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ParamsComparedByRef")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    wallpaperId: Long,
    viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        viewModel.sendIntent(DetailIntent.OnScreenOpened(wallpaperId))
        onDispose {

        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
package com.example.aura.shared.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AuraDimens(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val screenPadding: Dp = 16.dp,
    val topBarScrimHeight: Dp = 120.dp,
    val bottomOverlayHeight: Dp = 160.dp
)

val LocalAuraDimens = staticCompositionLocalOf { AuraDimens() }

val MaterialTheme.dimens: AuraDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalAuraDimens.current

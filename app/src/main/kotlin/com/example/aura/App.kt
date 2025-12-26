package com.example.aura

import androidx.compose.runtime.Composable
import com.example.aura.shared.designsystem.theme.AuraTheme
import com.example.aura.shared.navigation.MainNavHost

@Composable
fun App() {
    AuraTheme {
        MainNavHost()
    }
}
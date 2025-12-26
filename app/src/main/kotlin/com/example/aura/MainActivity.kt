package com.example.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb(),
            )
        )
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}
package com.example.aura.di

import com.example.aura.feature.detail.DetailViewModel
import com.example.aura.feature.favorites.FavoritesViewModel
import com.example.aura.feature.home.HomeViewModel
import com.example.aura.feature.settings.SettingsViewModel
import com.example.aura.feature.video_detail.VideoDetailViewModel
import com.example.aura.feature.videos.VideosViewModel
import com.example.aura.shared.core.util.ImageDownloader
import com.example.aura.shared.core.util.VideoDownloader
import com.example.aura.shared.navigation.AppNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    single {
        ImageDownloader(androidContext())
    }
    single {
        VideoDownloader(androidContext())
    }
    single {
        AppNavigator()
    }

    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::VideosViewModel)
    viewModelOf(::VideoDetailViewModel)
}

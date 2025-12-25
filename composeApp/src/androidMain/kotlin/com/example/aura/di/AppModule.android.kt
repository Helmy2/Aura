package com.example.aura.di

import com.example.aura.platform.ImageDownloader
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single{
        ImageDownloader(androidContext())
    }
}
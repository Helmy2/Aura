package com.example.aura.di

import com.example.aura.feature.detail.DetailViewModel
import com.example.aura.feature.home.HomeViewModel
import com.example.aura.shared.navigation.AppNavigator
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    includes(platformModule)
    single {
        AppNavigator()
    }
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
}

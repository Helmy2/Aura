package com.example.aura

import android.app.Application
import com.example.aura.di.appModule
import com.example.aura.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(applicationContext)
            modules(appModule)
        }
    }
}
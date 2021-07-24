package com.drbrosdev.studytextscan

import android.app.Application
import com.drbrosdev.studytextscan.di.appModule
import com.drbrosdev.studytextscan.di.repoModule
import com.drbrosdev.studytextscan.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(listOf(appModule, repoModule, viewModelModule))
        }
    }
}
package com.app.getswipe.assignment

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.getswipe.assignment.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidContext(this@KoinApplication)
            Log.d("fatal", printLogger().toString()) // Enables debug logs
            modules(appModule)
        }
    }

}
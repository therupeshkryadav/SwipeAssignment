package com.app.getswipe.assignment.presentation.ui

import NotificationHelper
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.getswipe.assignment.presentation.navigation.AppNavGraph
import com.app.getswipe.assignment.presentation.ui.theme.SwipeAssignmentTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Show splash screen before anything else
        installSplashScreen()
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        Log.d("MainActivity", "Notification Channel Created & Splash Screen Installed")

        setContent {
            SwipeAssignmentTheme {
                AppNavGraph(context = this@MainActivity)
            }
        }
    }
}


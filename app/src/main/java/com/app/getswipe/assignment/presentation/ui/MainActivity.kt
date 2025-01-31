package com.app.getswipe.assignment.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.getswipe.assignment.presentation.navigation.AppNavGraph
import com.app.getswipe.assignment.presentation.ui.theme.SwipeAssignmentTheme
import com.app.getswipe.assignment.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        installSplashScreen()

        setContent {
            SwipeAssignmentTheme {
                AppNavGraph(context = this@MainActivity)
            }
        }
    }
}


package com.app.getswipe.assignment.presentation.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.getswipe.assignment.R
import com.app.getswipe.assignment.presentation.ui.theme.SwipeAssignmentTheme
import com.app.getswipe.assignment.utils.NotificationHelper
import kotlinx.coroutines.delay

@Composable
fun SplashContent(onSplashEnd: () -> Unit) {
    val context = LocalContext.current
    // Simulate some delay (splash screen duration)
    LaunchedEffect(Unit) {
        delay(2000) // Show splash screen for 2 seconds
        onSplashEnd()
    }

    NotificationHelper.sendNotification(
        context,
        "App Started!!",
        "Get ready!!!"
    )

    // After the splash screen, show the main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .systemBarsPadding().navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main content after splash screen
        Image(
            painter = painterResource(R.drawable.brand_logo),
            contentDescription = "Brand Logo")
        Spacer(Modifier.height(16.dp))
        Text(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally), text = "Welcome to the Swipe's Assignment!", color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SwipeAssignmentTheme {
        SplashContent{}
    }}

package com.app.getswipe.assignment.presentation.navigation

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.getswipe.assignment.presentation.ui.screens.addScreen.AddScreen
import com.app.getswipe.assignment.presentation.ui.screens.productList.ProductListScreen
import com.app.getswipe.assignment.presentation.ui.screens.splash.SplashContent

@Composable
fun AppNavGraph(
    startDestination: String = "splash_content",
    context: Context
) {
    val navController = rememberNavController()


    NavHost(navController, startDestination = startDestination) {
        composable("splash_content") {
            SplashContent(
                onSplashEnd = {
                    // Navigate to product list after delay
                    navController.navigate("product_list") {
                        // Pop up the splash screen from back stack to avoid navigating back to it
                        popUpTo("splash_content") { inclusive = true }
                    }
                }
            )
        }
        composable("product_list") {
            ProductListScreen(onAddClick = {
                navController.navigate("add_screen")
            })
        }

        composable("add_screen") {
            AddScreen(
                navBack = { navController.popBackStack() },
                context = context,
                navSuccess = {
                    navController.navigate("product_list"){
                        popUpTo("add_screen") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
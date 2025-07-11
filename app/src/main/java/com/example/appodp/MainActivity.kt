package com.example.appodp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.appodp.navigation.AppNavigation
import com.example.appodp.ui.theme.AppODPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppODPTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}

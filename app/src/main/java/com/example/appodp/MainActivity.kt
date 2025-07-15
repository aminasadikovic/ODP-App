package com.example.appodp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable // Dodaj ovaj import
import androidx.navigation.compose.rememberNavController
import com.example.appodp.navigation.AppNavigation
import com.example.appodp.ui.theme.AppODPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val initialDarkTheme = isSystemInDarkTheme()
            var isDarkThemeActive by remember { mutableStateOf(initialDarkTheme) }

            val onToggleTheme: () -> Unit = {
                isDarkThemeActive = !isDarkThemeActive
            }

            AppODPTheme(darkTheme = isDarkThemeActive) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}

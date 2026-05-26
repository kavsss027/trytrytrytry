package com.gujaratifitness.app.navigation

import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

class SplashScreenRoute : Screen {
    @Composable
    override fun Content() {
        Text("Splash Screen Placeholder")
    }
}

class LoginScreenRoute : Screen {
    @Composable
    override fun Content() {
        Text("Login Screen Placeholder")
    }
}

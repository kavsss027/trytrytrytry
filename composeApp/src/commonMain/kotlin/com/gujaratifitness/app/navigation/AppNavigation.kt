package com.gujaratifitness.app.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

@Composable
fun AppNavigation() {
    Navigator(SplashScreenRoute()) { navigator ->
        SlideTransition(navigator)
    }
}

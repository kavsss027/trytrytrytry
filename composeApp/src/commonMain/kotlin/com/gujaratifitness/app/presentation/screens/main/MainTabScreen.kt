package com.gujaratifitness.app.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.gujaratifitness.app.core.utils.BackgroundColor
import com.gujaratifitness.app.core.utils.PrimaryColor
import com.gujaratifitness.app.core.utils.SurfaceColor
import com.gujaratifitness.app.core.utils.TextSecondaryColor
import com.gujaratifitness.app.presentation.screens.exercises.ExerciseLibraryTab
import com.gujaratifitness.app.presentation.screens.imbalance.ImbalanceTab
import com.gujaratifitness.app.presentation.screens.workout.WorkoutTab
import com.gujaratifitness.app.presentation.screens.diet.DietTab
import com.gujaratifitness.app.presentation.screens.profile.ProfileTab

class MainTabScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(WorkoutTab) {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = BackgroundColor,
                        tonalElevation = 8.dp
                    ) {
                        TabNavigationItem(WorkoutTab)
                        TabNavigationItem(DietTab)
                        TabNavigationItem(ImbalanceTab)
                        TabNavigationItem(ExerciseLibraryTab)
                        TabNavigationItem(ProfileTab)
                    }
                },
                containerColor = BackgroundColor
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    CurrentTab()
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        val icon = tab.options.icon ?: return
        
        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = { Icon(painter = icon, contentDescription = tab.options.title) },
            label = { Text(tab.options.title) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryColor,
                selectedTextColor = PrimaryColor,
                unselectedIconColor = TextSecondaryColor,
                unselectedTextColor = TextSecondaryColor,
                indicatorColor = SurfaceColor
            )
        )
    }
}

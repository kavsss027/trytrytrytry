package com.gujaratifitness.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.gujaratifitness.app.core.di.appModule
import com.gujaratifitness.app.core.di.dataModule
import com.gujaratifitness.app.core.di.domainModule
import com.gujaratifitness.app.core.utils.AppTheme
import com.gujaratifitness.app.core.utils.BackgroundColor
import com.gujaratifitness.app.core.utils.PrimaryColor
import com.gujaratifitness.app.data.model.UserProfile
import com.gujaratifitness.app.presentation.screens.auth.LoginScreen
import com.gujaratifitness.app.presentation.screens.main.MainTabScreen
import com.gujaratifitness.app.presentation.screens.onboarding.OnboardingScreen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule, dataModule, domainModule)
    }) {
        AppTheme {
            val supabase = koinInject<SupabaseClient>()
            
            var session by remember { mutableStateOf<io.github.jan.supabase.auth.user.UserInfo?>(null) }
            var profile by remember { mutableStateOf<UserProfile?>(null) }
            var isCheckingAuth by remember { mutableStateOf(true) }
            
            // Periodically check session updates and auto-route
            LaunchedEffect(supabase) {
                supabase.auth.sessionStatus.collect { status ->
                    if (status is io.github.jan.supabase.auth.status.SessionStatus.Initializing) {
                        return@collect
                    }
                    
                    val currentSession = if (status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated) {
                        status.session
                    } else null
                    
                    session = currentSession?.user
                    
                    if (currentSession != null) {
                        try {
                            val userId = currentSession.user?.id
                            if (userId != null) {
                                val userProfile = supabase.postgrest.from("users").select {
                                    filter {
                                        eq("id", userId)
                                    }
                                }.decodeList<UserProfile>().firstOrNull()
                                profile = userProfile
                            }
                        } catch (e: Exception) {
                            // Suppress errors during loading
                        }
                    } else {
                        profile = null
                    }
                    isCheckingAuth = false
                }
            }

            if (isCheckingAuth) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                val startScreen = when {
                    session == null -> LoginScreen()
                    profile?.fitness_level == null -> OnboardingScreen()
                    else -> MainTabScreen()
                }

                // Voyager Navigator automatically handles transitions and screen stack
                Navigator(startScreen) { navigator ->
                    // Re-route dynamically on auth changes
                    LaunchedEffect(session, profile) {
                        val currentStart = when {
                            session == null -> LoginScreen()
                            profile?.fitness_level == null -> OnboardingScreen()
                            else -> MainTabScreen()
                        }
                        // Replace root screen to clear backstack on state transitions
                        navigator.replaceAll(currentStart)
                    }
                    SlideTransition(navigator)
                }
            }
        }
    }
}

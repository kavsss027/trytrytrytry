package com.gujaratifitness.app.presentation.screens.onboarding

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.UserProfile
import com.gujaratifitness.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OnboardingState {
    object Idle : OnboardingState
    object Loading : OnboardingState
    object Success : OnboardingState
    data class Error(val message: String) : OnboardingState
}

class OnboardingScreenModel(private val authRepository: AuthRepository) : ScreenModel {

    private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Idle)
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun submitOnboarding(fullName: String, fitnessLevel: String) {
        val user = authRepository.currentSessionUser
        if (user == null) {
            _state.value = OnboardingState.Error("No active session found")
            return
        }

        if (fullName.isBlank()) {
            _state.value = OnboardingState.Error("Full Name is required")
            return
        }

        screenModelScope.launch {
            _state.value = OnboardingState.Loading
            try {
                val profile = UserProfile(
                    id = user.id,
                    email = user.email ?: "",
                    full_name = fullName,
                    fitness_level = fitnessLevel,
                    user_type = "free" // Default to free on onboarding
                )
                authRepository.updateUserProfile(profile)
                _state.value = OnboardingState.Success
            } catch (e: Exception) {
                _state.value = OnboardingState.Error(e.message ?: "Failed to update profile")
            }
        }
    }
}

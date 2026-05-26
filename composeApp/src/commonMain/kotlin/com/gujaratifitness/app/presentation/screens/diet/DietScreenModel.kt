package com.gujaratifitness.app.presentation.screens.diet

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.DietPlan
import com.gujaratifitness.app.data.repository.AuthRepository
import com.gujaratifitness.app.data.repository.DietRequest
import com.gujaratifitness.app.data.repository.FitnessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DietState(
    val activePlan: DietPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DietScreenModel(
    private val fitnessRepository: FitnessRepository,
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(DietState())
    val state: StateFlow<DietState> = _state.asStateFlow()

    init {
        loadActivePlan()
    }

    fun loadActivePlan() {
        val user = authRepository.currentSessionUser
        if (user == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val plan = fitnessRepository.getActiveDietPlan(user.id)
                _state.update { it.copy(activePlan = plan, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load diet plan", isLoading = false) }
            }
        }
    }

    fun generatePlan(
        fitnessLevel: String,
        goal: String,
        age: Int,
        gender: String,
        weightKg: Double,
        heightCm: Double,
        activityLevel: String,
        dietaryPreference: String,
        allergies: String?,
        mealsPerDay: Int,
        gujaratiPreference: Boolean
    ) {
        val user = authRepository.currentSessionUser
        if (user == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val request = DietRequest(
                    fitness_level = fitnessLevel,
                    goal = goal,
                    age = age,
                    gender = gender,
                    weight_kg = weightKg,
                    height_cm = heightCm,
                    activity_level = activityLevel,
                    dietary_preference = dietaryPreference,
                    food_allergies = allergies,
                    meals_per_day = mealsPerDay,
                    gujarati_food_preference = gujaratiPreference
                )
                val newPlan = fitnessRepository.generateDietPlan(request)
                _state.update { it.copy(activePlan = newPlan, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to generate diet plan", isLoading = false) }
            }
        }
    }
}

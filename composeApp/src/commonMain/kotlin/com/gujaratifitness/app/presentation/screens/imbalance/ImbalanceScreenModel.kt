package com.gujaratifitness.app.presentation.screens.imbalance

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.MuscleImbalanceReport
import com.gujaratifitness.app.data.repository.AuthRepository
import com.gujaratifitness.app.data.repository.ImbalanceRequest
import com.gujaratifitness.app.domain.usecases.DetectMuscleImbalanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ImbalanceState(
    val report: MuscleImbalanceReport? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToWorkout: Boolean = false
)

class ImbalanceScreenModel(
    private val detectImbalanceUseCase: DetectMuscleImbalanceUseCase,
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(ImbalanceState())
    val state: StateFlow<ImbalanceState> = _state.asStateFlow()

    init {
        loadLatestReport()
    }

    fun loadLatestReport() {
        val user = authRepository.currentSessionUser ?: return
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val report = detectImbalanceUseCase.getLatestReport(user.id)
                _state.update { it.copy(report = report, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load report", isLoading = false) }
            }
        }
    }

    fun detectImbalance(
        benchPress: Double,
        squat: Double,
        deadlift: Double,
        ohp: Double,
        pullups: Int,
        trainingDays: Map<String, Int>,
        experienceMonths: Int
    ) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val request = ImbalanceRequest(
                    bench_press_max_kg = benchPress,
                    squat_max_kg = squat,
                    deadlift_max_kg = deadlift,
                    overhead_press_max_kg = ohp,
                    pullup_rows_max_reps = pullups,
                    training_days_per_week = trainingDays,
                    training_duration_months = experienceMonths
                )
                val report = detectImbalanceUseCase.execute(request)
                _state.update { it.copy(report = report, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Analysis failed", isLoading = false) }
            }
        }
    }

    /** Called when user taps 'Generate Workout from this imbalance report' */
    fun onGenerateWorkoutFromImbalance() {
        _state.update { it.copy(navigateToWorkout = true) }
    }

    fun clearNavigateToWorkout() {
        _state.update { it.copy(navigateToWorkout = false) }
    }
}

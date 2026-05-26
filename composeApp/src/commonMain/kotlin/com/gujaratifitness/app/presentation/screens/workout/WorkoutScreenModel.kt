package com.gujaratifitness.app.presentation.screens.workout

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.MuscleImbalanceReport
import com.gujaratifitness.app.data.model.WorkoutPlan
import com.gujaratifitness.app.data.repository.AuthRepository
import com.gujaratifitness.app.data.repository.FitnessRepository
import com.gujaratifitness.app.data.repository.WorkoutRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutState(
    val activePlan: WorkoutPlan? = null,
    val latestImbalance: MuscleImbalanceReport? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WorkoutScreenModel(
    private val fitnessRepository: FitnessRepository,
    private val authRepository: AuthRepository
) : ScreenModel {

    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    init {
        loadActivePlanAndImbalance()
    }

    fun loadActivePlanAndImbalance() {
        val user = authRepository.currentSessionUser
        if (user == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val plan = fitnessRepository.getActiveWorkoutPlan(user.id)
                val imbalance = fitnessRepository.getLatestImbalanceReport(user.id)
                _state.update { it.copy(activePlan = plan, latestImbalance = imbalance, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load workout details", isLoading = false) }
            }
        }
    }

    fun generatePlan(
        fitnessLevel: String,
        goal: String,
        daysPerWeek: Int,
        durationMin: Int,
        equipment: List<String>,
        injuries: String?,
        includeImbalanceCorrectives: Boolean
    ) {
        val user = authRepository.currentSessionUser
        if (user == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val imbalanceContext = if (includeImbalanceCorrectives && _state.value.latestImbalance != null) {
                    val reportData = _state.value.latestImbalance!!.report_data
                    "The user has muscle imbalances with an overall balance score of " +
                            "${reportData.overall_balance_score}/100. " +
                            "Specific findings: ${reportData.imbalances.joinToString { "${it.area} is ${it.severity} (${it.finding})" }}. " +
                            "Please prioritize these fixes: ${reportData.priority_fixes.joinToString()}."
                } else null

                val request = WorkoutRequest(
                    fitness_level = fitnessLevel,
                    goal = goal,
                    days_per_week = daysPerWeek,
                    session_duration_minutes = durationMin,
                    available_equipment = equipment,
                    injuries_limitations = injuries,
                    current_lifts = if (_state.value.latestImbalance != null) {
                        val report = _state.value.latestImbalance!!
                        mapOf(
                            "Bench Press" to (report.bench_press_max ?: 0.0),
                            "Squat" to (report.squat_max ?: 0.0),
                            "Deadlift" to (report.deadlift_max ?: 0.0),
                            "Overhead Press" to (report.overhead_press_max ?: 0.0)
                        )
                    } else null,
                    imbalance_context = imbalanceContext
                )

                val newPlan = fitnessRepository.generateWorkoutPlan(request)
                _state.update { it.copy(activePlan = newPlan, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to generate workout plan", isLoading = false) }
            }
        }
    }
}

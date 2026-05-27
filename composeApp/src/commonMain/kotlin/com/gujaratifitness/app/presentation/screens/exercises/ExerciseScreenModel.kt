package com.gujaratifitness.app.presentation.screens.exercises

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.ExerciseDto
import com.gujaratifitness.app.domain.usecases.GetExercisesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseState(
    val exercises: List<ExerciseDto> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val error: String? = null
)

class ExerciseScreenModel(private val getExercisesUseCase: GetExercisesUseCase) : ScreenModel {

    private val _state = MutableStateFlow(ExerciseState())
    val state: StateFlow<ExerciseState> = _state.asStateFlow()

    init {
        loadExercises()
    }

    fun loadExercises(muscleGroup: String? = null) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                var list = getExercisesUseCase.execute(muscleGroup)
                if (list.isEmpty()) {
                    // Nothing cached yet — sync from remote first
                    getExercisesUseCase.sync()
                    list = getExercisesUseCase.execute(muscleGroup)
                }
                _state.update { it.copy(exercises = list, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load exercises", isLoading = false) }
            }
        }
    }

    fun filterExercises(muscleGroup: String?, query: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val baseList = getExercisesUseCase.execute(muscleGroup)
                val filtered = if (query.isBlank()) baseList
                else baseList.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
                }
                _state.update { it.copy(exercises = filtered, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Filter failed", isLoading = false) }
            }
        }
    }

    /** Called by swipe-to-refresh — forces remote sync then reloads */
    fun refresh(muscleGroup: String? = null) {
        screenModelScope.launch {
            _state.update { it.copy(isSyncing = true, error = null) }
            try {
                getExercisesUseCase.sync()
                val list = getExercisesUseCase.execute(muscleGroup)
                _state.update { it.copy(exercises = list, isSyncing = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Refresh failed", isSyncing = false) }
            }
        }
    }
}

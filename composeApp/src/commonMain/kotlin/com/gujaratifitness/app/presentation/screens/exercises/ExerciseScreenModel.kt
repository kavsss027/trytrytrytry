package com.gujaratifitness.app.presentation.screens.exercises

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.ExerciseDto
import com.gujaratifitness.app.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseState(
    val exercises: List<ExerciseDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ExerciseScreenModel(private val exerciseRepository: ExerciseRepository) : ScreenModel {

    private val _state = MutableStateFlow(ExerciseState())
    val state: StateFlow<ExerciseState> = _state.asStateFlow()

    init {
        loadExercises()
    }

    fun loadExercises() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                var list = exerciseRepository.getLocalExercises()
                if (list.isEmpty()) {
                    // Sync from remote
                    exerciseRepository.syncExercises()
                    list = exerciseRepository.getLocalExercises()
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
                val baseList = if (muscleGroup.isNullOrBlank() || muscleGroup == "All") {
                    exerciseRepository.getLocalExercises()
                } else {
                    exerciseRepository.getLocalExercisesByMuscle(muscleGroup.lowercase())
                }

                val filtered = if (query.isBlank()) {
                    baseList
                } else {
                    baseList.filter {
                        it.name.contains(query, ignoreCase = true) || 
                        it.description?.contains(query, ignoreCase = true) == true
                    }
                }
                _state.update { it.copy(exercises = filtered, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Filtering failed", isLoading = false) }
            }
        }
    }

    fun forceSync() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                exerciseRepository.syncExercises()
                val list = exerciseRepository.getLocalExercises()
                _state.update { it.copy(exercises = list, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Sync failed", isLoading = false) }
            }
        }
    }
}

package com.gujaratifitness.app.domain.usecases

import com.gujaratifitness.app.data.model.ExerciseDto
import com.gujaratifitness.app.data.repository.ExerciseRepository

class GetExercisesUseCase(private val repository: ExerciseRepository) {
    suspend fun execute(muscleGroup: String? = null): List<ExerciseDto> {
        return if (muscleGroup.isNullOrBlank() || muscleGroup == "All") {
            repository.getLocalExercises()
        } else {
            repository.getLocalExercisesByMuscle(muscleGroup.lowercase())
        }
    }

    suspend fun sync() = repository.syncExercises()
}

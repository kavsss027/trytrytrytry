package com.gujaratifitness.app.domain.usecases

import com.gujaratifitness.app.data.model.MuscleImbalanceReport
import com.gujaratifitness.app.data.model.WorkoutPlan
import com.gujaratifitness.app.data.repository.FitnessRepository
import com.gujaratifitness.app.data.repository.WorkoutRequest

class GenerateWorkoutPlanUseCase(private val repository: FitnessRepository) {
    suspend fun execute(request: WorkoutRequest): WorkoutPlan {
        return repository.generateWorkoutPlan(request)
    }

    suspend fun getActivePlan(userId: String): WorkoutPlan? {
        return repository.getActiveWorkoutPlan(userId)
    }

    suspend fun getLatestImbalanceReport(userId: String): MuscleImbalanceReport? {
        return repository.getLatestImbalanceReport(userId)
    }
}

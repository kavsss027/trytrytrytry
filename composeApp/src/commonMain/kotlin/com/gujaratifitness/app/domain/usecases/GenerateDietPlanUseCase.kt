package com.gujaratifitness.app.domain.usecases

import com.gujaratifitness.app.data.model.DietPlan
import com.gujaratifitness.app.data.repository.DietRequest
import com.gujaratifitness.app.data.repository.FitnessRepository

class GenerateDietPlanUseCase(private val repository: FitnessRepository) {
    suspend fun execute(request: DietRequest): DietPlan {
        return repository.generateDietPlan(request)
    }

    suspend fun getActivePlan(userId: String): DietPlan? {
        return repository.getActiveDietPlan(userId)
    }
}

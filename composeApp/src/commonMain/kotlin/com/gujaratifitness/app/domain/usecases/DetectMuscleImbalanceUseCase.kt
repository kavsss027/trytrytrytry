package com.gujaratifitness.app.domain.usecases

import com.gujaratifitness.app.data.model.MuscleImbalanceReport
import com.gujaratifitness.app.data.repository.FitnessRepository
import com.gujaratifitness.app.data.repository.ImbalanceRequest

class DetectMuscleImbalanceUseCase(private val repository: FitnessRepository) {
    suspend fun execute(request: ImbalanceRequest): MuscleImbalanceReport {
        return repository.detectMuscleImbalance(request)
    }

    suspend fun getLatestReport(userId: String): MuscleImbalanceReport? {
        return repository.getLatestImbalanceReport(userId)
    }
}

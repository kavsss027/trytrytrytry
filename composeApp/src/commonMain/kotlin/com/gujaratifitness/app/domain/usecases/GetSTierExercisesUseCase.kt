package com.gujaratifitness.app.domain.usecases

import com.gujaratifitness.app.data.model.ExerciseDto
import com.gujaratifitness.app.data.model.UserProfile
import com.gujaratifitness.app.data.repository.FitnessRepository

class GetSTierExercisesUseCase(private val repository: FitnessRepository) {
    
    /** Returns null if user is not premium/influencer/owner — caller should show lock screen */
    suspend fun execute(userProfile: UserProfile): List<ExerciseDto>? {
        val isPremium = userProfile.user_type in listOf("premium", "influencer", "owner")
        if (!isPremium) return null
        return repository.getSStierExercises()
    }

    fun isPremium(userProfile: UserProfile): Boolean {
        return userProfile.user_type in listOf("premium", "influencer", "owner")
    }
}

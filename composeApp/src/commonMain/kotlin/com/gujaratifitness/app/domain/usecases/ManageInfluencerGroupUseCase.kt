package com.gujaratifitness.app.domain.usecases

import com.gujaratifitness.app.data.model.InfluencerJoinRequest
import com.gujaratifitness.app.data.model.InfluencerProfile
import com.gujaratifitness.app.data.repository.FitnessRepository

class ManageInfluencerGroupUseCase(private val repository: FitnessRepository) {

    suspend fun getInfluencers(): List<InfluencerProfile> {
        return repository.getInfluencers()
    }

    suspend fun getJoinRequests(influencerId: String): List<InfluencerJoinRequest> {
        return repository.getInfluencerJoinRequests(influencerId)
    }

    suspend fun submitJoinRequest(influencerId: String, userId: String) {
        repository.submitJoinRequest(influencerId, userId)
    }

    suspend fun approveRequest(requestId: String) {
        repository.respondToJoinRequest(requestId, "approved")
    }

    suspend fun rejectRequest(requestId: String) {
        repository.respondToJoinRequest(requestId, "rejected")
    }
}

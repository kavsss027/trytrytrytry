package com.gujaratifitness.app.presentation.screens.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gujaratifitness.app.data.model.InfluencerJoinRequest
import com.gujaratifitness.app.data.model.UserProfile
import com.gujaratifitness.app.data.repository.AuthRepository
import com.gujaratifitness.app.domain.usecases.ManageInfluencerGroupUseCase
import com.gujaratifitness.app.domain.usecases.GetSTierExercisesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val profile: UserProfile? = null,
    val joinRequests: List<InfluencerJoinRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileScreenModel(
    private val authRepository: AuthRepository,
    private val influencerUseCase: ManageInfluencerGroupUseCase,
    private val sTierUseCase: GetSTierExercisesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val user = authRepository.currentSessionUser
        if (user == null) {
            _state.update { it.copy(error = "User not logged in") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val userProfile = authRepository.getUserProfile(user.id)
                _state.update { it.copy(profile = userProfile, isLoading = false) }

                if (userProfile.user_type == "influencer" || userProfile.user_type == "owner") {
                    loadJoinRequests(user.id)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load profile", isLoading = false) }
            }
        }
    }

    private fun loadJoinRequests(influencerUserId: String) {
        screenModelScope.launch {
            try {
                val influencers = influencerUseCase.getInfluencers()
                val activeInfluencer = influencers.firstOrNull { it.user_id == influencerUserId }
                if (activeInfluencer != null) {
                    val requests = influencerUseCase.getJoinRequests(activeInfluencer.id)
                    _state.update { it.copy(joinRequests = requests) }
                }
            } catch (_: Exception) {
                // Non-critical — don't show error for this
            }
        }
    }

    fun respondToJoinRequest(requestId: String, approve: Boolean) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                if (approve) {
                    influencerUseCase.approveRequest(requestId)
                } else {
                    influencerUseCase.rejectRequest(requestId)
                }
                val user = authRepository.currentSessionUser
                if (user != null) loadJoinRequests(user.id)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to respond to request", isLoading = false) }
            }
        }
    }

    fun togglePremium(currentProfile: UserProfile) {
        val newType = if (currentProfile.user_type == "free") "premium" else "free"
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val updatedProfile = authRepository.updateUserProfile(
                    currentProfile.copy(user_type = newType)
                )
                _state.update { it.copy(profile = updatedProfile, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to update status", isLoading = false) }
            }
        }
    }

    fun signOut() {
        screenModelScope.launch {
            try {
                authRepository.signOut()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to sign out") }
            }
        }
    }
}

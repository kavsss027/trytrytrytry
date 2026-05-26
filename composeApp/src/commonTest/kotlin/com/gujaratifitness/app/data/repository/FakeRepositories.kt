package com.gujaratifitness.app.data.repository

import com.gujaratifitness.app.data.model.*
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class FakeAuthRepository : AuthRepository {
    private val _sessionFlow = MutableStateFlow<UserInfo?>(null)
    override val sessionFlow: Flow<UserInfo?> = _sessionFlow.asStateFlow()
    override var currentSessionUser: UserInfo? = null

    var signUpCalled = false
    var signUpEmail = ""
    var signUpFullName = ""
    var signInCalled = false
    var signInEmail = ""
    var signOutCalled = false
    
    var userProfileResult: UserProfile? = null
    var lastUpdatedProfile: UserProfile? = null

    private fun createMockUserInfo(email: String): UserInfo {
        val jsonStr = """
            {
                "id": "mock-uuid-1234",
                "aud": "authenticated",
                "role": "authenticated",
                "email": "$email",
                "email_confirmed_at": "2026-05-27T00:00:00Z",
                "phone": "",
                "confirmed_at": "2026-05-27T00:00:00Z",
                "last_sign_in_at": "2026-05-27T00:00:00Z",
                "app_metadata": {},
                "user_metadata": { "full_name": "Gujarati Fitness Fan" },
                "identities": [],
                "created_at": "2026-05-27T00:00:00Z",
                "updated_at": "2026-05-27T00:00:00Z"
            }
        """.trimIndent()
        return Json.decodeFromString<UserInfo>(jsonStr)
    }

    override suspend fun signUp(email: String, password: String, fullName: String) {
        signUpCalled = true
        signUpEmail = email
        signUpFullName = fullName
    }

    override suspend fun signIn(email: String, password: String) {
        signInCalled = true
        signInEmail = email
        val user = createMockUserInfo(email)
        currentSessionUser = user
        _sessionFlow.value = user
    }

    /** Non-suspend helper for use in test setup where coroutine context is not available */
    fun signInForTest(email: String = "user@domain.com") {
        signInCalled = true
        signInEmail = email
        val user = createMockUserInfo(email)
        currentSessionUser = user
        _sessionFlow.value = user
    }

    override suspend fun signOut() {
        signOutCalled = true
        currentSessionUser = null
        _sessionFlow.value = null
    }

    override suspend fun getUserProfile(userId: String): UserProfile {
        return userProfileResult ?: UserProfile(
            id = userId,
            email = "test@example.com",
            full_name = "Test User",
            user_type = "free",
            fitness_level = "Beginner",
            influencer_id = null,
            created_at = kotlinx.datetime.Clock.System.now().toString(),
            updated_at = null
        )
    }

    override suspend fun updateUserProfile(profile: UserProfile): UserProfile {
        lastUpdatedProfile = profile
        return profile
    }
}

class FakeFitnessRepository : FitnessRepository {
    var generatedWorkoutPlan: WorkoutPlan? = null
    var generatedDietPlan: DietPlan? = null
    var detectedImbalanceReport: MuscleImbalanceReport? = null
    
    var workoutRequestSent: WorkoutRequest? = null
    var dietRequestSent: DietRequest? = null
    var imbalanceRequestSent: ImbalanceRequest? = null
    
    var activeWorkoutPlan: WorkoutPlan? = null
    var activeDietPlan: DietPlan? = null
    var latestImbalanceReport: MuscleImbalanceReport? = null
    
    var joinRequestInfluencerId: String? = null
    var joinRequestUserId: String? = null
    var joinResponseRequestId: String? = null
    var joinResponseStatus: String? = null

    override suspend fun generateWorkoutPlan(request: WorkoutRequest): WorkoutPlan {
        workoutRequestSent = request
        return generatedWorkoutPlan ?: WorkoutPlan(
            id = "w-1",
            user_id = "u-1",
            plan_data = WorkoutPlanData(
                title = "વર્કઆઉટ પ્લાન (Workout Plan)",
                duration_weeks = 4,
                days = emptyList(),
                general_notes = "Goal: ${request.goal}",
                nutrition_reminder = "Drink water"
            ),
            imbalance_used = request.imbalance_context != null,
            is_active = true,
            created_at = "2026-05-27T00:00:00Z"
        )
    }

    override suspend fun generateDietPlan(request: DietRequest): DietPlan {
        dietRequestSent = request
        return generatedDietPlan ?: DietPlan(
            id = "d-1",
            user_id = "u-1",
            plan_data = DietPlanData(
                title = "આહાર યોજના (Diet Plan)",
                daily_calories = 2000,
                macros = DietMacros(protein_g = 120, carbs_g = 200, fat_g = 60),
                meals = emptyList(),
                hydration_litres = 3.0,
                notes = "Gujarati Preference: ${request.gujarati_food_preference}"
            ),
            is_active = true,
            created_at = "2026-05-27T00:00:00Z"
        )
    }

    override suspend fun detectMuscleImbalance(request: ImbalanceRequest): MuscleImbalanceReport {
        imbalanceRequestSent = request
        return detectedImbalanceReport ?: MuscleImbalanceReport(
            id = "i-1",
            user_id = "u-1",
            bench_press_max = request.bench_press_max_kg,
            squat_max = request.squat_max_kg,
            deadlift_max = request.deadlift_max_kg,
            overhead_press_max = request.overhead_press_max_kg,
            pullup_rows_max = request.pullup_rows_max_reps,
            training_days = request.training_days_per_week,
            training_duration = "${request.training_duration_months} months",
            report_data = ImbalanceReportData(
                overall_balance_score = 80,
                imbalances = emptyList(),
                strengths = emptyList(),
                priority_fixes = listOf("Face Pulls")
            ),
            created_at = "2026-05-27T00:00:00Z"
        )
    }

    override suspend fun getActiveWorkoutPlan(userId: String): WorkoutPlan? = activeWorkoutPlan

    override suspend fun getActiveDietPlan(userId: String): DietPlan? = activeDietPlan

    override suspend fun getLatestImbalanceReport(userId: String): MuscleImbalanceReport? = latestImbalanceReport

    override suspend fun getInfluencers(): List<InfluencerProfile> = emptyList()

    override suspend fun getSStierExercises(): List<ExerciseDto> = emptyList()

    override suspend fun getInfluencerJoinRequests(influencerId: String): List<InfluencerJoinRequest> = emptyList()

    override suspend fun submitJoinRequest(influencerId: String, userId: String) {
        joinRequestInfluencerId = influencerId
        joinRequestUserId = userId
    }

    override suspend fun respondToJoinRequest(requestId: String, status: String) {
        joinResponseRequestId = requestId
        joinResponseStatus = status
    }
}

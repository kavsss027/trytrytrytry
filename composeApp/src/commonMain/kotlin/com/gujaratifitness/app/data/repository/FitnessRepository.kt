package com.gujaratifitness.app.data.repository

import com.gujaratifitness.app.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRequest(
    val fitness_level: String,
    val goal: String,
    val days_per_week: Int,
    val session_duration_minutes: Int,
    val available_equipment: List<String>,
    val injuries_limitations: String?,
    val current_lifts: Map<String, Double>?,
    val imbalance_context: String?
)

@Serializable
data class DietRequest(
    val fitness_level: String,
    val goal: String,
    val age: Int,
    val gender: String,
    val weight_kg: Double,
    val height_cm: Double,
    val activity_level: String,
    val dietary_preference: String,
    val food_allergies: String?,
    val meals_per_day: Int,
    val gujarati_food_preference: Boolean
)

@Serializable
data class ImbalanceRequest(
    val bench_press_max_kg: Double,
    val squat_max_kg: Double,
    val deadlift_max_kg: Double,
    val overhead_press_max_kg: Double,
    val pullup_rows_max_reps: Int,
    val training_days_per_week: Map<String, Int>,
    val training_duration_months: Int
)

@Serializable
data class WorkoutResponse(val plan: WorkoutPlan)

@Serializable
data class DietResponse(val plan: DietPlan)

@Serializable
data class ImbalanceResponse(val report: MuscleImbalanceReport)

class FitnessRepository(private val supabase: SupabaseClient) {

    suspend fun generateWorkoutPlan(request: WorkoutRequest): WorkoutPlan = withContext(Dispatchers.IO) {
        val response = supabase.functions.invoke("generate-workout-plan", request)
        response.body<WorkoutResponse>().plan
    }

    suspend fun generateDietPlan(request: DietRequest): DietPlan = withContext(Dispatchers.IO) {
        val response = supabase.functions.invoke("generate-diet-plan", request)
        response.body<DietResponse>().plan
    }

    suspend fun detectMuscleImbalance(request: ImbalanceRequest): MuscleImbalanceReport = withContext(Dispatchers.IO) {
        val response = supabase.functions.invoke("detect-muscle-imbalance", request)
        response.body<ImbalanceResponse>().report
    }

    suspend fun getActiveWorkoutPlan(userId: String): WorkoutPlan? = withContext(Dispatchers.IO) {
        supabase.postgrest.from("workout_plans").select {
            filter {
                eq("user_id", userId)
                eq("is_active", true)
            }
        }.decodeList<WorkoutPlan>().firstOrNull()
    }

    suspend fun getActiveDietPlan(userId: String): DietPlan? = withContext(Dispatchers.IO) {
        supabase.postgrest.from("diet_plans").select {
            filter {
                eq("user_id", userId)
                eq("is_active", true)
            }
        }.decodeList<DietPlan>().firstOrNull()
    }

    suspend fun getLatestImbalanceReport(userId: String): MuscleImbalanceReport? = withContext(Dispatchers.IO) {
        supabase.postgrest.from("muscle_imbalance_reports").select {
            filter {
                eq("user_id", userId)
            }
            order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit(1)
        }.decodeList<MuscleImbalanceReport>().firstOrNull()
    }

    suspend fun getInfluencers(): List<InfluencerProfile> = withContext(Dispatchers.IO) {
        supabase.postgrest.from("influencers").select {
            filter {
                eq("is_active", true)
            }
        }.decodeList<InfluencerProfile>()
    }

    suspend fun getSStierExercises(): List<ExerciseDto> = withContext(Dispatchers.IO) {
        supabase.postgrest.from("stier_exercises").select {
            filter {
                eq("is_active", true)
            }
        }.decodeList<ExerciseDto>()
    }

    suspend fun getInfluencerJoinRequests(influencerId: String): List<InfluencerJoinRequest> = withContext(Dispatchers.IO) {
        supabase.postgrest.from("influencer_join_requests").select {
            filter {
                eq("influencer_id", influencerId)
            }
        }.decodeList<InfluencerJoinRequest>()
    }

    @Serializable
    data class JoinRequestInsert(
        val user_id: String,
        val influencer_id: String
    )

    suspend fun submitJoinRequest(influencerId: String, userId: String): Unit = withContext(Dispatchers.IO) {
        supabase.postgrest.from("influencer_join_requests").insert(JoinRequestInsert(userId, influencerId))
    }

    suspend fun respondToJoinRequest(requestId: String, status: String): Unit = withContext(Dispatchers.IO) {
        // Status can be 'approved' or 'rejected'
        val requestMap = mapOf(
            "status" to status,
            "responded_at" to kotlinx.datetime.Clock.System.now().toString()
        )
        supabase.postgrest.from("influencer_join_requests").update(requestMap) {
            filter {
                eq("id", requestId)
            }
        }
    }
}

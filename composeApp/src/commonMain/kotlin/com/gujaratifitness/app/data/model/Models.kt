package com.gujaratifitness.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val full_name: String? = null,
    val user_type: String = "free",
    val fitness_level: String? = null,
    val influencer_id: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class InfluencerProfile(
    val id: String,
    val user_id: String,
    val display_name: String,
    val max_slots: Int = 100,
    val used_slots: Int = 0,
    val is_active: Boolean = true
)

@Serializable
data class ExerciseDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val muscle_group: String,
    val gif_url: String,
    val difficulty: String = "intermediate",
    val is_active: Boolean = true
)

@Serializable
data class InfluencerJoinRequest(
    val id: String,
    val user_id: String,
    val influencer_id: String,
    val status: String = "pending",
    val requested_at: String? = null,
    val responded_at: String? = null,
    val user_email: String? = null, // Custom extension if we query user email
    val user_name: String? = null  // Custom extension if we query user name
)

// Workout Plan Structures
@Serializable
data class WorkoutExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val rest_seconds: Int,
    val notes: String? = null
)

@Serializable
data class WorkoutDay(
    val day: String,
    val focus: String,
    val exercises: List<WorkoutExercise> = emptyList()
)

@Serializable
data class WorkoutPlanData(
    val title: String,
    val duration_weeks: Int,
    val days: List<WorkoutDay> = emptyList(),
    val general_notes: String? = null,
    val nutrition_reminder: String? = null
)

@Serializable
data class WorkoutPlan(
    val id: String,
    val user_id: String,
    val plan_data: WorkoutPlanData,
    val imbalance_used: Boolean = false,
    val is_active: Boolean = true,
    val created_at: String? = null
)

// Diet Plan Structures
@Serializable
data class DietMacros(
    val protein_g: Int,
    val carbs_g: Int,
    val fat_g: Int
)

@Serializable
data class DietMeal(
    val meal: String,
    val time: String,
    val foods: List<String> = emptyList(),
    val calories: Int
)

@Serializable
data class DietPlanData(
    val title: String,
    val daily_calories: Int,
    val macros: DietMacros,
    val meals: List<DietMeal> = emptyList(),
    val hydration_litres: Double,
    val notes: String? = null
)

@Serializable
data class DietPlan(
    val id: String,
    val user_id: String,
    val plan_data: DietPlanData,
    val is_active: Boolean = true,
    val created_at: String? = null
)

// Muscle Imbalance Structures
@Serializable
data class ImbalanceItem(
    val area: String,
    val severity: String,
    val finding: String,
    val recommendation: String
)

@Serializable
data class ImbalanceReportData(
    val overall_balance_score: Int,
    val imbalances: List<ImbalanceItem> = emptyList(),
    val strengths: List<String> = emptyList(),
    val priority_fixes: List<String> = emptyList()
)

@Serializable
data class MuscleImbalanceReport(
    val id: String,
    val user_id: String,
    val bench_press_max: Double? = null,
    val squat_max: Double? = null,
    val deadlift_max: Double? = null,
    val overhead_press_max: Double? = null,
    val pullup_rows_max: Int? = null,
    val training_days: Map<String, Int> = emptyMap(),
    val training_duration: String? = null,
    val report_data: ImbalanceReportData,
    val created_at: String? = null
)

// API Error Structure
@Serializable
data class SupabaseError(
    val error: String,
    val message: String? = null
)

# DATA_MODELS.md — GUJARATI FITNESS APP
> Every Kotlin data class used in this project.
> All models are @Serializable — required for Ktor and supabase-kt.
> Database column names use snake_case. Kotlin property names use camelCase.
> The @SerialName annotation maps between the two.

---

## USER MODELS

```kotlin
@Serializable
data class User(
    val id: String,
    val email: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("user_type") val userType: UserType = UserType.FREE,
    @SerialName("fitness_level") val fitnessLevel: FitnessLevel? = null,
    @SerialName("influencer_id") val influencerId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
enum class UserType {
    @SerialName("free") FREE,
    @SerialName("premium") PREMIUM,
    @SerialName("influencer") INFLUENCER,
    @SerialName("owner") OWNER
}

@Serializable
enum class FitnessLevel {
    @SerialName("beginner") BEGINNER,
    @SerialName("intermediate") INTERMEDIATE
}
```

---

## EXERCISE MODELS

```kotlin
@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("muscle_group") val muscleGroup: MuscleGroup,
    @SerialName("gif_url") val gifUrl: String,
    val difficulty: Difficulty = Difficulty.INTERMEDIATE,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
enum class MuscleGroup {
    @SerialName("chest") CHEST,
    @SerialName("back") BACK,
    @SerialName("shoulders") SHOULDERS,
    @SerialName("biceps") BICEPS,
    @SerialName("triceps") TRICEPS,
    @SerialName("legs") LEGS,
    @SerialName("glutes") GLUTES,
    @SerialName("core") CORE,
    @SerialName("full_body") FULL_BODY
}

@Serializable
enum class Difficulty {
    @SerialName("beginner") BEGINNER,
    @SerialName("intermediate") INTERMEDIATE,
    @SerialName("advanced") ADVANCED
}

@Serializable
data class STierExercise(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("gif_url") val gifUrl: String,
    @SerialName("muscle_group") val muscleGroup: MuscleGroup,
    @SerialName("influencer_id") val influencerId: String? = null,
    @SerialName("is_global") val isGlobal: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true
)
```

---

## WORKOUT PLAN MODELS

```kotlin
@Serializable
data class WorkoutPlan(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("plan_data") val planData: WorkoutPlanData,
    @SerialName("imbalance_used") val imbalanceUsed: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class WorkoutPlanData(
    val title: String,
    @SerialName("duration_weeks") val durationWeeks: Int,
    val days: List<WorkoutDay>,
    @SerialName("general_notes") val generalNotes: String? = null,
    @SerialName("nutrition_reminder") val nutritionReminder: String? = null
)

@Serializable
data class WorkoutDay(
    val day: String,
    val focus: String,
    val exercises: List<PlannedExercise>
)

@Serializable
data class PlannedExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    @SerialName("rest_seconds") val restSeconds: Int,
    val notes: String? = null
)

@Serializable
data class WorkoutQuestionnaire(
    @SerialName("fitness_level") val fitnessLevel: String,
    val goal: String,
    @SerialName("days_per_week") val daysPerWeek: Int,
    @SerialName("session_duration_minutes") val sessionDurationMinutes: Int,
    @SerialName("available_equipment") val availableEquipment: List<String>,
    @SerialName("injuries_limitations") val injuriesLimitations: String? = null,
    @SerialName("current_lifts") val currentLifts: CurrentLifts? = null,
    @SerialName("imbalance_context") val imbalanceContext: ImbalanceContext? = null
)

@Serializable
data class CurrentLifts(
    @SerialName("bench_press") val benchPress: Double? = null,
    val squat: Double? = null,
    val deadlift: Double? = null,
    @SerialName("overhead_press") val overheadPress: Double? = null
)
```

---

## DIET PLAN MODELS

```kotlin
@Serializable
data class DietPlan(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("plan_data") val planData: DietPlanData,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class DietPlanData(
    val title: String,
    @SerialName("daily_calories") val dailyCalories: Int,
    val macros: Macros,
    val meals: List<Meal>,
    @SerialName("hydration_litres") val hydrationLitres: Double,
    val notes: String? = null
)

@Serializable
data class Macros(
    @SerialName("protein_g") val proteinG: Int,
    @SerialName("carbs_g") val carbsG: Int,
    @SerialName("fat_g") val fatG: Int
)

@Serializable
data class Meal(
    val meal: String,
    val time: String,
    val foods: List<String>,
    val calories: Int
)

@Serializable
data class DietQuestionnaire(
    @SerialName("fitness_level") val fitnessLevel: String,
    val goal: String,
    val age: Int,
    val gender: String,
    @SerialName("weight_kg") val weightKg: Double,
    @SerialName("height_cm") val heightCm: Double,
    @SerialName("activity_level") val activityLevel: String,
    @SerialName("dietary_preference") val dietaryPreference: String,
    @SerialName("food_allergies") val foodAllergies: String? = null,
    @SerialName("meals_per_day") val mealsPerDay: Int,
    @SerialName("gujarati_food_preference") val gujaratiFoodPreference: Boolean = true
)
```

---

## MUSCLE IMBALANCE MODELS

```kotlin
@Serializable
data class MuscleImbalanceReport(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("bench_press_max") val benchPressMax: Double? = null,
    @SerialName("squat_max") val squatMax: Double? = null,
    @SerialName("deadlift_max") val deadliftMax: Double? = null,
    @SerialName("overhead_press_max") val overheadPressMax: Double? = null,
    @SerialName("pullup_rows_max") val pullupRowsMax: Int? = null,
    @SerialName("training_days") val trainingDays: Map<String, Int>? = null,
    @SerialName("training_duration") val trainingDuration: String? = null,
    @SerialName("report_data") val reportData: ImbalanceReportData,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ImbalanceReportData(
    @SerialName("overall_balance_score") val overallBalanceScore: Int,
    val imbalances: List<ImbalanceItem>,
    val strengths: List<String>,
    @SerialName("priority_fixes") val priorityFixes: List<String>
)

@Serializable
data class ImbalanceItem(
    val area: String,
    val severity: String,
    val finding: String,
    val recommendation: String
)

@Serializable
data class ImbalanceContext(
    @SerialName("priority_fixes") val priorityFixes: List<String>,
    @SerialName("overall_balance_score") val overallBalanceScore: Int
)
```

---

## INFLUENCER MODELS

```kotlin
@Serializable
data class Influencer(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("max_slots") val maxSlots: Int,
    @SerialName("used_slots") val usedSlots: Int,
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class JoinRequest(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("influencer_id") val influencerId: String,
    val status: JoinRequestStatus = JoinRequestStatus.PENDING,
    @SerialName("requested_at") val requestedAt: String? = null,
    @SerialName("responded_at") val respondedAt: String? = null
)

@Serializable
enum class JoinRequestStatus {
    @SerialName("pending") PENDING,
    @SerialName("approved") APPROVED,
    @SerialName("rejected") REJECTED
}
```

---

## GENERATION LIMIT MODELS

```kotlin
@Serializable
data class GenerationLimitResponse(
    @SerialName("can_generate") val canGenerate: Boolean,
    val used: Int,
    val limit: Int,
    val remaining: Int,
    @SerialName("reset_date") val resetDate: String? = null
)

@Serializable
enum class GenerationFeature {
    @SerialName("workout_plan") WORKOUT_PLAN,
    @SerialName("diet_plan") DIET_PLAN
}
```

---

## UI STATE MODELS (Not Serializable — App Only)

```kotlin
// One per screen — pattern must be followed exactly
sealed class ExerciseUiState {
    object Idle : ExerciseUiState()
    object Loading : ExerciseUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}

sealed class WorkoutUiState {
    object Idle : WorkoutUiState()
    object Loading : WorkoutUiState()
    data class Success(val plan: WorkoutPlan) : WorkoutUiState()
    data class Error(val message: String) : WorkoutUiState()
}

sealed class ImbalanceUiState {
    object Idle : ImbalanceUiState()
    object Loading : ImbalanceUiState()
    data class Success(val report: MuscleImbalanceReport) : ImbalanceUiState()
    data class Error(val message: String) : ImbalanceUiState()
}
```

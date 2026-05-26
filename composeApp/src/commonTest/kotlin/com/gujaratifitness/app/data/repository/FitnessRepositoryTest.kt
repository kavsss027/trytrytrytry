package com.gujaratifitness.app.data.repository

import com.gujaratifitness.app.data.model.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FitnessRepositoryTest {

    @Test
    fun testGenerateWorkoutPlanAssemblesCorrectRequest() = runTest {
        val fakeRepo = FakeFitnessRepository()
        
        val request = WorkoutRequest(
            fitness_level = "Advanced",
            goal = "Powerlifting",
            days_per_week = 4,
            session_duration_minutes = 75,
            available_equipment = listOf("Barbell", "Dumbbell", "Rack"),
            injuries_limitations = "Slight lower back soreness",
            current_lifts = mapOf("Bench" to 100.0, "Squat" to 140.0),
            imbalance_context = "Left-right imbalance in quads"
        )
        
        val expectedPlan = WorkoutPlan(
            id = "w-999",
            user_id = "user-1",
            plan_data = WorkoutPlanData(
                title = "Advanced Powerlifting Plan",
                duration_weeks = 4,
                days = emptyList(),
                general_notes = "Avoid heavy axial loading due to: Slight lower back soreness",
                nutrition_reminder = "Ensure calorie surplus"
            ),
            imbalance_used = true,
            is_active = true,
            created_at = "2026-05-27"
        )
        fakeRepo.generatedWorkoutPlan = expectedPlan
        
        val plan = fakeRepo.generateWorkoutPlan(request)
        
        assertEquals(expectedPlan, plan)
        assertNotNull(fakeRepo.workoutRequestSent)
        assertEquals("Advanced", fakeRepo.workoutRequestSent?.fitness_level)
        assertEquals("Powerlifting", fakeRepo.workoutRequestSent?.goal)
        assertEquals(4, fakeRepo.workoutRequestSent?.days_per_week)
        assertEquals("Slight lower back soreness", fakeRepo.workoutRequestSent?.injuries_limitations)
        assertEquals(100.0, fakeRepo.workoutRequestSent?.current_lifts?.get("Bench"))
    }

    @Test
    fun testGenerateDietPlanAssemblesCorrectRequest() = runTest {
        val fakeRepo = FakeFitnessRepository()
        
        val request = DietRequest(
            fitness_level = "Beginner",
            goal = "Weight Loss",
            age = 28,
            gender = "Female",
            weight_kg = 65.0,
            height_cm = 160.0,
            activity_level = "Light",
            dietary_preference = "Vegetarian",
            food_allergies = "Peanuts",
            meals_per_day = 3,
            gujarati_food_preference = true
        )
        
        val expectedDiet = DietPlan(
            id = "d-999",
            user_id = "user-1",
            plan_data = DietPlanData(
                title = "શાકાહારી આહાર યોજના (Veg Diet)",
                daily_calories = 1600,
                macros = DietMacros(protein_g = 80, carbs_g = 200, fat_g = 53),
                meals = emptyList(),
                hydration_litres = 2.5,
                notes = "Allergies: Peanuts. Gujarati style."
            ),
            is_active = true,
            created_at = "2026-05-27"
        )
        fakeRepo.generatedDietPlan = expectedDiet
        
        val plan = fakeRepo.generateDietPlan(request)
        
        assertEquals(expectedDiet, plan)
        assertNotNull(fakeRepo.dietRequestSent)
        assertEquals("Beginner", fakeRepo.dietRequestSent?.fitness_level)
        assertEquals("Weight Loss", fakeRepo.dietRequestSent?.goal)
        assertTrue(fakeRepo.dietRequestSent?.gujarati_food_preference ?: false)
        assertEquals("Peanuts", fakeRepo.dietRequestSent?.food_allergies)
    }

    @Test
    fun testDetectMuscleImbalanceAssemblesCorrectRequest() = runTest {
        val fakeRepo = FakeFitnessRepository()
        
        val request = ImbalanceRequest(
            bench_press_max_kg = 80.0,
            squat_max_kg = 100.0,
            deadlift_max_kg = 120.0,
            overhead_press_max_kg = 50.0,
            pullup_rows_max_reps = 8,
            training_days_per_week = mapOf("strength" to 3),
            training_duration_months = 12
        )
        
        val expectedReport = MuscleImbalanceReport(
            id = "i-999",
            user_id = "user-1",
            bench_press_max = 80.0,
            squat_max = 100.0,
            deadlift_max = 120.0,
            overhead_press_max = 50.0,
            pullup_rows_max = 8,
            training_days = mapOf("strength" to 3),
            training_duration = "12 months",
            report_data = ImbalanceReportData(
                overall_balance_score = 82,
                imbalances = listOf(
                    ImbalanceItem(area = "Posterior Chain", severity = "Mild", finding = "Squat to Deadlift ratio is slightly high", recommendation = "Focus on Romanian Deadlifts")
                ),
                strengths = listOf("Bench Press ratio is solid"),
                priority_fixes = listOf("Romanian Deadlifts", "Face Pulls")
            ),
            created_at = "2026-05-27"
        )
        fakeRepo.detectedImbalanceReport = expectedReport
        
        val report = fakeRepo.detectMuscleImbalance(request)
        
        assertEquals(expectedReport, report)
        assertNotNull(fakeRepo.imbalanceRequestSent)
        assertEquals(80.0, fakeRepo.imbalanceRequestSent?.bench_press_max_kg)
        assertEquals(8, fakeRepo.imbalanceRequestSent?.pullup_rows_max_reps)
        assertEquals(3, fakeRepo.imbalanceRequestSent?.training_days_per_week?.get("strength"))
    }
}

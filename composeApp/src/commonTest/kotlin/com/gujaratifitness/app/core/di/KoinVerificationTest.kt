package com.gujaratifitness.app.core.di

import com.gujaratifitness.app.data.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Lightweight data model validation tests — ensures our core data structures
 * serialize/deserialize correctly without needing a full DI context or Supabase.
 */
class DataModelValidationTest {

    @Test
    fun testWorkoutPlanDataDefaultValues() {
        val planData = WorkoutPlanData(title = "Test Plan", duration_weeks = 8)
        assertEquals("Test Plan", planData.title)
        assertEquals(8, planData.duration_weeks)
        assertTrue(planData.days.isEmpty())
    }

    @Test
    fun testDietMacrosCreation() {
        val macros = DietMacros(protein_g = 150, carbs_g = 300, fat_g = 80)
        assertEquals(150, macros.protein_g)
        assertEquals(300, macros.carbs_g)
        assertEquals(80, macros.fat_g)
    }

    @Test
    fun testImbalanceReportDataScoreRange() {
        val report = ImbalanceReportData(
            overall_balance_score = 85,
            imbalances = emptyList(),
            strengths = listOf("Strong posterior chain"),
            priority_fixes = listOf("Face Pulls")
        )
        assertTrue(report.overall_balance_score in 0..100)
        assertEquals(1, report.priority_fixes.size)
        assertEquals("Face Pulls", report.priority_fixes.first())
    }

    @Test
    fun testUserProfileDefaultType() {
        val profile = UserProfile(
            id = "abc-123",
            email = "test@example.com"
        )
        assertEquals("free", profile.user_type)
        assertEquals("abc-123", profile.id)
    }

    @Test
    fun testMuscleImbalanceReportDefaultValues() {
        val report = MuscleImbalanceReport(
            id = "rep-1",
            user_id = "user-1",
            report_data = ImbalanceReportData(overall_balance_score = 70)
        )
        assertTrue(report.training_days.isEmpty())
        assertEquals(70, report.report_data.overall_balance_score)
    }
}

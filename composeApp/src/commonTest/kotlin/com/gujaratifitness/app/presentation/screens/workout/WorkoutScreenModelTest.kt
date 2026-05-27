package com.gujaratifitness.app.presentation.screens.workout

import com.gujaratifitness.app.data.model.*
import com.gujaratifitness.app.data.repository.FakeAuthRepository
import com.gujaratifitness.app.data.repository.FakeFitnessRepository
import com.gujaratifitness.app.domain.usecases.GenerateWorkoutPlanUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutScreenModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitializationWithoutUserSetsError() = runTest {
        val authRepo = FakeAuthRepository()
        val fitnessRepo = FakeFitnessRepository()
        val useCase = GenerateWorkoutPlanUseCase(fitnessRepo)

        assertNull(authRepo.currentSessionUser)

        val screenModel = WorkoutScreenModel(useCase, authRepo)

        val state = screenModel.state.value
        assertEquals("User not logged in", state.error)
        assertFalse(state.isLoading)
        assertNull(state.activePlan)
    }

    @Test
    fun testGeneratePlanWithNoUserSetsError() = runTest {
        val authRepo = FakeAuthRepository()
        val fitnessRepo = FakeFitnessRepository()
        val useCase = GenerateWorkoutPlanUseCase(fitnessRepo)

        val screenModel = WorkoutScreenModel(useCase, authRepo)

        screenModel.generatePlan(
            fitnessLevel = "Intermediate",
            goal = "Hypertrophy",
            daysPerWeek = 3,
            durationMin = 60,
            equipment = listOf("Dumbbell"),
            injuries = null,
            includeImbalanceCorrectives = false
        )

        val state = screenModel.state.value
        assertEquals("User not logged in", state.error)
        assertNull(fitnessRepo.workoutRequestSent)
    }

    @Test
    fun testGeneratePlanIncludesImbalanceContext() = runTest {
        val authRepo = FakeAuthRepository()
        val fitnessRepo = FakeFitnessRepository()
        val useCase = GenerateWorkoutPlanUseCase(fitnessRepo)

        authRepo.signInForTest()

        val imbalance = MuscleImbalanceReport(
            id = "i-123",
            user_id = "mock-uuid-1234",
            bench_press_max = 75.0,
            squat_max = 95.0,
            deadlift_max = 110.0,
            overhead_press_max = 45.0,
            report_data = ImbalanceReportData(
                overall_balance_score = 75,
                imbalances = listOf(
                    ImbalanceItem(
                        area = "Chest/Back",
                        severity = "Moderate",
                        finding = "Push-pull imbalance",
                        recommendation = "Add more rows"
                    )
                ),
                priority_fixes = listOf("Seated Cable Rows", "Face Pulls")
            )
        )

        val screenModel = WorkoutScreenModel(useCase, authRepo)
        screenModel.setImbalanceContext(imbalance)

        screenModel.generatePlan(
            fitnessLevel = "Intermediate",
            goal = "Hypertrophy",
            daysPerWeek = 4,
            durationMin = 60,
            equipment = listOf("Barbell", "Dumbbell"),
            injuries = null,
            includeImbalanceCorrectives = true
        )

        val req = fitnessRepo.workoutRequestSent
        assertNotNull(req)
        assertNotNull(req.imbalance_context)
        assertTrue(req.imbalance_context!!.contains("75/100"))
        assertTrue(req.imbalance_context!!.contains("Seated Cable Rows"))

        assertEquals(75.0, req.current_lifts?.get("Bench Press"))
        assertEquals(95.0, req.current_lifts?.get("Squat"))
        assertEquals(110.0, req.current_lifts?.get("Deadlift"))
        assertEquals(45.0, req.current_lifts?.get("Overhead Press"))
    }

    @Test
    fun testGeneratePlanWithoutCorrectives() = runTest {
        val authRepo = FakeAuthRepository()
        val fitnessRepo = FakeFitnessRepository()
        val useCase = GenerateWorkoutPlanUseCase(fitnessRepo)

        authRepo.signInForTest()

        val screenModel = WorkoutScreenModel(useCase, authRepo)

        screenModel.generatePlan(
            fitnessLevel = "Beginner",
            goal = "Fat Loss",
            daysPerWeek = 3,
            durationMin = 45,
            equipment = listOf("Bodyweight"),
            injuries = "Wrist pain",
            includeImbalanceCorrectives = false
        )

        val req = fitnessRepo.workoutRequestSent
        assertNotNull(req)
        assertEquals("Beginner", req.fitness_level)
        assertEquals("Fat Loss", req.goal)
        assertEquals("Wrist pain", req.injuries_limitations)
        assertNull(req.imbalance_context)
        assertNull(req.current_lifts)
        assertFalse(screenModel.state.value.isLoading)
    }
}

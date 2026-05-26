package com.gujaratifitness.app.core.di

import com.gujaratifitness.app.presentation.screens.auth.AuthScreenModel
import com.gujaratifitness.app.presentation.screens.diet.DietScreenModel
import com.gujaratifitness.app.presentation.screens.exercises.ExerciseScreenModel
import com.gujaratifitness.app.presentation.screens.imbalance.ImbalanceScreenModel
import com.gujaratifitness.app.presentation.screens.onboarding.OnboardingScreenModel
import com.gujaratifitness.app.presentation.screens.profile.ProfileScreenModel
import com.gujaratifitness.app.presentation.screens.workout.WorkoutScreenModel
import org.koin.dsl.module

val domainModule = module {
    factory { AuthScreenModel(get()) }
    factory { OnboardingScreenModel(get()) }
    factory { ExerciseScreenModel(get()) }
    factory { ImbalanceScreenModel(get(), get()) }
    factory { WorkoutScreenModel(get(), get()) }
    factory { DietScreenModel(get(), get()) }
    factory { ProfileScreenModel(get(), get()) }
}

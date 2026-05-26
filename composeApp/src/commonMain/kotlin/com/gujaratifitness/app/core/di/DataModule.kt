package com.gujaratifitness.app.core.di

import com.gujaratifitness.app.data.repository.AuthRepository
import com.gujaratifitness.app.data.repository.ExerciseRepository
import com.gujaratifitness.app.data.repository.FitnessRepository
import org.koin.dsl.module

val dataModule = module {
    single { AuthRepository(get()) }
    single { ExerciseRepository(get(), get()) }
    single { FitnessRepository(get()) }
}

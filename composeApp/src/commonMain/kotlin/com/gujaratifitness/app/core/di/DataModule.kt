package com.gujaratifitness.app.core.di

import com.gujaratifitness.app.data.repository.*
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { SupabaseAuthRepository(get()) }
    single<ExerciseRepository> { SupabaseExerciseRepository(get(), get()) }
    single<FitnessRepository> { SupabaseFitnessRepository(get()) }
}

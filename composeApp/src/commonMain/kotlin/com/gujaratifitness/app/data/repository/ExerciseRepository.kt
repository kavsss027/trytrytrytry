package com.gujaratifitness.app.data.repository

import com.gujaratifitness.app.data.model.ExerciseDto
import com.gujaratifitness.app.database.AppDatabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

interface ExerciseRepository {
    suspend fun getLocalExercises(): List<ExerciseDto>
    suspend fun getLocalExercisesByMuscle(muscleGroup: String): List<ExerciseDto>
    suspend fun syncExercises()
}

class SupabaseExerciseRepository(
    private val supabase: SupabaseClient,
    private val database: AppDatabase
) : ExerciseRepository {
    private val queries = database.exerciseQueries

    override suspend fun getLocalExercises(): List<ExerciseDto> = withContext(Dispatchers.IO) {
        queries.getAllExercises().executeAsList().map {
            ExerciseDto(
                id = it.id,
                name = it.name,
                description = it.description,
                muscle_group = it.muscle_group,
                gif_url = it.gif_url,
                difficulty = it.difficulty
            )
        }
    }

    override suspend fun getLocalExercisesByMuscle(muscleGroup: String): List<ExerciseDto> = withContext(Dispatchers.IO) {
        queries.getByMuscleGroup(muscleGroup).executeAsList().map {
            ExerciseDto(
                id = it.id,
                name = it.name,
                description = it.description,
                muscle_group = it.muscle_group,
                gif_url = it.gif_url,
                difficulty = it.difficulty
            )
        }
    }

    override suspend fun syncExercises(): Unit = withContext(Dispatchers.IO) {
        val remoteExercises = supabase.postgrest.from("exercises")
            .select {
                filter {
                    eq("is_active", true)
                }
            }
            .decodeList<ExerciseDto>()

        queries.transaction {
            val now = Clock.System.now().toEpochMilliseconds()
            remoteExercises.forEach {
                queries.upsertExercise(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    muscle_group = it.muscle_group,
                    gif_url = it.gif_url,
                    difficulty = it.difficulty,
                    cached_at = now
                )
            }
        }
    }
}

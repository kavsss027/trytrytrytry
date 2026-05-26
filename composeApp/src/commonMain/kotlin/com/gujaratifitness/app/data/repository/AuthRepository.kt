package com.gujaratifitness.app.data.repository

import com.gujaratifitness.app.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository(private val supabase: SupabaseClient) {

    val sessionFlow: Flow<io.github.jan.supabase.auth.user.UserInfo?> =
        supabase.auth.sessionStatus.map { status ->
            if (status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated) {
                status.session.user
            } else null
        }

    val currentSessionUser: io.github.jan.supabase.auth.user.UserInfo?
        get() = supabase.auth.currentSessionOrNull()?.user

    suspend fun signUp(email: String, password: String, fullName: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName)
            }
        }
    }

    suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun getUserProfile(userId: String): UserProfile {
        return supabase.postgrest.from("users").select {
            filter {
                eq("id", userId)
            }
        }.decodeSingle<UserProfile>()
    }

    suspend fun updateUserProfile(profile: UserProfile): UserProfile {
        return supabase.postgrest.from("users").update(profile) {
            filter {
                eq("id", profile.id)
            }
        }.decodeSingle<UserProfile>()
    }
}

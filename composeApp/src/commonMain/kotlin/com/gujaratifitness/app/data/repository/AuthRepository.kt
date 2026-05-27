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

interface AuthRepository {
    val sessionFlow: Flow<io.github.jan.supabase.auth.user.UserInfo?>
    val currentSessionUser: io.github.jan.supabase.auth.user.UserInfo?
    suspend fun signUp(email: String, password: String, fullName: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    suspend fun getUserProfile(userId: String): UserProfile
    suspend fun updateUserProfile(profile: UserProfile): UserProfile
}

class SupabaseAuthRepository(private val supabase: SupabaseClient) : AuthRepository {

    override val sessionFlow: Flow<io.github.jan.supabase.auth.user.UserInfo?> =
        supabase.auth.sessionStatus.map { status ->
            if (status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated) {
                status.session.user
            } else null
        }

    override val currentSessionUser: io.github.jan.supabase.auth.user.UserInfo?
        get() = supabase.auth.currentSessionOrNull()?.user

    override suspend fun signUp(email: String, password: String, fullName: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName)
            }
        }
    }

    override suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut() {
        supabase.auth.signOut()
    }

    override suspend fun getUserProfile(userId: String): UserProfile {
        return try {
            supabase.postgrest.from("users").select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<UserProfile>()
        } catch (e: Exception) {
            val email = supabase.auth.currentSessionOrNull()?.user?.email ?: ""
            UserProfile(id = userId, email = email)
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile): UserProfile {
        return try {
            supabase.postgrest.from("users").upsert(profile).decodeSingle<UserProfile>()
        } catch (e: Exception) {
            try {
                supabase.postgrest.from("users").update(profile) {
                    filter {
                        eq("id", profile.id)
                    }
                }.decodeSingle<UserProfile>()
            } catch (e2: Exception) {
                profile
            }
        }
    }
}

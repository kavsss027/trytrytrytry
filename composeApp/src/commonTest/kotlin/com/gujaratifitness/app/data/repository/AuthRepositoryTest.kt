package com.gujaratifitness.app.data.repository

import com.gujaratifitness.app.data.model.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryTest {

    @Test
    fun testSignUpStoresCorrectCredentials() = runTest {
        val fakeRepo = FakeAuthRepository()

        fakeRepo.signUp("test@example.com", "password123", "Gujarati Fitness Fan")

        assertTrue(fakeRepo.signUpCalled)
        assertEquals("test@example.com", fakeRepo.signUpEmail)
        assertEquals("Gujarati Fitness Fan", fakeRepo.signUpFullName)
    }

    @Test
    fun testSignInUpdatesSession() = runTest {
        val fakeRepo = FakeAuthRepository()

        assertNull(fakeRepo.currentSessionUser)
        assertNull(fakeRepo.sessionFlow.first())

        fakeRepo.signIn("user@domain.com", "securepass")

        assertTrue(fakeRepo.signInCalled)
        assertEquals("user@domain.com", fakeRepo.signInEmail)

        val user = fakeRepo.currentSessionUser
        assertNotNull(user)
        assertEquals("user@domain.com", user.email)

        val flowUser = fakeRepo.sessionFlow.first()
        assertEquals(user, flowUser)
    }

    @Test
    fun testSignOutClearsSession() = runTest {
        val fakeRepo = FakeAuthRepository()
        fakeRepo.signIn("user@domain.com", "securepass")

        assertNotNull(fakeRepo.currentSessionUser)

        fakeRepo.signOut()

        assertTrue(fakeRepo.signOutCalled)
        assertNull(fakeRepo.currentSessionUser)
        assertNull(fakeRepo.sessionFlow.first())
    }

    @Test
    fun testUserProfileRetrievalAndUpdates() = runTest {
        val fakeRepo = FakeAuthRepository()
        val profile = UserProfile(
            id = "user-123",
            email = "user@domain.com",
            full_name = "Kavs",
            user_type = "premium",
            fitness_level = "Intermediate",
            influencer_id = null,
            created_at = "2026-05-27",
            updated_at = null
        )
        fakeRepo.userProfileResult = profile

        val fetchedProfile = fakeRepo.getUserProfile("user-123")
        assertEquals(profile, fetchedProfile)

        val updatedProfile = profile.copy(full_name = "Kavs Updated")
        val result = fakeRepo.updateUserProfile(updatedProfile)

        assertEquals(updatedProfile, fakeRepo.lastUpdatedProfile)
        assertEquals(updatedProfile, result)
    }
}

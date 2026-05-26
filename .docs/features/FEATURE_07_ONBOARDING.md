# FEATURE 07 — ONBOARDING FLOW
> Build this feature SECOND (immediately after Auth)
> Complexity: Low — navigation flow + user profile update
> Depends on: Auth system, users table

---

## WHAT THIS FEATURE DOES

First-time experience after registration. Asks the user where they are in their fitness journey and sets up their profile. This choice affects how the app presents content and language throughout.

---

## WHO SEES THIS

Every new user immediately after their first login. Never shown again after completion.

---

## SCREENS

### Screen 1 — Welcome Screen

**What the user sees:**
- App logo / welcome message
- "Let's set up your fitness profile"
- Two large cards to choose from:

```
[CARD 1]                    [CARD 2]
Complete Beginner           Intermediate Gym-goer
Just starting out           I know my way around
New to the gym              the gym already
```

Tapping either card navigates to Screen 2 with the selection carried forward.

---

### Screen 2 — Basic Info Screen

- Full name (text input)
- This screen is the same for both beginner and intermediate

After completing → updates users table:
```kotlin
supabase.postgrest["users"]
    .update({
        set("full_name", fullName)
        set("fitness_level", selectedLevel)
    })
    .eq("id", userId)
```

Then navigates to the main Home screen. Onboarding is marked complete.

---

### Screen 3 — (Branched) Beginner Welcome

Only shown to users who selected "Complete Beginner":
- "Welcome! We'll start with the basics."
- Brief 2-3 sentence explanation of what the app helps with
- "Let's Go" button → Home

---

### Onboarding Complete Check

```kotlin
// At app startup — in SplashScreen or AppNavigation
fun checkOnboardingComplete(user: User): Boolean {
    return user.fitnessLevel != null && user.fullName != null
}

// If false → navigate to Onboarding
// If true → navigate to Home
```

---

## ANTIGRAVITY CLI PROMPT

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_07_ONBOARDING.md \
  --prompt "Build the Onboarding Flow.
  1. OnboardingViewModel.kt — tracks fitness_level selection and fullName
  2. WelcomeScreen.kt — two path selection cards
  3. BasicInfoScreen.kt — name input + profile update
  4. BeginnerWelcomeScreen.kt — shown only for beginner path
  5. Update AppNavigation.kt to check onboarding completion at startup
  On completion: update users table fitness_level and full_name, then navigate to Home."
```

---

## DONE CRITERIA

```
□ New user sees onboarding after first login
□ Beginner and Intermediate paths both work
□ Full name saved to users table
□ Fitness level saved to users table
□ Beginner gets extra welcome screen
□ Onboarding never shown again after completion
□ Returning user skips directly to Home
□ App language/suggestions adjust based on fitness_level throughout the app
```
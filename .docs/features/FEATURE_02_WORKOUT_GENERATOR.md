# FEATURE 02 — WORKOUT PLAN GENERATOR
> Build this feature FIFTH (after Auth, Onboarding, Exercise Library, Muscle Imbalance)
> Complexity: High — AI API call, generation limits, imbalance pre-fill
> Depends on: Auth, Edge Function generate-workout-plan, generation_usage table, Muscle Imbalance feature

---

## WHAT THIS FEATURE DOES

A detailed questionnaire collects information about the user's goals, experience, equipment, and limitations. This data is sent to an Edge Function which calls Gemini 3.5 Flash (dev) or Claude (prod) to generate a fully personalised workout plan. The plan is saved to Supabase and displayed to the user.

---

## WHO CAN ACCESS

| User Type | Generations Per Month |
|-----------|----------------------|
| Free | 3 (exact number — finalize in Phase 4) |
| Premium | 20 (exact number — finalize in Phase 4) |
| Influencer | 20 |
| Owner | Unlimited |

---

## SCREENS IN THIS FEATURE

### Screen 1 — Generation Limit Check Screen

**What the user sees BEFORE the questionnaire:**
- Current usage: "You have used 1 of 3 plan generations this month"
- Progress bar showing remaining generations
- "Generate New Plan" button (disabled if limit reached)
- If limit reached: "Upgrade to Premium for more generations" message
- If they have an existing plan: Show it here with option to generate a new one

**API call (before showing questionnaire):**
```
POST /functions/v1/check-generation-limit
{ "feature": "workout_plan" }
```

---

### Screen 2 — Workout Questionnaire Screen

Multi-step form. Each step is a separate section, not a separate screen.

**Step 1 — Basic Info**
- Fitness level (auto-filled from onboarding — editable)
- Primary goal: Muscle Gain / Fat Loss / Strength / Endurance

**Step 2 — Schedule**
- Days per week (slider: 2–6)
- Session duration (slider: 30–120 minutes)

**Step 3 — Equipment**
- Multi-select checkboxes: Barbell, Dumbbells, Cables, Machines, Bodyweight Only, Resistance Bands

**Step 4 — Health**
- Any injuries or limitations? (text input — optional)
- Current max lifts (optional — pre-filled if imbalance data exists):
  - Bench press (kg)
  - Squat (kg)
  - Deadlift (kg)
  - Overhead press (kg)

**Step 5 — Review & Generate**
- Summary of all inputs
- "Generate My Plan" button
- Shows generation count warning if near limit

**⚠️ If imbalance data exists in the user's profile:**
Step 4 lifts are pre-filled automatically. A banner shows: "Your muscle imbalance data has been included for a more targeted plan."

---

### Screen 3 — Generating Screen

**What the user sees while AI is working:**
- Animated loading indicator
- Rotating messages: "Analysing your goals...", "Building your plan...", "Almost ready..."
- Cannot go back during generation (prevent double-generation)
- Estimated wait: 5–10 seconds

---

### Screen 4 — Plan Display Screen

**What the user sees:**
- Plan title
- Duration badge (e.g., "8-Week Plan")
- Day-by-day accordion sections:
  - Day name + focus (e.g., "Monday — Chest & Triceps")
  - Exercise list with sets, reps, rest time
  - Notes per exercise
- General notes section at bottom
- Nutrition reminder
- "Regenerate Plan" button (decrements generation count)
- "Share Plan" button (Phase 2 feature — not in V1)

---

## GENERATION LIMIT LOGIC

```
1. User taps "Generate My Plan"
2. App calls check-generation-limit endpoint
3. If can_generate = false → show limit screen, do NOT proceed
4. If can_generate = true → show questionnaire
5. User fills questionnaire and taps "Generate"
6. App calls generate-workout-plan endpoint
7. Edge Function increments generation_usage count
8. Plan returned and saved to workout_plans table
9. Plan displayed to user
```

---

## IMBALANCE PRE-FILL LOGIC

```kotlin
// In WorkoutViewModel
fun checkImbalanceData() {
    viewModelScope.launch {
        val latestReport = imbalanceRepository.getLatestReport(userId)
        if (latestReport != null) {
            _hasImbalanceData.value = true
            _imbalanceContext.value = ImbalanceContext(
                priorityFixes = latestReport.reportData.priorityFixes,
                overallBalanceScore = latestReport.reportData.overallBalanceScore
            )
            // Pre-fill current lifts
            _currentLifts.value = CurrentLifts(
                benchPress = latestReport.benchPressMax,
                squat = latestReport.squatMax,
                deadlift = latestReport.deadliftMax,
                overheadPress = latestReport.overheadPressMax
            )
        }
    }
}
```

---

## ERROR HANDLING

| Error | What User Sees |
|-------|---------------|
| Generation limit reached | "You've used all 3 plan generations this month. Your limit resets on [date]." |
| AI service down | "Our plan generator is busy right now. Please try again in a few minutes." |
| Network error | "Something went wrong. Please check your connection and try again." |
| Timeout (>30s) | "This is taking longer than usual. Please try again." |

---

## ANTIGRAVITY CLI PROMPT FOR THIS FEATURE

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/DATABASE_SCHEMA.md \
  --context .docs/contracts/API_CONTRACTS.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_02_WORKOUT_GENERATOR.md \
  --prompt "Build the Workout Plan Generator feature as specified.
  Start with:
  1. Edge Function: generate-workout-plan/index.ts (TypeScript/Deno, calls Gemini)
  2. Edge Function: check-generation-limit/index.ts
  3. PlanRemoteSource.kt — calls both edge functions via Ktor
  4. PlanRepository.kt
  5. GenerateWorkoutPlanUseCase.kt
  6. WorkoutViewModel.kt with all states and imbalance pre-fill logic
  7. WorkoutQuestionnaireScreen.kt — multi-step form
  8. PlanDisplayScreen.kt — shows generated plan
  Do NOT call Gemini from Kotlin. Only from Edge Function."
```

---

## DONE CRITERIA

```
□ Generation limit checked before showing questionnaire
□ Questionnaire collects all required fields
□ Imbalance data pre-fills if available
□ Loading screen shows during generation
□ Plan displays correctly with all sections
□ Generation count increments after successful generation
□ All error states handled with user-friendly messages
□ Free user cannot generate beyond monthly limit
□ Plan saved to Supabase and persists on app restart
```

# FEATURE 04 — MUSCLE IMBALANCE DETECTOR ★ KEY DIFFERENTIATOR
> Build this feature FOURTH (before AI features — no AI dependency for the detection)
> Complexity: Medium — data input form + AI analysis via Edge Function
> Depends on: Auth, Edge Function detect-muscle-imbalance, muscle_imbalance_reports table

---

## WHAT THIS FEATURE DOES

User inputs their PR maxes and training frequency. The app sends this to an Edge Function which analyses the data and generates a muscle imbalance report showing which body parts are underdeveloped. After seeing the report, the user is prompted to update or create a workout plan using this data.

---

## WHO CAN ACCESS

All user types — free, premium, influencer, owner.

---

## SCREENS

### Screen 1 — Imbalance Input Screen

**Section 1 — Maximum Lifts**
All fields optional but more = better analysis
- Bench Press max (kg) — number input
- Squat max (kg) — number input
- Deadlift max (kg) — number input
- Overhead Press max (kg) — number input
- Pull-ups / Rows max reps — number input

**Section 2 — Training Frequency**
- Days per week training each muscle group (0–7 for each):
  Chest / Back / Shoulders / Biceps / Triceps / Legs / Core

**Section 3 — Training History**
- How long training: dropdown:
  Less than 6 months / 6–12 months / 1–2 years / 2–4 years / 4+ years

- "Analyse My Imbalances" button at bottom

---

### Screen 2 — Analysing Screen
- Loading animation
- Message: "Analysing your training data..."

---

### Screen 3 — Imbalance Report Screen

**What the user sees:**
- Overall Balance Score (0–100) — displayed as a large score circle
- Score interpretation: Poor / Fair / Good / Excellent
- Imbalance list — each item shows:
  - Area name (e.g., "Posterior Chain")
  - Severity badge: Mild / Moderate / Severe
  - Finding (what the data shows)
  - Recommendation (what to do)
- Strengths section (what is balanced well)
- Priority fixes list

**After report — Action Prompt:**

```
IF user has existing workout plan:
  Show: "Want to update your plan based on these findings?"
  Button: "Update My Plan" → navigates to WorkoutQuestionnaire with imbalance pre-filled
  Button: "Save Report Only" → saves to profile

IF user has NO existing workout plan:
  Show: "Generate a workout plan that targets your imbalances?"
  Button: "Create My Plan" → navigates to WorkoutQuestionnaire with imbalance pre-filled
  Button: "Save Report Only" → saves to profile
```

---

## EDGE CASE — NO EXISTING PLAN

```kotlin
// In ImbalanceViewModel
fun checkExistingPlan() {
    viewModelScope.launch {
        val activePlan = planRepository.getActivePlan(userId)
        _hasExistingPlan.value = activePlan != null
    }
}
// Screen uses this to show correct post-report prompt
```

---

## ANTIGRAVITY CLI PROMPT

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/DATABASE_SCHEMA.md \
  --context .docs/contracts/API_CONTRACTS.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_04_MUSCLE_IMBALANCE.md \
  --prompt "Build the Muscle Imbalance Detector feature as specified.
  1. detect-muscle-imbalance Edge Function (TypeScript/Deno, calls Gemini)
  2. ImbalanceRemoteSource.kt
  3. ImbalanceRepository.kt
  4. DetectMuscleImbalanceUseCase.kt
  5. ImbalanceViewModel.kt including checkExistingPlan logic
  6. ImbalanceInputScreen.kt with all input sections
  7. ImbalanceReportScreen.kt with score display and action prompt
  Handle both 'has existing plan' and 'no existing plan' post-report flows."
```

---

## DONE CRITERIA

```
□ All input fields collected
□ Report shows balance score, imbalances, strengths, priority fixes
□ Post-report prompt changes based on whether existing plan exists
□ Navigates to workout generator with imbalance pre-filled on "Update/Create Plan"
□ Report saved to Supabase on "Save Report Only"
□ Latest report visible on user profile
□ All error states handled
```
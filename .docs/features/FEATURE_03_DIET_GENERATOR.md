# FEATURE 03 — DIET PLAN GENERATOR
> Build this feature SIXTH (mirrors workout generator — easier second time)
> Complexity: Medium — same AI pattern as workout generator
> Depends on: Auth, Edge Function generate-diet-plan, generation_usage table

---

## WHAT THIS FEATURE DOES

Questionnaire designed with a Masters-level nutrition expert. Sends answers to Edge Function which calls AI to generate a personalised diet plan with meals, macros, and calories. Includes Gujarati food options.

---

## WHO CAN ACCESS

Same generation limits as workout plan generator. See FEATURE_02.

---

## SCREENS

### Screen 1 — Diet Questionnaire (Multi-Step)

**Step 1 — Goals**
- Goal: Fat Loss / Muscle Gain / Maintenance
- Activity level: Sedentary / Lightly Active / Moderately Active / Very Active

**Step 2 — Body Stats**
- Age (number input)
- Gender (Male / Female)
- Weight in kg (number input)
- Height in cm (number input)

**Step 3 — Food Preferences**
- Dietary type: Vegetarian / Non-Vegetarian / Vegan
- Food allergies (text — optional)
- Meals per day (2–6 slider)
- Include Gujarati food options? (toggle — default ON)

**Step 4 — Review & Generate**
- Summary of inputs
- "Generate My Diet Plan" button

---

### Screen 2 — Generating Screen
Same as workout generator — rotating messages, loading animation.

---

### Screen 3 — Diet Plan Display Screen

**What the user sees:**
- Plan title
- Daily calorie target (prominent display)
- Macro breakdown: Protein / Carbs / Fat (in grams + percentage)
- Meal-by-meal breakdown:
  - Meal name and suggested time
  - Food list
  - Calorie count per meal
- Hydration target
- General notes and tips

---

## ANTIGRAVITY CLI PROMPT

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/API_CONTRACTS.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_03_DIET_GENERATOR.md \
  --prompt "Build the Diet Plan Generator following the exact same architecture
  as the Workout Plan Generator. Reuse the check-generation-limit Edge Function.
  Create generate-diet-plan Edge Function. Mirror the workout generator pattern
  for all Kotlin files. Feature is gujarati_food_preference aware."
```

---

## DONE CRITERIA

```
□ All questionnaire fields collected
□ Gujarati food preference flag sent to Edge Function
□ Plan shows macros, meals, and calories
□ Generation limit shared with workout plan (same monthly total or separate — confirm in Phase 4)
□ All error states handled
□ Plan saved and persists on restart
```
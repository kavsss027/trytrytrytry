# API_CONTRACTS.md — GUJARATI FITNESS APP
> Every Edge Function endpoint is defined here.
> Kotlin code and Edge Function code must match these contracts exactly.
> No endpoint may be added or changed without updating this document.

---

## BASE URL

```
Development:  http://localhost:54321/functions/v1/
Production:   https://[project-ref].supabase.co/functions/v1/
```

All requests require:
```
Authorization: Bearer [user_jwt_token]
Content-Type: application/json
apikey: [SUPABASE_ANON_KEY]
```

---

## ENDPOINT 1 — generate-workout-plan

**Purpose:** Takes questionnaire answers, calls AI, returns a personalised workout plan.

### Request
```
POST /functions/v1/generate-workout-plan
```

```json
{
  "fitness_level": "beginner",
  "goal": "muscle_gain",
  "days_per_week": 4,
  "session_duration_minutes": 60,
  "available_equipment": ["barbell", "dumbbells", "cables"],
  "injuries_limitations": "lower back pain",
  "current_lifts": {
    "bench_press": 60,
    "squat": 80,
    "deadlift": 100,
    "overhead_press": 40
  },
  "imbalance_context": null
}
```

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| fitness_level | string | ✅ | "beginner" or "intermediate" |
| goal | string | ✅ | "muscle_gain", "fat_loss", "strength", "endurance" |
| days_per_week | integer | ✅ | 2–6 |
| session_duration_minutes | integer | ✅ | 30–120 |
| available_equipment | string[] | ✅ | Equipment user has access to |
| injuries_limitations | string | ❌ | null if none |
| current_lifts | object | ❌ | null if unknown |
| imbalance_context | object | ❌ | Pre-filled from imbalance report if exists |

### Response — Success (200)
```json
{
  "plan": {
    "title": "4-Day Muscle Building Plan",
    "duration_weeks": 8,
    "days": [
      {
        "day": "Monday",
        "focus": "Chest and Triceps",
        "exercises": [
          {
            "name": "Bench Press",
            "sets": 4,
            "reps": "8-10",
            "rest_seconds": 90,
            "notes": "Keep elbows at 45 degrees"
          }
        ]
      }
    ],
    "general_notes": "Progressive overload every week",
    "nutrition_reminder": "Eat sufficient protein — 1.6–2g per kg bodyweight"
  }
}
```

### Response — Error (400)
```json
{ "error": "Missing required field: fitness_level" }
```

### Response — Rate Limited (429)
```json
{ "error": "Generation limit reached for this month", "limit": 3, "used": 3 }
```

### Response — Server Error (500)
```json
{ "error": "AI service temporarily unavailable. Please try again." }
```

### Auth Rule
- Requires valid JWT (logged-in user)
- Edge Function checks generation_usage before calling AI
- Free users: max 3 per month
- Premium users: max 20 per month (final number — Phase 4)

---

## ENDPOINT 2 — generate-diet-plan

**Purpose:** Takes nutrition questionnaire, calls AI, returns personalised diet plan.

### Request
```
POST /functions/v1/generate-diet-plan
```

```json
{
  "fitness_level": "intermediate",
  "goal": "fat_loss",
  "age": 25,
  "gender": "male",
  "weight_kg": 80,
  "height_cm": 175,
  "activity_level": "moderately_active",
  "dietary_preference": "vegetarian",
  "food_allergies": "none",
  "meals_per_day": 4,
  "gujarati_food_preference": true
}
```

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| goal | string | ✅ | "fat_loss", "muscle_gain", "maintenance" |
| age | integer | ✅ | |
| gender | string | ✅ | "male" or "female" |
| weight_kg | number | ✅ | |
| height_cm | number | ✅ | |
| activity_level | string | ✅ | "sedentary", "lightly_active", "moderately_active", "very_active" |
| dietary_preference | string | ✅ | "vegetarian", "non_vegetarian", "vegan" |
| gujarati_food_preference | boolean | ✅ | Includes Gujarati food examples in plan |

### Response — Success (200)
```json
{
  "plan": {
    "title": "Fat Loss Diet Plan",
    "daily_calories": 1800,
    "macros": {
      "protein_g": 144,
      "carbs_g": 180,
      "fat_g": 60
    },
    "meals": [
      {
        "meal": "Breakfast",
        "time": "8:00 AM",
        "foods": ["2 eggs", "1 cup poha", "1 banana"],
        "calories": 450
      }
    ],
    "hydration_litres": 3,
    "notes": "Increase protein with paneer and dal"
  }
}
```

---

## ENDPOINT 3 — detect-muscle-imbalance

**Purpose:** Analyses PR data and training frequency to generate an imbalance report.

### Request
```
POST /functions/v1/detect-muscle-imbalance
```

```json
{
  "bench_press_max_kg": 80,
  "squat_max_kg": 100,
  "deadlift_max_kg": 120,
  "overhead_press_max_kg": 50,
  "pullup_rows_max_reps": 12,
  "training_days_per_week": {
    "chest": 2,
    "back": 2,
    "shoulders": 1,
    "biceps": 1,
    "triceps": 1,
    "legs": 1,
    "core": 2
  },
  "training_duration_months": 18
}
```

### Response — Success (200)
```json
{
  "report": {
    "overall_balance_score": 62,
    "imbalances": [
      {
        "area": "Posterior Chain",
        "severity": "moderate",
        "finding": "Deadlift:Squat ratio indicates underdeveloped hamstrings",
        "recommendation": "Add Romanian deadlifts and leg curls"
      },
      {
        "area": "Push:Pull Balance",
        "severity": "mild",
        "finding": "Bench press volume exceeds pull movements",
        "recommendation": "Add one extra back session per week"
      }
    ],
    "strengths": ["Good overhead:bench ratio", "Core training frequency adequate"],
    "priority_fixes": ["Hamstrings", "Upper back volume"]
  }
}
```

---

## ENDPOINT 4 — check-generation-limit

**Purpose:** Checks whether a user can generate another plan this month before calling the AI.

### Request
```
POST /functions/v1/check-generation-limit
```

```json
{
  "feature": "workout_plan"
}
```

### Response — Can Generate (200)
```json
{
  "can_generate": true,
  "used": 1,
  "limit": 3,
  "remaining": 2
}
```

### Response — Limit Reached (200)
```json
{
  "can_generate": false,
  "used": 3,
  "limit": 3,
  "remaining": 0,
  "reset_date": "2026-06-01"
}
```

---

## SUPABASE DATABASE CALLS (Direct from App via supabase-kt)

These are NOT Edge Functions. The app calls Supabase PostgREST directly.

### Get Exercises by Muscle Group
```kotlin
supabase.postgrest["exercises"]
    .select()
    .eq("muscle_group", muscleGroup)
    .eq("is_active", true)
```

### Get S-TIER Exercises for Premium User
```kotlin
supabase.postgrest["stier_exercises"]
    .select()
    .eq("is_active", true)
    .or("is_global.eq.true,influencer_id.eq.${user.influencerId}")
```

### Save Workout Plan
```kotlin
supabase.postgrest["workout_plans"].insert(workoutPlanData)
```

### Get Active Workout Plan
```kotlin
supabase.postgrest["workout_plans"]
    .select()
    .eq("user_id", userId)
    .eq("is_active", true)
    .single()
```

### Submit Join Request
```kotlin
supabase.postgrest["influencer_join_requests"].insert(joinRequest)
```

### Influencer — Get Pending Requests
```kotlin
supabase.postgrest["influencer_join_requests"]
    .select()
    .eq("influencer_id", influencerId)
    .eq("status", "pending")
```

---

## HTTP STATUS CODE REFERENCE

| Code | Meaning | When Used |
|------|---------|-----------|
| 200 | Success | Request processed successfully |
| 400 | Bad Request | Missing or invalid input fields |
| 401 | Unauthorized | No valid JWT token provided |
| 403 | Forbidden | User lacks permission for this action |
| 429 | Too Many Requests | Generation limit reached |
| 500 | Server Error | AI service error or unexpected failure |

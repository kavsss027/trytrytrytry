# DEVELOPMENT_SEQUENCE.md — GUJARATI FITNESS APP
> The exact order everything gets built. Follow this sequence. Do not skip steps.
> Updated as phases complete.

---

## PHASE OVERVIEW

```
Phase 1 ✅  Requirements & Scope
Phase 2 ✅  Architecture & Tech Stack
Phase 3 🔄  Environment Setup
Phase 4 ⏳  Data Models & API Contracts
Phase 5 ⏳  Backend Implementation
Phase 6 ⏳  Frontend Integration
Phase 7 ⏳  Edge Cases & Error Handling
Phase 8 ⏳  Testing & Pre-launch
```

---

## PHASE 3 — ENVIRONMENT SETUP (IN PROGRESS)

```
Step 1:  Create KMP project in Android Studio ← YOU ARE HERE
Step 2:  Replace libs.versions.toml with verified version
Step 3:  Set up local.properties with Supabase keys
Step 4:  Create Supabase dev project (Singapore region)
Step 5:  Run supabase login + supabase link
Step 6:  Create supabase/.env.local with Gemini key
Step 7:  Connect Gemini API key to Android Studio
Step 8:  Verify .gitignore protects all secrets
Step 9:  Make first Git commit
Step 10: Commit all .docs/ files to Git
→ Phase 3 COMPLETE when all 10 steps done
```

---

## PHASE 4 — DATA MODELS & API CONTRACTS

```
Step 1:  Finalize generation limits (free: ? / premium: ?)
Step 2:  Run all SQL from DATABASE_SCHEMA.md in Supabase dashboard
Step 3:  Enable RLS on all tables
Step 4:  Add all RLS policies
Step 5:  Create all indexes
Step 6:  Create Supabase Storage bucket: exercise-gifs
Step 7:  Verify all tables visible in Supabase dashboard
Step 8:  Verify RLS policies working (test with anon key — should be blocked)
→ Phase 4 COMPLETE when all tables created and RLS verified
```

---

## PHASE 5 — BACKEND IMPLEMENTATION

Build in this exact order — each depends on the previous:

```
Step 1:  Supabase Auth setup (email/password login enabled in dashboard)
Step 2:  Edge Function: check-generation-limit
Step 3:  Edge Function: generate-workout-plan (calls Gemini)
Step 4:  Edge Function: generate-diet-plan (calls Gemini)
Step 5:  Edge Function: detect-muscle-imbalance (calls Gemini)
Step 6:  Test all Edge Functions locally with supabase functions serve
Step 7:  Deploy all Edge Functions to Supabase
Step 8:  Test deployed functions with curl
→ Phase 5 COMPLETE when all 4 Edge Functions deployed and tested
```

---

## PHASE 6 — FRONTEND INTEGRATION

Build features in this exact order:

```
FOUNDATION (must be first):
  Step 1:  Core setup — HttpClientFactory, DatabaseFactory, DriverFactory expect/actual
  Step 2:  Koin modules — AppModule, DataModule, DomainModule
  Step 3:  Voyager navigation setup — AppNavigation, Routes
  Step 4:  Shared components — LoadingScreen, ErrorScreen, EmptyState, AppButton, AppTextField

AUTH (must be before any feature):
  Step 5:  Auth screens — LoginScreen, RegisterScreen
  Step 6:  Auth ViewModel + Supabase Auth integration
  Step 7:  Token refresh handling

FEATURE BUILD ORDER:
  Step 8:  Feature 07 — Onboarding (fitness level + name collection)
  Step 9:  Feature 01 — Exercise Library (GIF list + detail screens)
  Step 10: Feature 04 — Muscle Imbalance Detector
  Step 11: Feature 02 — Workout Plan Generator
  Step 12: Feature 03 — Diet Plan Generator
  Step 13: Feature 05 — S-TIER Section (premium gate)
  Step 14: Feature 06 — Influencer System & Admin Panel

→ Phase 6 COMPLETE when all features working end-to-end on Android emulator
```

---

## PHASE 7 — EDGE CASES & ERROR HANDLING

```
Step 1:  Edge Case 1 — Imbalance detector with no existing plan
Step 2:  Edge Case 2 — AI API rate limit exceeded
Step 3:  Edge Case 3 — Influencer slot limit full
Step 4:  Edge Case 4 — Join request never responded to
Step 5:  Network failure on every screen
Step 6:  Token expiry mid-session
Step 7:  Empty states on every list screen
Step 8:  App killed during AI generation
→ Phase 7 COMPLETE when all edge cases tested and handled
```

---

## PHASE 8 — TESTING & PRE-LAUNCH

```
Step 1:  Manual test all features — TESTING_GUIDE.md checklist
Step 2:  Verify no secrets in codebase (git grep for API keys)
Step 3:  Create Supabase prod project
Step 4:  Run DATABASE_SCHEMA.md on prod project
Step 5:  Deploy Edge Functions to prod
Step 6:  Update local.properties with prod keys
Step 7:  Build release APK
Step 8:  Test release APK on physical Android device
Step 9:  Google Play Store submission prep
→ Phase 8 COMPLETE = app is live
```

---

## FEATURE BUILD DETAIL — WHAT EACH FEATURE NEEDS

For each feature, build in this internal order:
```
1. SQLDelight schema (if feature needs local cache)
2. Data models (@Serializable classes) — already in DATA_MODELS.md
3. Remote source (Supabase/Edge Function calls)
4. Local source (SQLDelight queries)
5. Repository (combines remote + local)
6. UseCase (business logic)
7. ViewModel (state management)
8. Screens (UI)
9. Navigation wiring (add route to AppNavigation)
10. Test on emulator
```

---

## ANTIGRAVITY CLI SESSION START TEMPLATE

Use at the start of every session:

```bash
cd C:\Projects\GujaratiFitnessApp

antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/agent/PROJECT_STATUS.md \
  --context [relevant feature or contract doc] \
  --prompt "[Copy the NEXT SESSION PROMPT from PROJECT_STATUS.md]"
```

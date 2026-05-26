# MASTER CONTEXT — GUJARATI FITNESS APP
> Paste this file at the start of EVERY Antigravity CLI session and every Android Studio Agent Mode session.
> This is the single source of truth for all AI agents working on this project.

---

## WHAT THIS APP IS

A fitness app built for Indian Gujarati gym-goers. Language is English.
Target users are complete beginners and intermediate gym-goers.
The app solves four problems: wrong exercise form, generic workout plans, generic diet plans, and no awareness of muscle imbalances.

---

## CRITICAL TECH STACK RULES — READ BEFORE WRITING ANY CODE

```
DO NOT suggest or use these under any circumstances:
❌ Firebase / Firestore
❌ Room Database
❌ Retrofit
❌ Hilt
❌ Jetpack Navigation (use Voyager only)
❌ OkHttp directly (Ktor uses it internally — never import OkHttp directly)
❌ Any library not listed in libs.versions.toml
❌ Any AI API call from the app directly (all AI calls go through Edge Functions)
```

---

## CONFIRMED TECH STACK — LOCKED

```
LANGUAGE              Kotlin 2.2.20
UI FRAMEWORK          Compose Multiplatform 1.10.3
PLATFORMS             Android (primary) + iOS (when Mac available)
STATE MANAGEMENT      ViewModel + StateFlow
NAVIGATION            Voyager 1.1.0-beta03
NETWORKING            Ktor Client 3.4.3
LOCAL DATABASE        SQLDelight 2.1.0
DEPENDENCY INJECTION  Koin 4.1.0
IMAGE + GIF LOADING   Coil 3.0.4 (GIF: expect/actual pattern)
BACKEND               Supabase (PostgreSQL + Auth + Storage + Edge Functions)
SUPABASE KT CLIENT    supabase-kt BOM 3.1.4
AI — DEVELOPMENT      Gemini 3.5 Flash (gemini-3.5-flash) via Edge Functions
AI — PRODUCTION       Claude claude-sonnet-4-20250514 via Edge Functions
BUILD TOOL            Antigravity CLI + Android Studio Panda 4
MIN ANDROID SDK       API 26 (Android 8.0)
```

---

## PROJECT LOCATION

```
C:\Projects\GujaratiFitnessApp\
```

---

## FOLDER STRUCTURE

```
composeApp/src/
  commonMain/kotlin/com/gujaratifitness/app/
    core/
      network/          ← Ktor HttpClient setup
      database/         ← SQLDelight setup + DriverFactory expect/actual
      di/               ← Koin modules
      utils/            ← Constants, extensions, helpers
    data/
      models/           ← @Serializable data classes
      remote/           ← Supabase + Edge Function calls
      local/            ← SQLDelight queries
      repository/       ← Combines remote + local data sources
    domain/
      usecases/         ← Business logic only — no UI, no DB direct access
    presentation/
      screens/          ← One subfolder per screen
      components/       ← Reusable Composables
      viewmodels/       ← One ViewModel per screen
    navigation/         ← Voyager routes and navigation setup

  androidMain/kotlin/com/gujaratifitness/app/
    core/database/      ← AndroidSqliteDriver actual implementation
    core/gif/           ← Coil GifDecoder actual implementation

  iosMain/kotlin/com/gujaratifitness/app/
    core/database/      ← NativeSqliteDriver actual implementation
    core/gif/           ← UIKit native GIF actual implementation
```

---

## 7 V1 FEATURES — STATUS

| # | Feature | Status |
|---|---------|--------|
| 1 | Exercise Library | ⏳ Not started |
| 2 | Workout Plan Generator | ⏳ Not started |
| 3 | Diet Plan Generator | ⏳ Not started |
| 4 | Muscle Imbalance Detector | ⏳ Not started |
| 5 | S-TIER Exercise Section | ⏳ Not started |
| 6 | Influencer System & Admin Panel | ⏳ Not started |
| 7 | Onboarding Flow | ⏳ Not started |

---

## 4 USER TYPES

```
FREE USER       → Registers directly. Access: Exercise library, limited plan generations,
                  limited diet plans, muscle imbalance detector.

PREMIUM USER    → Joins an influencer group (approved by influencer).
                  Access: Everything free + S-TIER section + higher/unlimited generations.

INFLUENCER      → Manually onboarded by app owner.
                  Access: Own admin panel, manage user slots, approve/reject join requests,
                  add exercises to their S-TIER list.

APP OWNER       → Built-in superadmin (you only).
                  Access: Full platform control — add exercises, manage global S-TIER list,
                  manage all influencers.
```

---

## CURRENT PHASE

```
Phase 1 — Requirements & Scope          ✅ COMPLETE
Phase 2 — Architecture & Tech Stack     ✅ COMPLETE
Phase 3 — Environment & Secrets Setup   🔄 IN PROGRESS
Phase 4 — Data Models & API Contracts   ⏳ NOT STARTED
Phase 5 — Backend Implementation        ⏳ NOT STARTED
Phase 6 — CMP Frontend Integration      ⏳ NOT STARTED
Phase 7 — Edge Cases & Error Handling   ⏳ NOT STARTED
Phase 8 — Testing & Pre-launch          ⏳ NOT STARTED
```

---

## SECRETS RULES — NEVER VIOLATE

```
✅ SUPABASE_URL and SUPABASE_ANON_KEY → local.properties only
✅ GEMINI_API_KEY → supabase/.env.local only
✅ All AI API keys → Supabase Edge Function secrets only
❌ Never hardcode any key in any Kotlin or TypeScript file
❌ Never call Gemini or Claude API from the app directly
❌ Never commit local.properties or supabase/.env.local to Git
```

---

## ANTIGRAVITY CLI — HOW TO START A SESSION

```bash
# Navigate to project
cd C:\Projects\GujaratiFitnessApp

# Start a task with context
antigravity run --context .docs/MASTER_CONTEXT.md --context .docs/features/FEATURE_0X.md --prompt "Your task here"

# Check agent status
antigravity status

# View agent logs
antigravity logs
```

---

## CURRENT TASK

See `.docs/agent/PROJECT_STATUS.md` → Section: NEXT SESSION PROMPT

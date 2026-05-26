# LOCAL_SETUP.md — GUJARATI FITNESS APP
> Complete setup guide for running this project from scratch on Windows 11.
> Follow every step in order. Do not skip.

---

## TOOLS INSTALLED (CONFIRMED)

```
✅ Git 2.x.x
✅ Android Studio Panda 4 (2025.3.4)
✅ KMP Plugin (installed via Android Studio Settings → Plugins)
✅ Node.js 22.x.x LTS
✅ Supabase CLI 2.x.x
✅ Deno 2.x.x
✅ Antigravity CLI (latest)
```

---

## PROJECT LOCATION

```
C:\Projects\GujaratiFitnessApp\
```

---

## STEP 1 — CLONE OR OPEN THE PROJECT

If opening for the first time:
```
Android Studio → Open → C:\Projects\GujaratiFitnessApp\ → OK
```

Wait for Gradle sync to complete (progress bar at bottom).

---

## STEP 2 — SET UP local.properties

File location: `C:\Projects\GujaratiFitnessApp\local.properties`

This file is gitignored — you must create it manually on any new machine.

```properties
# Android SDK path (Android Studio fills this automatically)
sdk.dir=C\:\\Users\\ADMIN\\AppData\\Local\\Android\\Sdk

# Supabase — Development Project
SUPABASE_URL=https://your-dev-project.supabase.co
SUPABASE_ANON_KEY=your-dev-anon-key-here
```

Get these values from:
```
Supabase Dashboard → gujarati-fitness-dev → Project Settings → API
```

---

## STEP 3 — SET UP supabase/.env.local

File location: `C:\Projects\GujaratiFitnessApp\supabase\.env.local`

This file is gitignored — you must create it manually on any new machine.

```env
# Backend secrets — never commit this file
GEMINI_API_KEY=your-gemini-api-key-here
```

Get your Gemini API key from: https://aistudio.google.com/apikey

---

## STEP 4 — LINK SUPABASE CLI

```bash
cd C:\Projects\GujaratiFitnessApp
supabase login
supabase link --project-ref your-project-ref-here
```

Your project ref is the subdomain of your Supabase URL.
Example: if URL is `https://abcxyz123.supabase.co` then ref is `abcxyz123`

When prompted for database password — use the password set during Supabase project creation.

---

## STEP 5 — CONNECT GEMINI TO ANDROID STUDIO

```
Android Studio → File → Settings → Tools → Gemini
Select: Use your own API key
Paste: Your Gemini API key from Google AI Studio
Click: Apply → OK
```

This enables Agent Mode with Gemini 3.5 Flash.

---

## STEP 6 — VERIFY EVERYTHING WORKS

Run these checks:

```bash
# Check Git
git --version
# Expected: git version 2.x.x

# Check Supabase connection
supabase status
# Expected: shows your project name and linked status

# Check Deno
deno --version
# Expected: deno 2.x.x

# Check Antigravity CLI
antigravity --version
# Expected: version number
```

In Android Studio:
```
Run → Run 'composeApp' (Android)
Expected: emulator launches and shows the app
```

---

## COMPLETE libs.versions.toml

File location: `C:\Projects\GujaratiFitnessApp\gradle\libs.versions.toml`

```toml
[versions]
kotlin                  = "2.2.20"
compose-multiplatform   = "1.10.3"
voyager                 = "1.1.0-beta03"
ktor                    = "3.4.3"
sqldelight              = "2.1.0"
koin                    = "4.1.0"
coil                    = "3.0.4"
supabase                = "3.1.4"
lifecycle               = "2.9.0"
kotlinx-serialization   = "1.8.1"
kotlinx-coroutines      = "1.10.2"
agp                     = "8.7.0"

[libraries]
ktor-client-core              = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp            = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin            = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-content-negotiation      = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-json       = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging           = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
sqldelight-runtime            = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines         = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android-driver     = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver      = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }
koin-core                     = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose                  = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-compose-viewmodel        = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }
voyager-navigator             = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-screenmodel           = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager-transitions           = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }
voyager-tab-navigator         = { module = "cafe.adriel.voyager:voyager-tab-navigator", version.ref = "voyager" }
voyager-koin                  = { module = "cafe.adriel.voyager:voyager-koin", version.ref = "voyager" }
coil-compose                  = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network-ktor             = { module = "io.coil-kt.coil3:coil-network-ktor3", version.ref = "coil" }
coil-gif                      = { module = "io.coil-kt.coil3:coil-gif", version.ref = "coil" }
supabase-bom                  = { module = "io.github.jan-tennert.supabase:bom", version.ref = "supabase" }
supabase-auth                 = { module = "io.github.jan-tennert.supabase:auth-kt" }
supabase-postgrest            = { module = "io.github.jan-tennert.supabase:postgrest-kt" }
supabase-storage              = { module = "io.github.jan-tennert.supabase:storage-kt" }
supabase-functions            = { module = "io.github.jan-tennert.supabase:functions-kt" }
supabase-compose-auth         = { module = "io.github.jan-tennert.supabase:compose-auth" }
lifecycle-viewmodel           = { module = "org.jetbrains.androidx.lifecycle:lifecycle-viewmodel", version.ref = "lifecycle" }
lifecycle-viewmodel-compose   = { module = "org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycle" }
kotlinx-serialization-json    = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-coroutines-core       = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }

[plugins]
kotlin-multiplatform          = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
compose-multiplatform         = { id = "org.jetbrains.compose", version.ref = "compose-multiplatform" }
compose-compiler              = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization          = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
sqldelight-plugin             = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
android-application           = { id = "com.android.application", version.ref = "agp" }
```

---

## RUNNING EDGE FUNCTIONS LOCALLY

```bash
cd C:\Projects\GujaratiFitnessApp

# Start local Supabase environment
supabase start

# Serve all Edge Functions locally
supabase functions serve --env-file supabase/.env.local

# Test a specific function
curl -X POST http://localhost:54321/functions/v1/generate-workout-plan \
  -H "Content-Type: application/json" \
  -d '{"test": true}'
```

---

## ANTIGRAVITY CLI — COMMON COMMANDS

```bash
# Start a new development task
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/features/FEATURE_01_EXERCISE_LIBRARY.md \
  --prompt "Build the exercise list screen with muscle group filter tabs"

# Check what the agent is doing
antigravity status

# View agent output logs
antigravity logs

# Stop a running agent
antigravity stop

# Run with a specific model
antigravity run --model gemini-3.5-flash --prompt "Your task"
```

---

## GITIGNORE — VERIFY THESE EXIST

```gitignore
local.properties
supabase/.env.local
*.apk
*.aab
build/
.gradle/
.idea/
*.iml
```

---

## ENVIRONMENT VARIABLES CHECKLIST

Before any development session:
```
□ local.properties has SUPABASE_URL and SUPABASE_ANON_KEY filled in
□ supabase/.env.local has GEMINI_API_KEY filled in
□ supabase link shows connected to gujarati-fitness-dev
□ Android Studio Gemini API key is connected
□ Neither secrets file appears in git status output
```

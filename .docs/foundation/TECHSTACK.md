# TECHSTACK.md — GUJARATI FITNESS APP
> STATUS: LOCKED — Phase 2 Complete
> No library, version, or tool may be changed without a deliberate replanning session logged in DECISION_LOG.md

---

## FRONTEND — COMPOSE MULTIPLATFORM

| Layer | Library | Version | Purpose |
|-------|---------|---------|---------|
| Language | Kotlin | 2.2.20 | Everything is written in Kotlin |
| UI Framework | Compose Multiplatform | 1.10.3 | One UI codebase for Android + iOS |
| State Management | ViewModel + StateFlow | lifecycle 2.9.0 | Screen state and reactive UI updates |
| Navigation | Voyager | 1.1.0-beta03 | Cross-platform navigation (KMP compatible) |
| Networking | Ktor Client | 3.4.3 | HTTP calls — OkHttp on Android, Darwin on iOS |
| Local Database | SQLDelight | 2.1.0 | On-device storage — works on Android + iOS |
| Dependency Injection | Koin | 4.1.0 | Object creation and sharing across the app |
| Image + GIF Loading | Coil | 3.0.4 | Image loading — GIF via expect/actual |
| Serialization | Kotlinx Serialization | 1.8.1 | JSON parsing — required by Ktor and Supabase |
| Coroutines | Kotlinx Coroutines | 1.10.2 | Async operations (API calls, DB reads) |

---

## BACKEND — SUPABASE

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Platform | Supabase | Managed backend — no server to maintain |
| Database | PostgreSQL (via Supabase) | Relational data — users, plans, exercises |
| Authentication | Supabase Auth | JWT-based auth, Row Level Security |
| File Storage | Supabase Storage | GIF files for exercise library |
| Server Logic | Supabase Edge Functions (TypeScript/Deno) | AI API calls, generation limits, business logic |
| Client SDK | supabase-kt BOM 3.1.4 | Kotlin Multiplatform Supabase client |

---

## AI INTEGRATION

| Environment | Model | Model ID | Location |
|-------------|-------|----------|----------|
| Development | Gemini 3.5 Flash | gemini-3.5-flash | Edge Function only |
| Production | Claude Sonnet | claude-sonnet-4-20250514 | Edge Function only |

> ⚠️ AI API keys NEVER go into the app. They live in Supabase Edge Function secrets ONLY.

---

## DEVELOPMENT TOOLS

| Tool | Version | Purpose |
|------|---------|---------|
| Android Studio | Panda 4 (2025.3.4) | IDE — compile, run, emulate |
| Gemini Agent Mode | Built into Android Studio | AI-assisted code writing in IDE |
| Antigravity CLI | Latest | Agent orchestration from terminal |
| Supabase CLI | 2.x.x | Edge Function development and deployment |
| Deno | 2.x.x | Edge Function runtime (local testing) |
| Git | 2.x.x | Version control |
| Node.js | 22.x.x LTS | Supabase CLI dependency |

---

## PLATFORM TARGETS

| Platform | Status | Notes |
|----------|--------|-------|
| Android | ✅ Primary | Min SDK API 26 (Android 8.0) |
| iOS | ⏳ Deferred | Architecture ready — needs Mac to build |

---

## ENVIRONMENT PROJECTS

| Environment | Supabase Project Name | When Used |
|-------------|----------------------|-----------|
| Development | gujarati-fitness-dev | All development and testing |
| Production | gujarati-fitness-prod | Real users — created before launch only |

---

## DEPENDENCY FILE LOCATION

```
C:\Projects\GujaratiFitnessApp\gradle\libs.versions.toml
```

Full verified content of libs.versions.toml is in LOCAL_SETUP.md.

---

## WHY EACH MAJOR DECISION WAS MADE

**Compose Multiplatform over Flutter:**
Switching to Kotlin ecosystem for long-term control, native Android integration, and Gemini tooling compatibility. CMP iOS is stable since 1.8.0.

**Supabase over Firebase:**
The app's data is relational — users, influencer slots, permissions, plan ownership. PostgreSQL with Row Level Security models this correctly. Firebase's NoSQL structure would require complex workarounds.

**Ktor over Retrofit:**
Retrofit does not support iOS in KMP. Ktor is the official JetBrains KMP networking library.

**SQLDelight over Room:**
Room does not support iOS in KMP. SQLDelight generates type-safe Kotlin from SQL and works on both platforms.

**Koin over Hilt:**
Hilt does not support iOS in KMP. Koin is lightweight and fully KMP compatible.

**Voyager over Jetpack Navigation:**
Jetpack Navigation has known issues on CMP iOS. Voyager is purpose-built for CMP and stable in production.

**Gemini 3.5 Flash for development:**
Free tier API key. Swapping to Claude in production is a 5-line change in the Edge Function — app code is unaffected.

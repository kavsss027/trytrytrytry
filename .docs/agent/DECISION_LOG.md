# DECISION_LOG.md — GUJARATI FITNESS APP
> Every technical decision made in this project is logged here with the date, reasoning, and alternatives rejected.
> If you ever wonder "why did we do it this way" — the answer is here.

---

## DECISION 001 — Platform: Compose Multiplatform
**Date:** 2026-05-25
**Decision:** Use Compose Multiplatform (CMP) targeting Android + iOS
**Reasoning:** Developer wants cross-platform experience from one Kotlin codebase. CMP iOS is now stable since 1.8.0. Android is primary target (95%+ market share in India).
**Alternatives Rejected:**
- Flutter: Was original choice — switched to Kotlin ecosystem for native Android tooling and Gemini integration
- KMP with Native UI: Requires two separate UI codebases (Compose + SwiftUI) — too complex for one developer
**Status:** LOCKED

---

## DECISION 002 — iOS Deferred Until Mac Available
**Date:** 2026-05-25
**Decision:** Build and test Android only. iOS architecture is kept ready in codebase.
**Reasoning:** Developer has Windows 11 only. iOS requires Xcode which only runs on macOS.
**Impact:** All expect/actual implementations must be written for both platforms but only Android is testable now.
**Status:** LOCKED — revisit when Mac is available

---

## DECISION 003 — Backend: Supabase
**Date:** 2026-05-25
**Decision:** Use Supabase (PostgreSQL + Auth + Storage + Edge Functions)
**Reasoning:** App data is relational (users, influencer slots, permissions). Supabase gives managed PostgreSQL, auth, and serverless functions with no server maintenance. Perfect for a backend-beginner developer.
**Alternatives Rejected:**
- Firebase: NoSQL is wrong for relational permission structure
- Custom Node.js backend: Adds 3+ months of setup work for beginner
**Status:** LOCKED

---

## DECISION 004 — AI: Gemini 3.5 Flash (Dev) → Claude Sonnet (Prod)
**Date:** 2026-05-25
**Decision:** Use gemini-3.5-flash in Edge Functions for development. Swap to claude-sonnet-4-20250514 for production.
**Reasoning:** Developer has Gemini free tier API key. Swap is a 5-line change in Edge Function — app code is unaffected.
**Critical Rule:** AI API is NEVER called from Kotlin code. Only from Supabase Edge Functions.
**Status:** LOCKED

---

## DECISION 005 — Navigation: Voyager 1.1.0-beta03
**Date:** 2026-05-25
**Decision:** Use Voyager for CMP navigation
**Reasoning:** Jetpack Navigation has known issues on CMP iOS. Voyager is purpose-built for CMP and production-proven.
**Risk Accepted:** Voyager is in beta. Version pinned — never auto-upgrade.
**Status:** LOCKED

---

## DECISION 006 — Networking: Ktor Client 3.4.3
**Date:** 2026-05-25
**Decision:** Use Ktor Client for all HTTP calls
**Reasoning:** Retrofit does not support iOS in KMP. Ktor is the official JetBrains KMP networking library.
**Status:** LOCKED

---

## DECISION 007 — Local Database: SQLDelight 2.1.0
**Date:** 2026-05-25
**Decision:** Use SQLDelight for on-device storage
**Reasoning:** Room does not support iOS in KMP. SQLDelight generates type-safe Kotlin from SQL and works on both platforms.
**iOS Note:** Requires `-lsqlite3` linker flag in Xcode when iOS build begins.
**Status:** LOCKED

---

## DECISION 008 — DI: Koin 4.1.0
**Date:** 2026-05-25
**Decision:** Use Koin for dependency injection
**Reasoning:** Hilt does not support iOS in KMP. Koin is lightweight and fully KMP compatible.
**Status:** LOCKED

---

## DECISION 009 — GIF Loading: Coil 3.0.4 with expect/actual
**Date:** 2026-05-25
**Decision:** Use Coil 3.0.4 for images. GIF support via expect/actual (Coil GifDecoder on Android, UIKit on iOS)
**Reasoning:** coil-gif is Android-only. iOS requires native implementation. expect/actual is the correct KMP pattern.
**Risk:** iOS GIF implementation requires testing on real device or simulator. Deferred until Mac available.
**Status:** LOCKED

---

## DECISION 010 — Min SDK: API 26 (Android 8.0)
**Date:** 2026-05-25
**Decision:** Minimum Android SDK is API 26
**Reasoning:** supabase-kt requires minimum API 26. Covers 98%+ of active Android devices as of 2026.
**Status:** LOCKED

---

## DECISION 011 — Development Tools: Antigravity CLI + Android Studio Agent Mode
**Date:** 2026-05-25
**Decision:** Use Antigravity CLI for agent-driven development. Android Studio for compilation and emulator.
**Reasoning:** Antigravity CLI announced at Google I/O 2026 — purpose-built for agent-first Android development. Android Studio Agent Mode (Gemini) for in-IDE tasks.
**Workflow:** Antigravity writes code → Android Studio compiles and runs → repeat.
**Status:** LOCKED

---

## DECISION 012 — Supabase Region: Singapore
**Date:** 2026-05-25
**Decision:** Host Supabase project in Singapore region
**Reasoning:** Closest region to India with good latency. Target users are in India.
**Status:** LOCKED

---

## OPEN DECISIONS (Not Yet Made)

| # | Decision Needed | Target Phase |
|---|----------------|--------------|
| D013 | Exact free tier generation limit per month | Phase 4 |
| D014 | Exact premium tier generation limit per month | Phase 4 |
| D015 | Workout + Diet generation limits: shared pool or separate? | Phase 4 |
| D016 | GIF sourcing strategy (create, license, scrape?) | Phase 5 |
| D017 | App name | Later |
| D018 | Business model and pricing | V2 |

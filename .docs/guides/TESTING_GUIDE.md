# TESTING_GUIDE.md — GUJARATI FITNESS APP
> Manual test checklist for every feature.
> Run through this before marking any feature complete.
> Run the full checklist before Phase 8 (pre-launch).
> Log results in PROJECT_STATUS.md → Section 8 (Test Results Log).

---

## HOW TO LOG TEST RESULTS

After each test session, paste results into PROJECT_STATUS.md in this format:

```
[2026-05-25] | [Exercise Library] | [Manual Android] | ✅ 8/8 passed
[2026-05-25] | [Workout Generator] | [Manual Android] | ❌ 6/8 passed — EC-003 not handled, EC-014 not handled
```

---

## HOW TO RUN EDGE FUNCTION TESTS

```bash
cd C:\Projects\GujaratiFitnessApp

# Start local Supabase
supabase start

# Serve functions locally
supabase functions serve --env-file supabase/.env.local

# In a second terminal — test each function:
curl -X POST http://localhost:54321/functions/v1/check-generation-limit \
  -H "Authorization: Bearer [test_user_jwt]" \
  -H "Content-Type: application/json" \
  -d '{"feature": "workout_plan"}'

curl -X POST http://localhost:54321/functions/v1/generate-workout-plan \
  -H "Authorization: Bearer [test_user_jwt]" \
  -H "Content-Type: application/json" \
  -d '{
    "fitness_level": "beginner",
    "goal": "muscle_gain",
    "days_per_week": 4,
    "session_duration_minutes": 60,
    "available_equipment": ["dumbbells"],
    "injuries_limitations": null,
    "imbalance_context": null
  }'
```

---

## TEST SUITE 1 — AUTHENTICATION

Run before testing any feature.

```
TEST-AUTH-001 □ Register with new email — account created, onboarding shows
TEST-AUTH-002 □ Register with existing email — error: "Email already in use"
TEST-AUTH-003 □ Login with correct credentials — navigates to home
TEST-AUTH-004 □ Login with wrong password — error: "Invalid email or password"
TEST-AUTH-005 □ Login with unregistered email — error: "Invalid email or password"
TEST-AUTH-006 □ App restart after login — stays logged in, skips login screen
TEST-AUTH-007 □ Logout — returns to login screen, all cached data cleared
TEST-AUTH-008 □ Token expiry simulation — re-login prompt shown (EC-009)
```

---

## TEST SUITE 2 — ONBOARDING

```
TEST-ONB-001 □ New user sees onboarding after first login
TEST-ONB-002 □ Select "Complete Beginner" — beginner path shown
TEST-ONB-003 □ Select "Intermediate" — skips extra beginner screen
TEST-ONB-004 □ Full name saved — visible on profile after onboarding
TEST-ONB-005 □ Fitness level saved — exercise content adjusts
TEST-ONB-006 □ Returning user — onboarding never shown again
TEST-ONB-007 □ Cannot skip onboarding — back button handled
```

---

## TEST SUITE 3 — EXERCISE LIBRARY

```
TEST-EX-001  □ Exercise list loads on screen open
TEST-EX-002  □ "All" tab shows all exercises
TEST-EX-003  □ Each muscle group tab filters correctly
TEST-EX-004  □ Tapping exercise card opens detail screen
TEST-EX-005  □ GIF plays automatically on detail screen
TEST-EX-006  □ GIF loops continuously
TEST-EX-007  □ Exercise name and description visible
TEST-EX-008  □ Back button returns to list at same scroll position
TEST-EX-009  □ Loading state shows during API call (test on slow connection)
TEST-EX-010  □ Error state + retry button when network off
TEST-EX-011  □ Empty state when muscle group has no exercises
TEST-EX-012  □ Cached exercises load when offline after first load
TEST-EX-013  □ Free user can access — confirmed
TEST-EX-014  □ Premium user can access — confirmed
```

---

## TEST SUITE 4 — MUSCLE IMBALANCE DETECTOR

```
TEST-IMB-001 □ Input screen shows all lift fields and training days
TEST-IMB-002 □ Form submits with partial data (only some fields filled)
TEST-IMB-003 □ Empty form shows validation error (EC-002)
TEST-IMB-004 □ Loading screen shows during analysis
TEST-IMB-005 □ Report shows balance score (0–100)
TEST-IMB-006 □ Report shows imbalance list with severity
TEST-IMB-007 □ Report shows strengths section
TEST-IMB-008 □ Report shows priority fixes
TEST-IMB-009 □ User WITH existing plan → "Update My Plan" button shown (EC-001)
TEST-IMB-010 □ User WITHOUT existing plan → "Create My Plan" button shown (EC-001)
TEST-IMB-011 □ Tapping "Update/Create Plan" → workout generator opens with pre-filled data
TEST-IMB-012 □ Tapping "Save Report Only" → report saved, stays on report screen
TEST-IMB-013 □ Report persists after app restart
TEST-IMB-014 □ Error state when network fails during analysis
```

---

## TEST SUITE 5 — WORKOUT PLAN GENERATOR

```
TEST-WRK-001 □ Generation count shows correctly before questionnaire
TEST-WRK-002 □ Questionnaire all steps complete correctly
TEST-WRK-003 □ Equipment multi-select works
TEST-WRK-004 □ Imbalance data pre-fills lift fields if report exists
TEST-WRK-005 □ Imbalance banner shown when data is pre-filled
TEST-WRK-006 □ Loading screen shows during generation
TEST-WRK-007 □ Generated plan shows title, days, exercises
TEST-WRK-008 □ Each exercise shows sets, reps, rest, notes
TEST-WRK-009 □ Plan persists after app restart
TEST-WRK-010 □ Generation count increments after successful generation
TEST-WRK-011 □ Free user at limit → cannot generate (EC-004)
TEST-WRK-012 □ Free user at limit → clear message shown with reset date
TEST-WRK-013 □ AI rate limited → friendly message, count not decremented (EC-003)
TEST-WRK-014 □ Network failure → friendly message, count not decremented
TEST-WRK-015 □ Back button during generation → disabled / warning shown (EC-014)
TEST-WRK-016 □ Timeout → friendly message, retry button (EC-005)
```

---

## TEST SUITE 6 — DIET PLAN GENERATOR

```
TEST-DIT-001 □ All questionnaire steps complete
TEST-DIT-002 □ Gujarati food toggle defaults to ON
TEST-DIT-003 □ Plan shows daily calories prominently
TEST-DIT-004 □ Plan shows macros (protein/carbs/fat in grams)
TEST-DIT-005 □ Plan shows meal-by-meal breakdown
TEST-DIT-006 □ Each meal shows food list and calorie count
TEST-DIT-007 □ Hydration target shown
TEST-DIT-008 □ Plan persists after app restart
TEST-DIT-009 □ Generation limit enforced same as workout plan
TEST-DIT-010 □ All error states handled (mirrors workout generator)
```

---

## TEST SUITE 7 — S-TIER SECTION

```
TEST-STR-001 □ S-TIER tab NOT visible to free users — confirmed
TEST-STR-002 □ S-TIER tab visible to premium users — confirmed
TEST-STR-003 □ Global S-TIER exercises visible to all premium users
TEST-STR-004 □ Influencer-specific exercises visible only to that influencer's users
TEST-STR-005 □ Exercises from other influencers NOT visible — confirmed
TEST-STR-006 □ GIFs play correctly on S-TIER exercises
TEST-STR-007 □ Loading and error states handled
```

---

## TEST SUITE 8 — INFLUENCER SYSTEM (USER SIDE)

```
TEST-INF-001 □ Influencer list loads with slot availability
TEST-INF-002 □ Full influencer shows "Group Full" — button disabled (EC-006)
TEST-INF-003 □ Available influencer shows "Request to Join" button
TEST-INF-004 □ Confirmation screen shows before submitting request
TEST-INF-005 □ Request submitted — status shows "Pending"
TEST-INF-006 □ Duplicate request prevented — "Pending" badge shown instead (EC-008)
TEST-INF-007 □ After 7 days pending — reminder message + cancel option shown (EC-007)
TEST-INF-008 □ Cancel request works — user can request different influencer
TEST-INF-009 □ After approval — user_type upgrades to premium instantly
TEST-INF-010 □ After approval — S-TIER section becomes visible
TEST-INF-011 □ After rejection — clear rejection message shown
TEST-INF-012 □ After rejection — user can request another influencer
```

---

## TEST SUITE 9 — INFLUENCER ADMIN PANEL

```
TEST-ADM-001 □ Admin panel only visible to influencer + owner user types
TEST-ADM-002 □ Dashboard shows correct member count and slot usage
TEST-ADM-003 □ Pending requests list loads
TEST-ADM-004 □ Approve request — user_type updates to premium
TEST-ADM-005 □ Approve request — used_slots increments correctly
TEST-ADM-006 □ Reject request — user notified
TEST-ADM-007 □ Cannot approve when at max_slots — button disabled
TEST-ADM-008 □ Add S-TIER exercise — appears for influencer's users only
TEST-ADM-009 □ Toggle exercise inactive — disappears from user view
```

---

## TEST SUITE 10 — EDGE CASES (Phase 7 Sprint)

```
TEST-EC-001  □ EC-001: No existing plan + imbalance report (Create Plan flow)
TEST-EC-002  □ EC-002: Empty imbalance form validation
TEST-EC-003  □ EC-003: AI rate limit error message
TEST-EC-004  □ EC-004: Generation limit check always fresh
TEST-EC-005  □ EC-005: Edge Function timeout → friendly message
TEST-EC-006  □ EC-006: Full influencer slot → button disabled
TEST-EC-007  □ EC-007: 7+ days pending request → cancel option
TEST-EC-008  □ EC-008: Duplicate join request prevention
TEST-EC-009  □ EC-009: Token expiry → re-login prompt
TEST-EC-010  □ EC-010: Offline first launch → offline message
TEST-EC-011  □ EC-011: Empty exercise library → empty state shown
TEST-EC-012  □ EC-012: GIF load failure → placeholder shown (no crash)
TEST-EC-013  □ EC-013: Malformed AI JSON → error message (no crash)
TEST-EC-014  □ EC-014: Back button during generation → disabled
TEST-EC-015  □ EC-015: App killed during generation → plan found on restart
```

---

## PRE-LAUNCH SECURITY CHECKLIST

Run these before switching to production Supabase project:

```
□ git grep "GEMINI_API_KEY" — should return zero results in .kt or .ts files
□ git grep "SUPABASE_ANON_KEY" — should only appear in local.properties (not committed)
□ git grep "eyJ" — JWT pattern — should return zero results in committed files
□ Supabase RLS enabled on ALL tables — verify in dashboard
□ service_role key never used in app — verify in all .kt files
□ All Edge Function secrets set in Supabase Dashboard (not in code)
□ local.properties not in git history — git log --all -- local.properties
□ supabase/.env.local not in git history
□ Release APK tested on physical Android device
□ App does not crash on first cold start
□ App does not crash when offline
```

---

## EDGE FUNCTION UNIT TESTS

Run these via Supabase CLI after Phase 5:

```bash
# Test 1: check-generation-limit returns correct structure
curl -X POST http://localhost:54321/functions/v1/check-generation-limit \
  -H "Authorization: Bearer [jwt]" \
  -d '{"feature":"workout_plan"}'
# Expected: { "can_generate": true, "used": 0, "limit": 3, "remaining": 3 }

# Test 2: generate-workout-plan returns valid plan structure
# Expected: { "plan": { "title": "...", "days": [...] } }

# Test 3: detect-muscle-imbalance returns valid report structure
# Expected: { "report": { "overall_balance_score": ..., "imbalances": [...] } }

# Test 4: Rate limit enforcement
# Call generate-workout-plan 4 times as free user
# 4th call expected: { "error": "LIMIT_REACHED" } with status 429

# Test 5: Missing auth header
# Call any endpoint without Authorization header
# Expected: 401 Unauthorized
```

---

## REPORTING TEMPLATE FOR PROJECT_STATUS.md

After every test session, copy this into PROJECT_STATUS.md Section 8:

```
[DATE] | [FEATURE NAME] | [Android Emulator / Physical Device / Edge Function]
PASSED: X/Y tests
FAILED:
  - TEST-XXX-000: [what happened]
  - TEST-XXX-000: [what happened]
EDGE CASES VERIFIED: EC-00X, EC-00X
NEXT ACTION: [what needs fixing]
```

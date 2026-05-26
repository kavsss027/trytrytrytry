# EDGE_CASES.md — GUJARATI FITNESS APP
> Every known edge case and failure path in the app.
> Each one must be handled before Phase 8 begins.
> Status column updated as each case is implemented and tested.

---

## HOW TO USE THIS FILE

Every edge case has:
- A clear description of what can go wrong
- Which screen or feature it affects
- The exact user-facing message or behaviour
- The code-level handling strategy
- A status checkbox

When an edge case is handled and tested → mark it ✅ and log the date.

---

## CATEGORY 1 — MUSCLE IMBALANCE DETECTOR

### EC-001 — Imbalance detector run before any workout plan exists
**Status:** ⏳ Not yet implemented
**Phase:** Detected in Phase 1 — implement in Phase 7

**What goes wrong:**
User completes the imbalance detector. Post-report prompt says "Update your existing plan" — but the user has never generated a plan. The update flow breaks or shows a dead end.

**Correct behaviour:**
- After generating the report, check `workout_plans` table for active plan
- If active plan exists → show "Update My Plan" button
- If no active plan → show "Create My Plan" button (different copy, same destination)

**Code strategy:**
```kotlin
// ImbalanceViewModel.kt
fun loadPostReportAction() {
    viewModelScope.launch {
        val activePlan = planRepository.getActivePlan(currentUserId)
        _hasExistingPlan.value = activePlan != null
    }
}

// ImbalanceReportScreen.kt
if (hasExistingPlan) {
    AppButton("Update My Plan") { navigateToWorkoutGenerator(withImbalanceContext = true) }
} else {
    AppButton("Create My Plan") { navigateToWorkoutGenerator(withImbalanceContext = true) }
}
AppButton("Save Report Only", style = secondary) { viewModel.saveReportOnly() }
```

---

### EC-002 — User submits imbalance form with all fields empty
**Status:** ⏳ Not yet implemented

**What goes wrong:**
User taps "Analyse My Imbalances" without entering any data. Edge Function receives empty payload and either crashes or returns meaningless results.

**Correct behaviour:**
- Require at least ONE lift value OR at least ONE training frequency value before allowing submission
- Show inline validation: "Please enter at least one lift or training frequency to get an analysis"

**Code strategy:**
```kotlin
fun validateImbalanceInput(): Boolean {
    return benchPress != null || squatMax != null ||
           deadliftMax != null || overheadPress != null ||
           pullupMax != null || trainingDays.values.any { it > 0 }
}
```

---

## CATEGORY 2 — AI GENERATION LIMITS

### EC-003 — AI API rate limit exceeded (too many users simultaneously)
**Status:** ⏳ Not yet implemented
**Phase:** Detected in Phase 1 — implement in Phase 7

**What goes wrong:**
Multiple users generate plans at the same time. Gemini/Claude API returns a 429 rate limit error. The app shows a raw error or crashes.

**Correct behaviour:**
User sees: *"Our plan generator is busy right now. Please try again in a few minutes."*
- Retry button shown
- Generation count is NOT decremented (user didn't waste a generation)
- Error logged for monitoring

**Code strategy:**
```typescript
// In Edge Function — catch 429 specifically
if (aiResponse.status === 429) {
  return new Response(
    JSON.stringify({ error: "AI_RATE_LIMITED", message: "Our plan generator is busy right now. Please try again in a few minutes." }),
    { status: 429, headers }
  );
}
```

```kotlin
// In Kotlin — map to user message
is WorkoutUiState.Error -> {
    val displayMessage = when (state.errorCode) {
        "AI_RATE_LIMITED" -> "Our plan generator is busy right now. Please try again in a few minutes."
        "LIMIT_REACHED"   -> "You've used all your plan generations this month."
        else              -> "Something went wrong. Please try again."
    }
    ErrorScreen(message = displayMessage, onRetry = { viewModel.generatePlan(questionnaire) })
}
```

---

### EC-004 — User reaches generation limit mid-session
**Status:** ⏳ Not yet implemented

**What goes wrong:**
User has 1 generation left. They generate a plan. They immediately try to generate another. The limit check is already cached from earlier in the session showing they had 1 remaining.

**Correct behaviour:**
- Always call check-generation-limit fresh before EVERY generation attempt
- Never cache the limit check result
- After a successful generation, refresh the usage counter shown on screen

---

### EC-005 — Edge Function timeout (AI takes too long)
**Status:** ⏳ Not yet implemented

**What goes wrong:**
AI API call takes more than 30 seconds. Supabase Edge Function times out. App gets a 504 or network error with no useful message.

**Correct behaviour:**
User sees: *"This is taking longer than usual. Please try again."*
- Generation count NOT decremented
- Show retry button

**Code strategy:**
```typescript
// Set explicit timeout in Edge Function
const controller = new AbortController();
const timeout = setTimeout(() => controller.abort(), 25000); // 25s — before Supabase's 30s limit

try {
    const response = await fetch(geminiUrl, { signal: controller.signal, ...options });
    clearTimeout(timeout);
} catch (e) {
    if (e.name === 'AbortError') {
        return new Response(JSON.stringify({ error: "TIMEOUT" }), { status: 504, headers });
    }
}
```

---

## CATEGORY 3 — INFLUENCER SYSTEM

### EC-006 — User tries to join full influencer group
**Status:** ⏳ Not yet implemented
**Phase:** Detected in Phase 1 — implement in Phase 7

**What goes wrong:**
User taps "Request to Join" on an influencer whose slots are full. Request sits in pending state permanently — influencer cannot approve more users than max_slots allows.

**Correct behaviour:**
- Before showing "Request to Join" button, check `used_slots < max_slots`
- If full → show "This group is full" text, button is disabled and greyed out
- This check is done at screen load, not just on tap

**Code strategy:**
```kotlin
// InfluencerCard.kt
if (influencer.usedSlots >= influencer.maxSlots) {
    Text("This group is full", color = MaterialTheme.colorScheme.error)
    AppButton("Group Full", enabled = false)
} else {
    AppButton("Request to Join") { viewModel.submitJoinRequest(influencer.id) }
}
```

---

### EC-007 — Influencer never responds to join request
**Status:** ⏳ Not yet implemented
**Phase:** Detected in Phase 1 — implement in Phase 7

**What goes wrong:**
User submits a join request. The influencer ignores it. User is stuck in "pending" forever with no feedback.

**Correct behaviour:**
- Show request date on the status screen: "Requested on [date]"
- After 7 days pending → show message: "Your request has been pending for a while. The influencer has been reminded."
- Supabase scheduled function (or client-side check) sends reminder after 7 days
- User can cancel their pending request and request to join a different influencer

**Code strategy:**
```kotlin
// JoinRequestStatusScreen.kt
val daysPending = calculateDaysBetween(request.requestedAt, now())
if (daysPending >= 7 && request.status == JoinRequestStatus.PENDING) {
    Text("Your request has been pending for $daysPending days. You can cancel and try another group.")
    AppButton("Cancel Request", style = destructive) { viewModel.cancelRequest(request.id) }
}
```

---

### EC-008 — User already has a pending request and tries to request again
**Status:** ⏳ Not yet implemented

**What goes wrong:**
User submits a join request, then navigates away and comes back, and taps "Request to Join" again. Database has a UNIQUE constraint but the app shows a confusing error instead of a clear message.

**Correct behaviour:**
- On "Find Influencer" screen load, fetch existing pending request for this user
- If pending request exists for an influencer → show "Request Pending" on that card
- "Request to Join" button replaced with "Pending" status badge

---

## CATEGORY 4 — AUTHENTICATION

### EC-009 — Auth token expires mid-session
**Status:** ⏳ Not yet implemented

**What goes wrong:**
User is using the app. Their Supabase JWT expires (default: 1 hour). The next API call returns a 401. App crashes or shows a cryptic error.

**Correct behaviour:**
- Supabase auth client handles token refresh automatically — verify this is configured
- If refresh fails (user logs out from another device) → navigate to Login screen
- Show message: "Your session has expired. Please log in again."

**Code strategy:**
```kotlin
// In SupabaseClient.kt setup
val supabase = createSupabaseClient(url, key) {
    install(Auth) {
        autoRefreshToken = true  // Supabase handles refresh
        alwaysAutoRefresh = true
    }
}

// Global error handler in repositories
catch (e: UnauthorizedException) {
    // Navigate to login — supabase-kt throws this on 401
    authEventBus.emit(AuthEvent.SessionExpired)
}
```

---

### EC-010 — User opens app with no internet on first launch
**Status:** ⏳ Not yet implemented

**What goes wrong:**
App tries to check auth state, connect to Supabase, and load the exercise library — all require network. Everything fails with confusing errors.

**Correct behaviour:**
- Check network connectivity at app startup
- If offline and user was previously logged in → show cached data where available
- If offline and no cached data → show "You're offline. Connect to the internet to use the app."
- Exercise library: show cached exercises if available

---

## CATEGORY 5 — DATA & CONTENT

### EC-011 — Exercise library is empty (no exercises added yet)
**Status:** ⏳ Not yet implemented

**What goes wrong:**
App owner has not added any exercises yet. User opens exercise library and sees a blank screen or crash.

**Correct behaviour:**
- Show EmptyState component: "No exercises available yet. Check back soon!"
- This is a valid state — not an error

---

### EC-012 — GIF fails to load
**Status:** ⏳ Not yet implemented

**What goes wrong:**
Supabase Storage has a temporary issue. GIF URL is valid but request fails. User sees a broken image.

**Correct behaviour:**
- Coil handles this with an error placeholder automatically if configured
- Show a grey placeholder box with exercise name text below
- No error message needed — silent graceful fallback

**Code strategy:**
```kotlin
AsyncImage(
    model = exercise.gifUrl,
    contentDescription = exercise.name,
    error = painterResource(Res.drawable.placeholder_exercise),
    placeholder = painterResource(Res.drawable.placeholder_exercise),
    modifier = modifier
)
```

---

### EC-013 — Generated plan data is malformed JSON from AI
**Status:** ⏳ Not yet implemented

**What goes wrong:**
AI returns valid JSON but with unexpected structure. Kotlin serialization throws an exception. App crashes on plan display screen.

**Correct behaviour:**
- Wrap plan parsing in try/catch in repository
- If parsing fails → show error: "We couldn't display your plan. Please try generating a new one."
- Log the malformed response for debugging

---

## CATEGORY 6 — PLATFORM SPECIFIC

### EC-014 — User navigates back during AI generation
**Status:** ⏳ Not yet implemented

**What goes wrong:**
User taps back while the AI is generating their plan. Generation continues in background. Plan is saved to DB. User returns to find an unexpected result. Or generation is cancelled halfway and count is decremented incorrectly.

**Correct behaviour:**
- Disable back navigation during active generation (intercept back press)
- Show: "Please wait while your plan is being generated"
- Generation count only decremented AFTER successful API response — never on start

---

### EC-015 — App killed during active generation
**Status:** ⏳ Not yet implemented

**What goes wrong:**
User force-closes the app while AI is generating. Edge Function completes and saves plan to DB. User reopens app and plan exists but they never saw the success screen.

**Correct behaviour:**
- On app startup, check for any active/unviewed plans
- If found → navigate directly to Plan Display Screen
- This is an edge case — acceptable to handle in Phase 7

---

## EDGE CASES IMPLEMENTATION CHECKLIST

```
Phase 5 (Backend):
□ EC-003 — AI rate limit error code in Edge Functions
□ EC-005 — Edge Function timeout handling

Phase 6 (Frontend):
□ EC-001 — Imbalance post-report action (has plan / no plan)
□ EC-002 — Empty imbalance form validation
□ EC-006 — Full influencer slot UI
□ EC-007 — Pending request timeout message + cancel option
□ EC-008 — Duplicate join request prevention
□ EC-011 — Empty exercise library state
□ EC-012 — GIF load failure placeholder
□ EC-014 — Back press during generation disabled

Phase 7 (Edge Cases Sprint):
□ EC-004 — Generation limit cache freshness
□ EC-009 — Token expiry mid-session
□ EC-010 — Offline first launch
□ EC-013 — Malformed AI JSON response
□ EC-015 — App killed during generation
```

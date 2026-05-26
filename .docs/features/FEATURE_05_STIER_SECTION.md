# FEATURE 05 — S-TIER EXERCISE SECTION
> Build this feature SEVENTH (after generators — premium gating needed)
> Complexity: Low-Medium — premium gate + owner/influencer content management
> Depends on: Auth, premium user check, stier_exercises table

---

## WHAT THIS FEATURE DOES

A curated list of viral and trending exercises reviewed for safety and effectiveness. Completely hidden from free users. Global list managed by app owner. Each influencer can add their own exercises on top of the global list for their users only.

---

## WHO CAN ACCESS

| User Type | Access |
|-----------|--------|
| Free | ❌ Completely hidden — section not visible |
| Premium | ✅ Sees global S-TIER list + their influencer's exercises |
| Influencer | ✅ Sees global + their own additions |
| Owner | ✅ Sees everything |

---

## SCREENS

### Screen 1 — S-TIER List Screen (Premium Only)

**What premium user sees:**
- "S-TIER Exercises" heading with star badge
- Filter tabs: All / Global / [Influencer Name]'s Picks
- Exercise cards with GIF preview, name, muscle group
- "Why S-TIER?" badge or tag on each exercise (safety-reviewed, trending)

**What free user sees:**
- Nothing — this tab/section is completely hidden from navigation
- The tab does not appear in free user's bottom nav

**Premium gate logic:**
```kotlin
// In navigation setup
if (user.userType == UserType.FREE) {
    // S-TIER tab not added to bottom nav
    // Route is not registered for free users
}
```

---

### Screen 2 — S-TIER Exercise Detail Screen

Same structure as regular exercise detail — GIF, name, description, muscle group.

---

## ANTIGRAVITY CLI PROMPT

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/DATABASE_SCHEMA.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_05_STIER_SECTION.md \
  --prompt "Build the S-TIER Exercise Section.
  1. STierRemoteSource.kt — fetch stier_exercises with RLS handling both global and influencer-specific
  2. STierRepository.kt
  3. GetSTierExercisesUseCase.kt
  4. STierViewModel.kt
  5. STierListScreen.kt — hidden completely from free users
  6. Premium gate in navigation — S-TIER tab only appears for premium/influencer/owner users
  Use the RLS policy — Supabase will automatically filter to what the user can see."
```

---

## DONE CRITERIA

```
□ S-TIER section completely invisible to free users
□ Premium users see global list + influencer's exercises
□ Navigation tab only appears for premium/influencer/owner
□ GIFs load correctly
□ Influencer's exercises only visible to their own group users
```
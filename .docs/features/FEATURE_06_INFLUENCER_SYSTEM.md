# FEATURE 06 — INFLUENCER SYSTEM & ADMIN PANEL
> Build this feature LAST — most complex, most dependencies
> Complexity: High — multi-role logic, approval flows, slot management
> Depends on: All other features complete, influencers table, join_requests table

---

## WHAT THIS FEATURE DOES

Multiple influencers can exist on the platform. Each has their own admin panel to manage their group. Users request to join influencer groups. Influencer approves or rejects. Approved users become premium and get access to S-TIER section.

---

## USER-FACING SCREENS

### Screen 1 — Find Influencer Screen

**What the user sees:**
- List of active influencers on the platform
- Each card shows: influencer name, slot availability
- "Request to Join" button per influencer
- If slots full: "This group is full" — button disabled
- If already requested: "Request Pending"
- If already a member: "You're a member"

---

### Screen 2 — Join Request Confirmation Screen

- "Are you sure you want to request to join [Influencer Name]'s group?"
- "Your request will be reviewed by the influencer."
- "Confirm Request" / "Cancel" buttons

---

### Screen 3 — Request Status Screen

- Shows current join request status: Pending / Approved / Rejected
- If approved: Congratulations message, premium features unlocked
- If rejected: "Your request was not approved. You can request to join another group."
- If pending too long: "Your request is still pending. The influencer has been notified."

---

## INFLUENCER ADMIN PANEL SCREENS

### Admin Screen 1 — Dashboard

- Total members / slots used
- Pending requests count
- Quick actions: View Requests, Manage S-TIER

### Admin Screen 2 — Pending Requests List

- List of pending join requests
- Each row: user name, request date
- "Approve" and "Reject" buttons per request
- Approve action:
  1. Updates join_requests status to 'approved'
  2. Updates users.user_type to 'premium'
  3. Sets users.influencer_id
  4. Increments influencers.used_slots

### Admin Screen 3 — Manage S-TIER Exercises

- Shows influencer's own S-TIER additions
- "Add Exercise" button (name, GIF URL, muscle group)
- Toggle active/inactive per exercise

---

## SLOT FULL EDGE CASE

```kotlin
// In InfluencerViewModel before submitting join request
fun checkSlotAvailability(influencerId: String): Boolean {
    val influencer = influencerRepository.getInfluencer(influencerId)
    return influencer.usedSlots < influencer.maxSlots
}

// Show "This group is full" if false
// Disable request button if false
```

---

## ANTIGRAVITY CLI PROMPT

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/DATABASE_SCHEMA.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_06_INFLUENCER_SYSTEM.md \
  --prompt "Build the Influencer System.
  User side:
  1. InfluencerRemoteSource.kt
  2. InfluencerRepository.kt
  3. ManageInfluencerGroupUseCase.kt
  4. InfluencerViewModel.kt
  5. FindInfluencerScreen.kt with slot availability check
  6. JoinRequestScreen.kt and StatusScreen.kt

  Admin panel side:
  7. AdminDashboardScreen.kt (influencer_type users only)
  8. PendingRequestsScreen.kt with approve/reject actions
  9. ManageSTierScreen.kt
  Approval flow must update user_type to premium and increment used_slots atomically."
```

---

## DONE CRITERIA

```
□ Users can see available influencers
□ Slot availability shown correctly
□ Join request submitted and status tracked
□ Influencer sees pending requests in admin panel
□ Approve action makes user premium instantly
□ Reject action shown to user with clear message
□ Slot count increments correctly on approval
□ Full slots prevent new requests
□ Influencer can add S-TIER exercises for their group
```
# FEATURE 01 — EXERCISE LIBRARY
> Build this feature THIRD (after Auth and Onboarding)
> Complexity: Low — no AI, no payment logic, good learning feature
> Depends on: Auth system complete, DATABASE_SCHEMA.md tables created

---

## WHAT THIS FEATURE DOES

Shows a browsable library of exercises with GIF demonstrations.
Users can filter by muscle group and tap an exercise to see full details.
Only the app owner can add or remove exercises (done via Supabase dashboard directly — no UI needed for V1).

---

## WHO CAN ACCESS

| User Type | Access |
|-----------|--------|
| Free | ✅ Full access |
| Premium | ✅ Full access |
| Influencer | ✅ Full access |
| Owner | ✅ Full access |

---

## SCREENS IN THIS FEATURE

### Screen 1 — Exercise List Screen

**Route:** `ExerciseListScreen`

**What the user sees:**
- Tab bar at top with muscle group filters: All, Chest, Back, Shoulders, Biceps, Triceps, Legs, Glutes, Core, Full Body
- Grid or list of exercise cards below the tabs
- Each card shows: exercise name, thumbnail of GIF, difficulty badge
- Tapping a card navigates to Exercise Detail Screen

**States to handle:**
```
Loading  → Show shimmer placeholder cards
Success  → Show exercise grid/list
Empty    → "No exercises found for this muscle group yet"
Error    → "Failed to load exercises" + Retry button
```

**ViewModel actions:**
```kotlin
fun loadExercises(muscleGroup: MuscleGroup?)  // null = load all
fun onMuscleGroupSelected(group: MuscleGroup)
```

**API call:**
```kotlin
supabase.postgrest["exercises"]
    .select()
    .eq("muscle_group", muscleGroup)
    .eq("is_active", true)
    .order("name")
```

**Local caching:** Cache exercise list in SQLDelight after first load. Show cached data if network fails.

---

### Screen 2 — Exercise Detail Screen

**Route:** `ExerciseDetailScreen(exerciseId: String)`

**What the user sees:**
- Full-size GIF playing at top (autoplay, loop)
- Exercise name (large heading)
- Muscle group badge
- Difficulty badge
- Description / form cues
- Back button to return to list

**States to handle:**
```
Loading  → Show GIF placeholder + skeleton text
Success  → Show full exercise detail
Error    → "Failed to load exercise" + Back button
```

**Data source:** Exercise object passed from list screen via Voyager (no second API call needed if passed correctly).

---

## GIF IMPLEMENTATION — EXPECT/ACTUAL

This is the one technically complex piece of this feature.

### commonMain
```kotlin
// GifImage.kt in commonMain/components/
@Composable
expect fun GifImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)
```

### androidMain
```kotlin
// GifImage.android.kt
@Composable
actual fun GifImage(
    url: String,
    modifier: Modifier,
    contentDescription: String?
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(GifDecoder.Factory()) }
            .build()
    }
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
```

### iosMain
```kotlin
// GifImage.ios.kt
@Composable
actual fun GifImage(
    url: String,
    modifier: Modifier,
    contentDescription: String?
) {
    UIKitView(
        factory = {
            val imageView = UIImageView()
            imageView.contentMode = UIViewContentModeScaleAspectFit
            imageView.loadGifFromUrl(url)  // extension using NSData + UIImage.animatedImage
            imageView
        },
        modifier = modifier
    )
}
```

**⚠️ iOS GIF implementation must be tested as first priority when Mac is available.**

---

## SQLDELIGHT SCHEMA FOR LOCAL CACHE

```sql
-- In commonMain/sqldelight/com/gujaratifitness/app/Exercise.sq
CREATE TABLE ExerciseCache (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    muscle_group TEXT NOT NULL,
    gif_url TEXT NOT NULL,
    difficulty TEXT NOT NULL,
    cached_at INTEGER NOT NULL  -- Unix timestamp
);

-- Query: get exercises by muscle group
getByMuscleGroup:
SELECT * FROM ExerciseCache
WHERE muscle_group = :muscleGroup;

-- Query: insert or replace
upsertExercise:
INSERT OR REPLACE INTO ExerciseCache
VALUES (?, ?, ?, ?, ?, ?, ?);

-- Query: get all
getAllExercises:
SELECT * FROM ExerciseCache;
```

---

## ANTIGRAVITY CLI PROMPT FOR THIS FEATURE

```bash
antigravity run \
  --context .docs/MASTER_CONTEXT.md \
  --context .docs/contracts/DATABASE_SCHEMA.md \
  --context .docs/contracts/DATA_MODELS.md \
  --context .docs/features/FEATURE_01_EXERCISE_LIBRARY.md \
  --prompt "Build the Exercise Library feature exactly as specified in the feature doc.
  Start with:
  1. ExerciseCache SQLDelight schema
  2. ExerciseLocalSource.kt
  3. ExerciseRemoteSource.kt (Supabase postgrest call)
  4. ExerciseRepository.kt
  5. GetExercisesUseCase.kt
  6. ExerciseViewModel.kt with ExerciseUiState
  7. ExerciseListScreen.kt with muscle group tabs
  8. ExerciseDetailScreen.kt
  9. GifImage expect/actual (Android implementation first)
  Do NOT use Firebase, Room, Retrofit, or Hilt."
```

---

## DONE CRITERIA — THIS FEATURE IS COMPLETE WHEN

```
□ Exercise list loads from Supabase on first launch
□ Muscle group tabs filter correctly
□ GIF plays on exercise detail screen (Android)
□ Cached exercises load when offline
□ Loading state shows during API call
□ Error state shows with retry button when API fails
□ Empty state shows if no exercises in a muscle group
□ All 4 user types can access the feature
□ No hardcoded data — all from Supabase
```

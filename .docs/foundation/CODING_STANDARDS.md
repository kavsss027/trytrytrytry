# CODING_STANDARDS.md — GUJARATI FITNESS APP
> All code written for this project — by agents or humans — must follow these standards.
> Consistent code means agents can read and modify any file without confusion.

---

## NAMING CONVENTIONS

### Files
```
Screens:         LoginScreen.kt, ExerciseListScreen.kt
ViewModels:      LoginViewModel.kt, ExerciseViewModel.kt
UseCases:        GenerateWorkoutPlanUseCase.kt, GetExercisesUseCase.kt
Repositories:    ExerciseRepository.kt, PlanRepository.kt
Remote Sources:  ExerciseRemoteSource.kt
Local Sources:   ExerciseLocalSource.kt
Data Models:     User.kt, Exercise.kt, WorkoutPlan.kt
Components:      AppButton.kt, ExerciseCard.kt, GifImage.kt
Koin Modules:    AppModule.kt, DataModule.kt, DomainModule.kt
```

### Classes and Objects
```kotlin
class ExerciseViewModel : ViewModel()          // PascalCase
object AppRoutes                               // PascalCase
data class WorkoutPlan(...)                    // PascalCase
sealed class ExerciseUiState                   // PascalCase
interface ExerciseRepository                   // PascalCase — no "I" prefix
```

### Functions
```kotlin
fun generateWorkoutPlan()                      // camelCase
fun getExercisesByMuscleGroup()               // camelCase — descriptive
suspend fun fetchUserProfile()                 // suspend prefix for coroutines: no prefix needed, just mark suspend
```

### Variables and Properties
```kotlin
val exerciseList: List<Exercise>               // camelCase
var isLoading: Boolean = false                 // camelCase
private val _uiState = MutableStateFlow(...)   // underscore prefix for private mutable
val uiState = _uiState.asStateFlow()          // public exposed as immutable
```

### Constants
```kotlin
object AppConstants {
    const val MAX_FREE_PLAN_GENERATIONS = 3
    const val SUPABASE_STORAGE_BUCKET = "exercise-gifs"
    const val EDGE_FUNCTION_GENERATE_PLAN = "generate-workout-plan"
}
```

### Database Tables (Supabase/PostgreSQL)
```
users                    ← snake_case, plural
exercises                ← snake_case, plural
workout_plans            ← snake_case, plural, underscore separator
diet_plans
muscle_imbalance_reports
stier_exercises
influencers
influencer_join_requests
generation_usage
```

### Edge Functions (Supabase)
```
generate-workout-plan    ← kebab-case
generate-diet-plan
detect-muscle-imbalance
check-generation-limit
```

---

## KOTLIN CODE PATTERNS

### Every ViewModel follows this exact structure
```kotlin
class ExerciseViewModel(
    private val getExercisesUseCase: GetExercisesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    fun loadExercises(muscleGroup: String) {
        viewModelScope.launch {
            _uiState.value = ExerciseUiState.Loading
            try {
                val exercises = getExercisesUseCase.execute(muscleGroup)
                _uiState.value = ExerciseUiState.Success(exercises)
            } catch (e: Exception) {
                _uiState.value = ExerciseUiState.Error(
                    message = e.message ?: "Failed to load exercises"
                )
            }
        }
    }
}
```

### Every Screen follows this exact structure
```kotlin
@Composable
fun ExerciseListScreen(
    viewModel: ExerciseViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is ExerciseUiState.Idle    -> ExerciseIdleContent(onLoad = { viewModel.loadExercises("all") })
        is ExerciseUiState.Loading -> LoadingScreen()
        is ExerciseUiState.Success -> ExerciseListContent(exercises = state.exercises)
        is ExerciseUiState.Error   -> ErrorScreen(message = state.message, onRetry = { viewModel.loadExercises("all") })
    }
}
```

### Every UiState follows this exact structure
```kotlin
sealed class ExerciseUiState {
    object Idle : ExerciseUiState()
    object Loading : ExerciseUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}
```

### Every Repository follows this exact structure
```kotlin
class ExerciseRepositoryImpl(
    private val remoteSource: ExerciseRemoteSource,
    private val localSource: ExerciseLocalSource
) : ExerciseRepository {

    override suspend fun getExercises(muscleGroup: String): List<Exercise> {
        return try {
            val remote = remoteSource.fetchExercises(muscleGroup)
            localSource.cacheExercises(remote)
            remote
        } catch (e: Exception) {
            localSource.getCachedExercises(muscleGroup)
        }
    }
}
```

---

## COMPOSABLE RULES

```
✅ Every Composable must have a @Preview annotation for Android
✅ Every screen Composable takes a ViewModel parameter (injected via koinViewModel())
✅ No business logic inside Composables — only UI logic
✅ No direct database or API calls inside Composables — everything via ViewModel
✅ Every loading state shows the shared LoadingScreen() component
✅ Every error state shows the shared ErrorScreen() component with a retry action
✅ Every empty list state shows the shared EmptyState() component
```

---

## ERROR HANDLING RULES

```
✅ Every try/catch must map to a UiState.Error — never swallow exceptions silently
✅ Error messages shown to users must be human-readable — never show raw exception messages
✅ Network errors show: "Something went wrong. Please check your connection and try again."
✅ API limit errors show: "Our plan generator is busy right now. Please try again in a few minutes."
✅ Auth errors show: "Your session has expired. Please log in again."
✅ Every error screen must include a retry button
```

---

## EDGE FUNCTION (TYPESCRIPT) RULES

```typescript
// Every Edge Function follows this structure
import { serve } from "https://deno.land/std@0.177.0/http/server.ts";

serve(async (req: Request) => {
  // 1. CORS headers always first
  const headers = {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  };

  // 2. Handle preflight
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers });
  }

  try {
    // 3. Parse and validate input
    const body = await req.json();

    // 4. Get secrets from environment (NEVER hardcode)
    const geminiKey = Deno.env.get("GEMINI_API_KEY");
    if (!geminiKey) throw new Error("Missing GEMINI_API_KEY");

    // 5. Business logic here

    // 6. Return success
    return new Response(JSON.stringify({ data: result }), { headers, status: 200 });

  } catch (error) {
    // 7. Always return structured errors
    return new Response(
      JSON.stringify({ error: error.message }),
      { headers, status: 500 }
    );
  }
});
```

---

## GIT COMMIT MESSAGE FORMAT

```
Phase X: Short description of what was done

Examples:
Phase 3: Project setup complete — CMP initialized
Phase 5: Exercise Library backend — Supabase tables and RLS created
Phase 6: Exercise Library UI — list screen and detail screen complete
Phase 7: Edge case handled — imbalance detector with no existing plan
```

---

## WHAT AGENTS MUST NEVER DO

```
❌ Never import Firebase, Firestore, or any Firebase package
❌ Never import Room, @Entity, @Dao, or @Database annotations
❌ Never import Retrofit or @GET/@POST annotations
❌ Never import Hilt or @HiltViewModel
❌ Never call Gemini or Claude API from Kotlin code
❌ Never hardcode any URL, key, or credential
❌ Never write a Composable that calls a suspend function directly (use ViewModel)
❌ Never skip the Loading or Error state in a UiState sealed class
❌ Never write a TODO comment — flag it in PROJECT_STATUS.md instead
```

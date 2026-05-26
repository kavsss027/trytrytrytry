# ARCHITECTURE.md — GUJARATI FITNESS APP
> STATUS: LOCKED — Phase 2 Complete
> Every feature must follow this architecture. No exceptions without logging in DECISION_LOG.md.

---

## THE THREE-LAYER RULE

Every feature in this app follows the same three layers. These layers never talk to each other out of order.

```
PRESENTATION LAYER         ← What the user sees (Compose UI + ViewModel)
        ↕ only
DOMAIN LAYER               ← Business logic only (UseCases)
        ↕ only
DATA LAYER                 ← Data fetching (Repository → Remote + Local)
```

**Plain English:**
- The UI never calls the database directly
- The database never knows what the UI looks like
- UseCases contain the rules — they call the Repository, Repository calls Supabase or SQLDelight
- If you are writing a Composable that imports a SQLDelight query — that is wrong

---

## FULL FOLDER STRUCTURE

```
C:\Projects\GujaratiFitnessApp\
│
├── composeApp/
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/com/gujaratifitness/app/
│       │       │
│       │       ├── core/
│       │       │   ├── network/
│       │       │   │   ├── HttpClientFactory.kt      ← Creates Ktor HttpClient
│       │       │   │   └── NetworkConstants.kt       ← Base URLs, timeouts
│       │       │   ├── database/
│       │       │   │   ├── DriverFactory.kt          ← expect fun — platform provides driver
│       │       │   │   └── DatabaseFactory.kt        ← Creates SQLDelight database instance
│       │       │   ├── di/
│       │       │   │   ├── AppModule.kt              ← Koin: network, database
│       │       │   │   ├── DataModule.kt             ← Koin: repositories
│       │       │   │   └── DomainModule.kt           ← Koin: use cases
│       │       │   └── utils/
│       │       │       ├── Constants.kt
│       │       │       └── Extensions.kt
│       │       │
│       │       ├── data/
│       │       │   ├── models/                       ← @Serializable data classes
│       │       │   │   ├── User.kt
│       │       │   │   ├── Exercise.kt
│       │       │   │   ├── WorkoutPlan.kt
│       │       │   │   ├── DietPlan.kt
│       │       │   │   ├── MuscleImbalanceReport.kt
│       │       │   │   └── Influencer.kt
│       │       │   ├── remote/
│       │       │   │   ├── SupabaseClient.kt         ← Supabase client init
│       │       │   │   ├── ExerciseRemoteSource.kt
│       │       │   │   ├── PlanRemoteSource.kt
│       │       │   │   ├── DietRemoteSource.kt
│       │       │   │   ├── ImbalanceRemoteSource.kt
│       │       │   │   └── InfluencerRemoteSource.kt
│       │       │   ├── local/
│       │       │   │   ├── ExerciseLocalSource.kt
│       │       │   │   └── PlanLocalSource.kt
│       │       │   └── repository/
│       │       │       ├── ExerciseRepository.kt
│       │       │       ├── PlanRepository.kt
│       │       │       ├── DietRepository.kt
│       │       │       ├── ImbalanceRepository.kt
│       │       │       └── InfluencerRepository.kt
│       │       │
│       │       ├── domain/
│       │       │   └── usecases/
│       │       │       ├── GetExercisesUseCase.kt
│       │       │       ├── GenerateWorkoutPlanUseCase.kt
│       │       │       ├── GenerateDietPlanUseCase.kt
│       │       │       ├── DetectMuscleImbalanceUseCase.kt
│       │       │       ├── GetSTierExercisesUseCase.kt
│       │       │       └── ManageInfluencerGroupUseCase.kt
│       │       │
│       │       ├── presentation/
│       │       │   ├── screens/
│       │       │   │   ├── splash/
│       │       │   │   ├── auth/
│       │       │   │   │   ├── LoginScreen.kt
│       │       │   │   │   └── RegisterScreen.kt
│       │       │   │   ├── onboarding/
│       │       │   │   ├── home/
│       │       │   │   ├── exercises/
│       │       │   │   ├── workout/
│       │       │   │   ├── diet/
│       │       │   │   ├── imbalance/
│       │       │   │   ├── stier/
│       │       │   │   ├── influencer/
│       │       │   │   └── profile/
│       │       │   ├── components/
│       │       │   │   ├── AppButton.kt
│       │       │   │   ├── AppTextField.kt
│       │       │   │   ├── LoadingScreen.kt
│       │       │   │   ├── ErrorScreen.kt
│       │       │   │   ├── EmptyState.kt
│       │       │   │   └── GifImage.kt               ← expect/actual GIF component
│       │       │   └── viewmodels/
│       │       │       ├── ExerciseViewModel.kt
│       │       │       ├── WorkoutViewModel.kt
│       │       │       ├── DietViewModel.kt
│       │       │       ├── ImbalanceViewModel.kt
│       │       │       └── InfluencerViewModel.kt
│       │       │
│       │       └── navigation/
│       │           ├── AppNavigation.kt              ← Root Voyager navigator
│       │           └── Routes.kt                     ← All screen route objects
│       │
│       ├── androidMain/
│       │   └── kotlin/com/gujaratifitness/app/
│       │       ├── core/database/
│       │       │   └── DriverFactory.android.kt      ← actual AndroidSqliteDriver
│       │       └── core/gif/
│       │           └── GifImage.android.kt           ← actual Coil3 GifDecoder
│       │
│       └── iosMain/
│           └── kotlin/com/gujaratifitness/app/
│               ├── core/database/
│               │   └── DriverFactory.ios.kt          ← actual NativeSqliteDriver
│               └── core/gif/
│                   └── GifImage.ios.kt               ← actual UIKit GIF rendering
│
├── supabase/
│   ├── functions/
│   │   ├── generate-workout-plan/
│   │   │   └── index.ts
│   │   ├── generate-diet-plan/
│   │   │   └── index.ts
│   │   ├── detect-muscle-imbalance/
│   │   │   └── index.ts
│   │   └── check-generation-limit/
│   │       └── index.ts
│   ├── migrations/                                   ← Database schema changes
│   └── .env.local                                    ← NEVER commit — backend secrets
│
├── .docs/                                            ← All project documentation
├── gradle/
│   └── libs.versions.toml                            ← All dependency versions
├── local.properties                                  ← NEVER commit — app secrets
└── .gitignore
```

---

## DATA FLOW — HOW A FEATURE REQUEST TRAVELS

Using "Generate Workout Plan" as an example:

```
1. User taps "Generate Plan" button
   → WorkoutScreen.kt (Composable)

2. Screen calls ViewModel
   → WorkoutViewModel.kt
   → viewModel.generatePlan(questionnaireData)

3. ViewModel calls UseCase
   → GenerateWorkoutPlanUseCase.kt
   → useCase.execute(questionnaireData)

4. UseCase calls Repository
   → PlanRepository.kt
   → repository.generateWorkoutPlan(questionnaireData)

5. Repository calls Remote Source
   → PlanRemoteSource.kt
   → calls Supabase Edge Function: generate-workout-plan

6. Edge Function runs on Supabase servers
   → Calls Gemini 3.5 Flash API with questionnaire data
   → Returns generated plan as JSON

7. Response travels back up the chain
   → PlanRemoteSource → PlanRepository → UseCase → ViewModel

8. ViewModel updates StateFlow
   → _uiState.value = WorkoutUiState.Success(plan)

9. Screen observes StateFlow
   → Recomposes automatically with the new plan
   → User sees their generated workout plan
```

---

## STATE MANAGEMENT PATTERN

Every screen follows this exact pattern — no exceptions:

```kotlin
// UiState sealed class — one per screen
sealed class WorkoutUiState {
    object Idle : WorkoutUiState()
    object Loading : WorkoutUiState()
    data class Success(val plan: WorkoutPlan) : WorkoutUiState()
    data class Error(val message: String) : WorkoutUiState()
}

// ViewModel
class WorkoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WorkoutUiState>(WorkoutUiState.Idle)
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    fun generatePlan(data: QuestionnaireData) {
        viewModelScope.launch {
            _uiState.value = WorkoutUiState.Loading
            try {
                val plan = generatePlanUseCase.execute(data)
                _uiState.value = WorkoutUiState.Success(plan)
            } catch (e: Exception) {
                _uiState.value = WorkoutUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// Screen — always handles all 4 states
@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is WorkoutUiState.Idle    -> { /* Show form */ }
        is WorkoutUiState.Loading -> { LoadingScreen() }
        is WorkoutUiState.Success -> { /* Show plan */ }
        is WorkoutUiState.Error   -> { ErrorScreen(message) }
    }
}
```

**Every screen must handle all 4 states: Idle, Loading, Success, Error.**
Never skip Loading or Error states.

---

## GIF LOADING — EXPECT/ACTUAL PATTERN

```kotlin
// commonMain — the shared declaration
@Composable
expect fun GifImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
)

// androidMain — Coil3 implementation
@Composable
actual fun GifImage(url: String, modifier: Modifier, contentDescription: String?) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components { add(GifDecoder.Factory()) }
        .build()
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier
    )
}

// iosMain — UIKit implementation
@Composable
actual fun GifImage(url: String, modifier: Modifier, contentDescription: String?) {
    // Native iOS GIF rendering via UIKitView interop
    // Implementation details in FEATURE_01_EXERCISE_LIBRARY.md
}
```

---

## SUPABASE ROW LEVEL SECURITY — THE PERMISSION SYSTEM

Every database table has RLS enabled. Rules are written in SQL and enforced by Supabase automatically. The app never needs to check permissions manually — the database rejects unauthorized queries.

```
FREE USER    → Can read exercise library, read/write own data
PREMIUM USER → Everything free + read S-TIER exercises
INFLUENCER   → Everything premium + manage own group users
APP OWNER    → Full access to everything (service_role key — backend only)
```

Full RLS policies are defined in DATABASE_SCHEMA.md.

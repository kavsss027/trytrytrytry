<div align="center">

# 💪 CoolFit

### AI-powered fitness app built for Indian Gujarati gym-goers

![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.11.0-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3.1.4-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Gemini AI](https://img.shields.io/badge/Gemini_AI-3.5_Flash-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_26+-3DDC84?style=for-the-badge&logo=android&logoColor=white)

<!-- Add screenshots here -->

</div>

---

## 📖 About CoolFit

CoolFit is an AI-powered fitness app designed specifically for Indian Gujarati gym-goers — both complete beginners and intermediate lifters. Built with real gym trainer input and a Masters-level nutrition expert, CoolFit goes far beyond the generic fitness apps flooding the market.

The app solves four problems that most gym-goers face every single day: not knowing the correct exercise form, receiving generic cookie-cutter workout plans, getting diet advice that ignores their actual food culture, and having no awareness of muscle imbalances that are silently holding back their progress.

What makes CoolFit different is the depth of personalisation. The workout generator asks questions real trainers would ask. The diet generator knows what rotli, dal, and methi muthia are. The muscle imbalance detector analyses your actual lifts and training frequency to tell you exactly what needs work — and then generates a corrective plan automatically.

---

## ✨ Features

### 🏋️ Exercise Library
- GIF demonstrations of correct form for every exercise
- Browsable by 9 muscle group categories
- Available to all users — free and premium
- Cached locally for offline access

### 🤖 AI Workout Plan Generator
- Questionnaire designed with real gym trainer input
- Personalised based on your goals, equipment, schedule, and limitations
- Muscle imbalance data pre-filled automatically when available
- Free users: 3 generations per month | Premium: 7 per month

### 🥗 AI Diet Plan Generator
- Questionnaire designed with a Masters-level nutrition expert
- Authentic Gujarati food options included (rotli, dal, khichdi, paneer, methi muthia)
- Full macro and calorie breakdown per meal
- Separate generation limit from workout plans

### 📊 Muscle Imbalance Detector
- Input your PR maxes: bench, squat, deadlift, overhead press, pull-ups
- Input your weekly training frequency per muscle group
- AI generates an overall balance score from 0 to 100
- Shows specific imbalances, severity, and priority fixes
- One-tap redirect to generate a corrective workout plan

### ⭐ S-TIER Exercise Section *(Premium)*
- Curated list of viral and trending exercises
- Every exercise reviewed for safety and effectiveness
- Global list curated by app owner
- Influencers can add their own picks for their members

### 👥 Influencer System
- Join influencer fitness communities inside the app
- Influencers manage their own member groups via admin panel
- Approve or reject join requests
- Exclusive S-TIER content per influencer group

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.3.21 |
| UI Framework | Compose Multiplatform 1.11.0 |
| Navigation | Voyager 1.1.0-beta03 |
| Networking | Ktor Client 3.4.3 |
| Local Database | SQLDelight 2.1.0 |
| Dependency Injection | Koin 4.1.0 |
| Image + GIF Loading | Coil 3.0.4 |
| Backend | Supabase (PostgreSQL + Auth + Storage + Edge Functions) |
| AI — Development | Gemini 3.5 Flash |
| AI — Production | Claude claude-sonnet-4-20250514 |
| Target Platform | Android (iOS architecture ready) |
| Min Android SDK | API 26 (Android 8.0) |

---

## 🏗 Architecture

CoolFit follows Clean Architecture with three strict layers:

```
Presentation Layer  →  Screens + ScreenModels (Compose UI)
        ↓
Domain Layer        →  UseCases (Business Logic Only)
        ↓
Data Layer          →  Repositories → Remote (Supabase) + Local (SQLDelight)
        ↓
Backend             →  Supabase Edge Functions → Gemini / Claude AI
```

### Project Structure

```
composeApp/
  src/
    commonMain/    ← Shared UI and logic (Android + iOS)
    androidMain/   ← Android-specific implementations (drivers, GIF)
    iosMain/       ← iOS-specific implementations (drivers, GIF)

androidApp/        ← Android entry point
iosApp/            ← iOS entry point (Xcode — requires Mac)

supabase/
  functions/       ← Edge Functions (TypeScript / Deno)
    check-generation-limit/
    generate-workout-plan/
    generate-diet-plan/
    detect-muscle-imbalance/

.docs/             ← Full project documentation
  MASTER_CONTEXT.md
  foundation/
  contracts/
  features/
  agent/
  guides/
```

---

## 👤 User Types

| User Type | Access |
|-----------|--------|
| **Free** | Exercise library, 3 AI generations/month per feature, muscle imbalance detector |
| **Premium** | Everything free + S-TIER exercises + 7 generations/month per feature |
| **Influencer** | Everything premium + admin panel + manage own member group |
| **App Owner** | Full platform control — superadmin |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Panda 4 (2025.3.4) or newer
- Kotlin 2.3.21+
- Node.js 22.x LTS
- Supabase CLI 2.x
- Deno 2.x
- Antigravity CLI (latest)

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/kavsss027/FitnessApp.git
cd FitnessApp
```

**2. Create `local.properties` at project root**
```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
SUPABASE_URL=your_supabase_project_url
SUPABASE_ANON_KEY=your_supabase_anon_key
```

**3. Create `supabase/.env.local`**
```env
GEMINI_API_KEY=your_gemini_api_key
```

**4. Link Supabase CLI**
```bash
supabase login
supabase link --project-ref your_project_ref
```

**5. Open in Android Studio**
- File → Open → select project folder
- Wait for Gradle sync to complete

**6. Run on Android emulator**
- Tools → Device Manager → Create Virtual Device (Pixel 8, API 35+)
- Click the green ▶ play button

> 📄 See `.docs/foundation/LOCAL_SETUP.md` for the complete detailed setup guide.

---

## 🔐 Environment Variables

### `local.properties` *(app secrets — never commit)*
```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your_anon_key_here
```

### `supabase/.env.local` *(backend secrets — never commit)*
```env
GEMINI_API_KEY=your_gemini_api_key_here
```

### Supabase Dashboard → Edge Functions → Secrets *(production)*
```
GEMINI_API_KEY   your production AI key
```

> ⚠️ Both `local.properties` and `supabase/.env.local` are gitignored. Never commit secrets to version control.

---

## 🗄 Database Schema

All tables use PostgreSQL via Supabase with Row Level Security (RLS) enabled.

| Table | Purpose |
|-------|---------|
| `users` | User profiles, type (free/premium/influencer/owner), fitness level |
| `exercises` | Exercise library with GIF URLs and muscle group |
| `stier_exercises` | S-TIER curated exercises — global and per-influencer |
| `influencers` | Influencer profiles and slot management |
| `influencer_join_requests` | User requests to join influencer groups |
| `workout_plans` | AI-generated workout plans per user |
| `diet_plans` | AI-generated diet plans per user |
| `muscle_imbalance_reports` | Imbalance analysis results per user |
| `generation_usage` | Monthly AI generation count per user per feature |

---

## ⚡ Backend Edge Functions

All AI calls are made server-side only — API keys never touch the app.

| Function | Purpose |
|----------|---------|
| `check-generation-limit` | Enforces monthly AI generation limits per user type |
| `generate-workout-plan` | AI-powered personalised workout plan generation |
| `generate-diet-plan` | AI-powered diet plan with Gujarati food support |
| `detect-muscle-imbalance` | Analyses lift maxes and returns balance report |

---

## 🗺 Roadmap

- [x] Exercise Library with GIF demonstrations
- [x] AI Workout Plan Generator
- [x] AI Diet Plan Generator with Gujarati food options
- [x] Muscle Imbalance Detector
- [x] S-TIER Premium Exercise Section
- [x] Influencer System and Admin Panel
- [x] Onboarding flow (Beginner / Intermediate paths)
- [ ] iOS App Store release
- [ ] Weekly and monthly training followups
- [ ] In-app payment processing
- [ ] Social features and community feed
- [ ] Video demonstrations
- [ ] Training session logs

---

## 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Follow the coding standards in `.docs/foundation/CODING_STANDARDS.md`
4. Commit with a clear message following the project convention
5. Submit a pull request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

<div align="center">

Built with ❤️ for the Gujarati fitness community

</div>

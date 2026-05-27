<div align="center">

# 💪 CoolFit

### AI-powered fitness app built for Indian Gujarati gym-goers

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_26+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

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

## 👤 User Types

| User Type | Access |
|-----------|--------|
| **Free** | Exercise library, 3 AI generations/month per feature, muscle imbalance detector |
| **Premium** | Everything free + S-TIER exercises + 7 generations/month per feature |
| **Influencer** | Everything premium + admin panel + manage own member group |
| **App Owner** | Full platform control — superadmin |

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin Multiplatform |
| UI Framework | Compose Multiplatform |
| Navigation | Voyager |
| Networking | Ktor Client |
| Local Database | SQLDelight |
| Dependency Injection | Koin |
| Backend | Supabase |
| Target Platform | Android (iOS architecture ready) |
| Min Android SDK | API 26 (Android 8.0) |

---

## 🗄 Database

All tables use PostgreSQL via Supabase with Row Level Security (RLS) enabled on every table.

| Table | Purpose |
|-------|---------|
| `users` | User profiles and permission types |
| `exercises` | Exercise library with GIF demonstrations |
| `stier_exercises` | S-TIER curated exercises — global and per-influencer |
| `influencers` | Influencer profiles and slot management |
| `influencer_join_requests` | User requests to join influencer groups |
| `workout_plans` | AI-generated workout plans per user |
| `diet_plans` | AI-generated diet plans per user |
| `muscle_imbalance_reports` | Imbalance analysis results per user |
| `generation_usage` | Monthly AI generation count per user per feature |

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

---

## 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit with a clear message
4. Submit a pull request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

<div align="center">

Built with ❤️ for the Gujarati fitness community

</div>

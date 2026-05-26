# DATABASE_SCHEMA.md — GUJARATI FITNESS APP
> STATUS: Phase 4 — To be finalized before Phase 5 begins
> This is the single source of truth for all Supabase PostgreSQL tables.
> No table, column, or policy may be changed without updating this document first.

---

## DATABASE: gujarati-fitness-dev (Supabase PostgreSQL)

---

## TABLE 1 — users

Extends Supabase Auth. Created automatically when a user registers.

```sql
CREATE TABLE users (
  id              UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  email           TEXT NOT NULL UNIQUE,
  full_name       TEXT,
  user_type       TEXT NOT NULL DEFAULT 'free'
                  CHECK (user_type IN ('free', 'premium', 'influencer', 'owner')),
  fitness_level   TEXT CHECK (fitness_level IN ('beginner', 'intermediate')),
  influencer_id   UUID REFERENCES influencers(id) ON DELETE SET NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| id | UUID | ✅ | Matches auth.users id |
| email | TEXT | ✅ | Unique — from auth |
| full_name | TEXT | ❌ | Added during onboarding |
| user_type | TEXT | ✅ | Default: free |
| fitness_level | TEXT | ❌ | Set during onboarding |
| influencer_id | UUID | ❌ | Set when user joins influencer group |
| created_at | TIMESTAMPTZ | ✅ | Auto |
| updated_at | TIMESTAMPTZ | ✅ | Auto |

---

## TABLE 2 — exercises

App owner adds exercises. Users browse them.

```sql
CREATE TABLE exercises (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name            TEXT NOT NULL,
  description     TEXT,
  muscle_group    TEXT NOT NULL
                  CHECK (muscle_group IN (
                    'chest', 'back', 'shoulders', 'biceps', 'triceps',
                    'legs', 'glutes', 'core', 'full_body'
                  )),
  gif_url         TEXT NOT NULL,
  difficulty      TEXT NOT NULL DEFAULT 'intermediate'
                  CHECK (difficulty IN ('beginner', 'intermediate', 'advanced')),
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  added_by        UUID REFERENCES users(id),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| id | UUID | ✅ | Auto generated |
| name | TEXT | ✅ | Exercise name |
| description | TEXT | ❌ | Form tips and cues |
| muscle_group | TEXT | ✅ | One of 9 allowed values |
| gif_url | TEXT | ✅ | Supabase Storage URL |
| difficulty | TEXT | ✅ | Default: intermediate |
| is_active | BOOLEAN | ✅ | Soft delete — false hides from users |
| added_by | UUID | ❌ | Should always be app owner |
| created_at | TIMESTAMPTZ | ✅ | Auto |

---

## TABLE 3 — stier_exercises

Curated viral/trending exercises. Global list + per-influencer additions.

```sql
CREATE TABLE stier_exercises (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name            TEXT NOT NULL,
  description     TEXT,
  gif_url         TEXT NOT NULL,
  muscle_group    TEXT NOT NULL,
  influencer_id   UUID REFERENCES influencers(id) ON DELETE CASCADE,
  is_global       BOOLEAN NOT NULL DEFAULT FALSE,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| influencer_id | UUID | ❌ | NULL = global (owner-only). Set = influencer-specific |
| is_global | BOOLEAN | ✅ | TRUE = visible to all premium users |

---

## TABLE 4 — influencers

One row per influencer on the platform.

```sql
CREATE TABLE influencers (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  display_name    TEXT NOT NULL,
  max_slots       INTEGER NOT NULL DEFAULT 100,
  used_slots      INTEGER NOT NULL DEFAULT 0,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| user_id | UUID | ✅ | The influencer's user account |
| max_slots | INTEGER | ✅ | How many premium users they can have |
| used_slots | INTEGER | ✅ | Current count — incremented on approval |

---

## TABLE 5 — influencer_join_requests

Tracks user requests to join influencer groups.

```sql
CREATE TABLE influencer_join_requests (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  influencer_id   UUID NOT NULL REFERENCES influencers(id) ON DELETE CASCADE,
  status          TEXT NOT NULL DEFAULT 'pending'
                  CHECK (status IN ('pending', 'approved', 'rejected')),
  requested_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  responded_at    TIMESTAMPTZ,
  UNIQUE(user_id, influencer_id)
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| status | TEXT | ✅ | pending → approved or rejected |
| responded_at | TIMESTAMPTZ | ❌ | Set when influencer responds |
| UNIQUE constraint | — | — | One request per user per influencer |

---

## TABLE 6 — workout_plans

Stores generated workout plans per user.

```sql
CREATE TABLE workout_plans (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  plan_data       JSONB NOT NULL,
  questionnaire   JSONB NOT NULL,
  imbalance_used  BOOLEAN NOT NULL DEFAULT FALSE,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| plan_data | JSONB | ✅ | Full AI-generated plan as JSON |
| questionnaire | JSONB | ✅ | The inputs that generated this plan |
| imbalance_used | BOOLEAN | ✅ | Was imbalance data pre-filled? |
| is_active | BOOLEAN | ✅ | Only one active plan at a time |

---

## TABLE 7 — diet_plans

Mirrors workout_plans structure.

```sql
CREATE TABLE diet_plans (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  plan_data       JSONB NOT NULL,
  questionnaire   JSONB NOT NULL,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## TABLE 8 — muscle_imbalance_reports

Stores imbalance detector results.

```sql
CREATE TABLE muscle_imbalance_reports (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  bench_press_max     NUMERIC,
  squat_max           NUMERIC,
  deadlift_max        NUMERIC,
  overhead_press_max  NUMERIC,
  pullup_rows_max     INTEGER,
  training_days       JSONB,
  training_duration   TEXT,
  report_data         JSONB NOT NULL,
  created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| training_days | JSONB | ❌ | Days per week per muscle group |
| report_data | JSONB | ✅ | Full imbalance analysis result |

---

## TABLE 9 — generation_usage

Tracks AI plan generation counts per user per month.

```sql
CREATE TABLE generation_usage (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  feature         TEXT NOT NULL
                  CHECK (feature IN ('workout_plan', 'diet_plan')),
  month_year      TEXT NOT NULL,
  count           INTEGER NOT NULL DEFAULT 0,
  UNIQUE(user_id, feature, month_year)
);
```

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| month_year | TEXT | ✅ | Format: "2026-05" — resets each month |
| count | INTEGER | ✅ | Incremented each generation |
| UNIQUE | — | — | One row per user per feature per month |

---

## ROW LEVEL SECURITY (RLS) POLICIES

Enable RLS on every table immediately after creation:

```sql
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE stier_exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE influencers ENABLE ROW LEVEL SECURITY;
ALTER TABLE influencer_join_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE workout_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE diet_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE muscle_imbalance_reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE generation_usage ENABLE ROW LEVEL SECURITY;
```

### Key RLS Policies

```sql
-- Users: can only read and update their own row
CREATE POLICY "Users read own profile"
  ON users FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users update own profile"
  ON users FOR UPDATE USING (auth.uid() = id);

-- Exercises: everyone can read, only owner can write
CREATE POLICY "Anyone can read exercises"
  ON exercises FOR SELECT USING (is_active = TRUE);

CREATE POLICY "Only owner can insert exercises"
  ON exercises FOR INSERT
  WITH CHECK (
    EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND user_type = 'owner')
  );

-- S-TIER: only premium users can read
CREATE POLICY "Premium users can read stier"
  ON stier_exercises FOR SELECT
  USING (
    EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND user_type IN ('premium', 'influencer', 'owner'))
    AND is_active = TRUE
  );

-- Workout plans: users read/write own plans only
CREATE POLICY "Users manage own workout plans"
  ON workout_plans FOR ALL USING (auth.uid() = user_id);

-- Diet plans: users read/write own plans only
CREATE POLICY "Users manage own diet plans"
  ON diet_plans FOR ALL USING (auth.uid() = user_id);

-- Imbalance reports: users read/write own reports only
CREATE POLICY "Users manage own imbalance reports"
  ON muscle_imbalance_reports FOR ALL USING (auth.uid() = user_id);

-- Generation usage: users read own, edge functions write
CREATE POLICY "Users read own usage"
  ON generation_usage FOR SELECT USING (auth.uid() = user_id);
```

---

## INDEXES FOR PERFORMANCE

```sql
CREATE INDEX idx_exercises_muscle_group ON exercises(muscle_group);
CREATE INDEX idx_stier_influencer ON stier_exercises(influencer_id);
CREATE INDEX idx_join_requests_user ON influencer_join_requests(user_id);
CREATE INDEX idx_join_requests_influencer ON influencer_join_requests(influencer_id);
CREATE INDEX idx_join_requests_status ON influencer_join_requests(status);
CREATE INDEX idx_workout_plans_user ON workout_plans(user_id);
CREATE INDEX idx_diet_plans_user ON diet_plans(user_id);
CREATE INDEX idx_imbalance_user ON muscle_imbalance_reports(user_id);
CREATE INDEX idx_generation_usage_lookup ON generation_usage(user_id, feature, month_year);
```

---

## STORAGE BUCKETS

```
Bucket name:    exercise-gifs
Access:         Public read (anyone can view GIFs)
Write access:   Owner only (via service_role in Edge Function)
File types:     .gif only
Max file size:  10MB per file
```

---

## DATA DELETION RULES

```
users           → Hard delete (CASCADE removes all their data)
exercises       → Soft delete (is_active = FALSE — never truly deleted)
stier_exercises → Soft delete (is_active = FALSE)
workout_plans   → Soft delete (is_active = FALSE — keep for history)
diet_plans      → Soft delete (is_active = FALSE — keep for history)
imbalance reports → Never deleted — user history
join_requests   → Never deleted — audit trail
generation_usage → Never deleted — billing reference
```

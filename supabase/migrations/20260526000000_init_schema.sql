-- 1. Create users table first (without circular FK reference to influencers)
CREATE TABLE users (
  id              UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  email           TEXT NOT NULL UNIQUE,
  full_name       TEXT,
  user_type       TEXT NOT NULL DEFAULT 'free'
                  CHECK (user_type IN ('free', 'premium', 'influencer', 'owner')),
  fitness_level   TEXT CHECK (fitness_level IN ('beginner', 'intermediate')),
  influencer_id   UUID, -- will add FK constraint later
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Create influencers table
CREATE TABLE influencers (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  display_name    TEXT NOT NULL,
  max_slots       INTEGER NOT NULL DEFAULT 100,
  used_slots      INTEGER NOT NULL DEFAULT 0,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Add foreign key from users to influencers now that influencers table exists
ALTER TABLE users ADD CONSTRAINT fk_users_influencer FOREIGN KEY (influencer_id) REFERENCES influencers(id) ON DELETE SET NULL;

-- 4. Create exercises table
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

-- 5. Create stier_exercises table
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

-- 6. Create influencer_join_requests table
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

-- 7. Create workout_plans table
CREATE TABLE workout_plans (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  plan_data       JSONB NOT NULL,
  questionnaire   JSONB NOT NULL,
  imbalance_used  BOOLEAN NOT NULL DEFAULT FALSE,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 8. Create diet_plans table
CREATE TABLE diet_plans (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  plan_data       JSONB NOT NULL,
  questionnaire   JSONB NOT NULL,
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 9. Create muscle_imbalance_reports table
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

-- 10. Create generation_usage table
CREATE TABLE generation_usage (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  feature         TEXT NOT NULL
                  CHECK (feature IN ('workout_plan', 'diet_plan')),
  month_year      TEXT NOT NULL,
  count           INTEGER NOT NULL DEFAULT 0,
  UNIQUE(user_id, feature, month_year)
);

-- 11. Enable Row Level Security (RLS) on all tables
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE stier_exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE influencers ENABLE ROW LEVEL SECURITY;
ALTER TABLE influencer_join_requests ENABLE ROW LEVEL SECURITY;
ALTER TABLE workout_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE diet_plans ENABLE ROW LEVEL SECURITY;
ALTER TABLE muscle_imbalance_reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE generation_usage ENABLE ROW LEVEL SECURITY;

-- 12. Create RLS Policies

-- Users policies
CREATE POLICY "Users read own profile"
  ON users FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users update own profile"
  ON users FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users insert own profile"
  ON users FOR INSERT WITH CHECK (auth.uid() = id);

-- Exercises policies
CREATE POLICY "Anyone can read exercises"
  ON exercises FOR SELECT USING (is_active = TRUE);

CREATE POLICY "Only owner can insert exercises"
  ON exercises FOR INSERT
  WITH CHECK (
    EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND user_type = 'owner')
  );

-- S-TIER policies
CREATE POLICY "Premium users can read stier"
  ON stier_exercises FOR SELECT
  USING (
    EXISTS (SELECT 1 FROM users WHERE id = auth.uid() AND user_type IN ('premium', 'influencer', 'owner'))
    AND is_active = TRUE
  );

-- Workout plans policies
CREATE POLICY "Users manage own workout plans"
  ON workout_plans FOR ALL USING (auth.uid() = user_id);

-- Diet plans policies
CREATE POLICY "Users manage own diet plans"
  ON diet_plans FOR ALL USING (auth.uid() = user_id);

-- Imbalance reports policies
CREATE POLICY "Users manage own imbalance reports"
  ON muscle_imbalance_reports FOR ALL USING (auth.uid() = user_id);

-- Generation usage policies
CREATE POLICY "Users read own usage"
  ON generation_usage FOR SELECT USING (auth.uid() = user_id);

-- 13. Create Indexes for Performance
CREATE INDEX idx_exercises_muscle_group ON exercises(muscle_group);
CREATE INDEX idx_stier_influencer ON stier_exercises(influencer_id);
CREATE INDEX idx_join_requests_user ON influencer_join_requests(user_id);
CREATE INDEX idx_join_requests_influencer ON influencer_join_requests(influencer_id);
CREATE INDEX idx_join_requests_status ON influencer_join_requests(status);
CREATE INDEX idx_workout_plans_user ON workout_plans(user_id);
CREATE INDEX idx_diet_plans_user ON diet_plans(user_id);
CREATE INDEX idx_imbalance_user ON muscle_imbalance_reports(user_id);
CREATE INDEX idx_generation_usage_lookup ON generation_usage(user_id, feature, month_year);

-- 14. Create Trigger to automatically insert a user row on signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.users (id, email, full_name, user_type)
  VALUES (
    new.id,
    new.email,
    new.raw_user_meta_data->>'full_name',
    'free'
  );
  RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

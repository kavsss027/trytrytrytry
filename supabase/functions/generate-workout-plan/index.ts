import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders, status: 200 });
  }

  try {
    const authHeader = req.headers.get("Authorization");
    if (!authHeader) {
      return new Response(JSON.stringify({ error: "Missing Authorization header" }), {
        headers: corsHeaders,
        status: 401,
      });
    }

    const token = authHeader.replace("Bearer ", "");
    const supabaseClient = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "",
      {
        auth: {
          persistSession: false,
        },
      }
    );

    // Get user from token
    const { data: { user }, error: authError } = await supabaseClient.auth.getUser(token);
    if (authError || !user) {
      return new Response(JSON.stringify({ error: "Invalid token or unauthorized" }), {
        headers: corsHeaders,
        status: 401,
      });
    }

    // Parse request body
    const body = await req.json();
    const {
      fitness_level,
      goal,
      days_per_week,
      session_duration_minutes,
      available_equipment,
      injuries_limitations,
      current_lifts,
      imbalance_context,
    } = body;

    // Validate request inputs
    if (!fitness_level || !goal || !days_per_week || !session_duration_minutes || !available_equipment) {
      return new Response(JSON.stringify({ error: "Missing required fields in questionnaire" }), {
        headers: corsHeaders,
        status: 400,
      });
    }

    // 1. Check generation limit
    const { data: userData } = await supabaseClient
      .from("users")
      .select("user_type")
      .eq("id", user.id)
      .single();

    const userType = userData?.user_type ?? "free";
    const currentMonth = new Date().toISOString().substring(0, 7);

    const { data: usageData } = await supabaseClient
      .from("generation_usage")
      .select("id, count")
      .eq("user_id", user.id)
      .eq("feature", "workout_plan")
      .eq("month_year", currentMonth)
      .maybeSingle();

    const count = usageData?.count ?? 0;
    const limit = userType === "owner" ? 999
      : (userType === "premium" || userType === "influencer") ? 7
      : 3;

    if (count >= limit) {
      return new Response(
        JSON.stringify({
          error: "Generation limit reached for this month",
          limit: limit,
          used: count,
        }),
        { headers: corsHeaders, status: 429 }
      );
    }

    // 2. Call Gemini API
    const geminiKey = Deno.env.get("GEMINI_API_KEY");
    if (!geminiKey) {
      return new Response(JSON.stringify({ error: "Gemini API key is not configured on backend" }), {
        headers: corsHeaders,
        status: 500,
      });
    }

    const systemPrompt = `You are an elite, certified strength and conditioning coach and personal trainer. 
Your goal is to design a highly personalized, effective, and safe workout plan for a user.
The plan must target their fitness level, equipment availability, schedule, and primary goal.
If they have injuries or muscle imbalances, you must adapt the exercises, volume, and notes to safely address them.
You must return your output ONLY as a JSON object matching this schema exactly:
{
  "title": "Plan title (e.g., 4-Day Muscle Building Plan)",
  "duration_weeks": 8,
  "days": [
    {
      "day": "Day name (e.g., Monday)",
      "focus": "Focus of the day (e.g., Chest and Triceps)",
      "exercises": [
        {
          "name": "Exercise name",
          "sets": 4,
          "reps": "8-10",
          "rest_seconds": 90,
          "notes": "Form tips and safety reminders (e.g., Keep elbows at 45 degrees)"
        }
      ]
    }
  ],
  "general_notes": "General tips on progression, warmups, and workout guidelines",
  "nutrition_reminder": "General nutrition tips related to their fitness goal"
}`;

    const userPrompt = `Generate a workout plan with the following user profile:
- Fitness Level: ${fitness_level}
- Goal: ${goal}
- Training Days per Week: ${days_per_week} days
- Session Duration: ${session_duration_minutes} minutes
- Available Equipment: ${JSON.stringify(available_equipment)}
- Injuries / Limitations: ${injuries_limitations || "None"}
- Current Lifts (1RM or rep maxes): ${current_lifts ? JSON.stringify(current_lifts) : "Unknown"}
- Muscle Imbalance Context: ${imbalance_context ? JSON.stringify(imbalance_context) : "None"}. If imbalances exist, incorporate specific corrective exercises and modify volume/intensity ratios to resolve them.
`;

    const response = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${geminiKey}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          contents: [
            {
              role: "user",
              parts: [{ text: `${systemPrompt}\n\n${userPrompt}` }],
            },
          ],
          generationConfig: {
            responseMimeType: "application/json",
          },
        }),
      }
    );

    if (!response.ok) {
      const errText = await response.text();
      throw new Error(`Gemini API call failed: ${errText}`);
    }

    const resJson = await response.json();
    const generatedText = resJson.candidates?.[0]?.content?.parts?.[0]?.text;
    if (!generatedText) {
      throw new Error("No content generated by Gemini");
    }

    const planData = JSON.parse(generatedText.trim());

    // 3. Save to database
    // Set all existing workout plans for this user to is_active = false
    await supabaseClient
      .from("workout_plans")
      .update({ is_active: false })
      .eq("user_id", user.id);

    // Insert new plan
    const { data: newPlan, error: insertError } = await supabaseClient
      .from("workout_plans")
      .insert({
        user_id: user.id,
        plan_data: planData,
        questionnaire: {
          fitness_level,
          goal,
          days_per_week,
          session_duration_minutes,
          available_equipment,
          injuries_limitations,
          current_lifts,
          imbalance_context,
        },
        imbalance_used: !!imbalance_context,
        is_active: true,
      })
      .select()
      .single();

    if (insertError) {
      throw new Error(`Failed to save workout plan to database: ${insertError.message}`);
    }

    // 4. Update usage count
    if (usageData) {
      await supabaseClient
        .from("generation_usage")
        .update({ count: usageData.count + 1 })
        .eq("id", usageData.id);
    } else {
      await supabaseClient.from("generation_usage").insert({
        user_id: user.id,
        feature: "workout_plan",
        month_year: currentMonth,
        count: 1,
      });
    }

    return new Response(JSON.stringify({ plan: newPlan }), {
      headers: corsHeaders,
      status: 200,
    });

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: corsHeaders,
      status: 500,
    });
  }
});

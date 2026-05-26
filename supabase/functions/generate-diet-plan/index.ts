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
      age,
      gender,
      weight_kg,
      height_cm,
      activity_level,
      dietary_preference,
      food_allergies,
      meals_per_day,
      gujarati_food_preference,
    } = body;

    // Validate inputs
    if (!goal || !age || !gender || !weight_kg || !height_cm || !activity_level || !dietary_preference || !meals_per_day) {
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
      .eq("feature", "diet_plan")
      .eq("month_year", currentMonth)
      .maybeSingle();

    const count = usageData?.count ?? 0;
    let limit = 3;
    if (userType === "owner") {
      limit = 999999;
    } else if (userType === "premium" || userType === "influencer") {
      limit = 20;
    }

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

    const systemPrompt = `You are a professional registered dietitian and sports nutritionist. 
Your goal is to design a highly personalized, practical, and nutritionally balanced diet plan for a user.
The plan must target their fitness level, body stats (age, weight, height, gender), activity level, dietary preference, and primary fitness goal.
You must return your output ONLY as a JSON object matching this schema exactly:
{
  "title": "Plan title (e.g., Fat Loss Diet Plan)",
  "daily_calories": 1800,
  "macros": {
    "protein_g": 144,
    "carbs_g": 180,
    "fat_g": 60
  },
  "meals": [
    {
      "meal": "Meal name (e.g., Breakfast)",
      "time": "Suggested time (e.g., 8:00 AM)",
      "foods": ["Food item 1 with quantity (e.g., 2 eggs)", "Food item 2 (e.g., 1 cup poha)"],
      "calories": 450
    }
  ],
  "hydration_litres": 3.0,
  "notes": "General nutrition advice, supplementation tips if needed, and food swap options"
}`;

    const userPrompt = `Generate a diet plan with the following user profile:
- Fitness Level: ${fitness_level || "unknown"}
- Goal: ${goal} (Fat Loss / Muscle Gain / Maintenance)
- Age: ${age}
- Gender: ${gender}
- Weight: ${weight_kg} kg
- Height: ${height_cm} cm
- Activity Level: ${activity_level} (Sedentary / Lightly Active / Moderately Active / Very Active)
- Dietary Preference: ${dietary_preference} (Vegetarian / Non-Vegetarian / Vegan)
- Food Allergies / Exclusions: ${food_allergies || "None"}
- Number of Meals per Day: ${meals_per_day}
- Gujarati Food Preference: ${gujarati_food_preference ? "Yes" : "No"}. ${
      gujarati_food_preference
        ? "Crucial: The user prefers traditional Gujarati cuisine. Suggest healthy, high-protein versions of traditional Gujarati foods (e.g. rotli, dal, shaak, khichdi, dhokla, muthia, buttermilk/chhas, sprouted mung, paneer, handvo) structured to meet their macro targets."
        : ""
    }
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
    // Set all existing diet plans for this user to is_active = false
    await supabaseClient
      .from("diet_plans")
      .update({ is_active: false })
      .eq("user_id", user.id);

    // Insert new plan
    const { data: newPlan, error: insertError } = await supabaseClient
      .from("diet_plans")
      .insert({
        user_id: user.id,
        plan_data: planData,
        questionnaire: {
          fitness_level,
          goal,
          age,
          gender,
          weight_kg,
          height_cm,
          activity_level,
          dietary_preference,
          food_allergies,
          meals_per_day,
          gujarati_food_preference,
        },
        is_active: true,
      })
      .select()
      .single();

    if (insertError) {
      throw new Error(`Failed to save diet plan to database: ${insertError.message}`);
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
        feature: "diet_plan",
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

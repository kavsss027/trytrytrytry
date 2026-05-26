import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

serve(async (req: Request) => {
  // Handle preflight OPTIONS request
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
    
    // Create Supabase client with Service Role Key to bypass RLS for administrative count checks
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
    const { feature } = body;
    if (!feature || (feature !== "workout_plan" && feature !== "diet_plan")) {
      return new Response(JSON.stringify({ error: "Invalid or missing feature field. Must be 'workout_plan' or 'diet_plan'" }), {
        headers: corsHeaders,
        status: 400,
      });
    }

    // Query user's type
    const { data: userData, error: userError } = await supabaseClient
      .from("users")
      .select("user_type")
      .eq("id", user.id)
      .single();

    const userType = userData?.user_type ?? "free";

    // Calculate current month in "YYYY-MM" format
    const now = new Date();
    const currentMonth = now.toISOString().substring(0, 7); // e.g. "2026-05"

    // Query generation usage for this user, feature, and month
    const { data: usageData, error: usageError } = await supabaseClient
      .from("generation_usage")
      .select("count")
      .eq("user_id", user.id)
      .eq("feature", feature)
      .eq("month_year", currentMonth)
      .maybeSingle();

    const count = usageData?.count ?? 0;

    // Determine limit based on user type
    let limit = 3; // default free
    if (userType === "owner") {
      limit = 999999; // unlimited
    } else if (userType === "premium" || userType === "influencer") {
      limit = 20;
    }

    const canGenerate = count < limit;
    const remaining = Math.max(0, limit - count);

    // Calculate next month reset date (1st of next month)
    const nextMonth = new Date(now.getFullYear(), now.getMonth() + 1, 1);
    const resetDate = nextMonth.toISOString().substring(0, 10); // YYYY-MM-DD

    return new Response(
      JSON.stringify({
        can_generate: canGenerate,
        used: count,
        limit: limit,
        remaining: remaining,
        reset_date: resetDate,
      }),
      { headers: corsHeaders, status: 200 }
    );

  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), {
      headers: corsHeaders,
      status: 500,
    });
  }
});

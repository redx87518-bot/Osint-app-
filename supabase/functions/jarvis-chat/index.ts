import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const authHeader = req.headers.get("authorization");
    if (!authHeader) {
      return new Response(JSON.stringify({ error: "Missing authorization" }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const supabase = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_ANON_KEY") ?? "",
      {
        global: { headers: { Authorization: authHeader } },
      }
    );

    const { data: { user } } = await supabase.auth.getUser();
    if (!user) {
      return new Response(JSON.stringify({ error: "Unauthorized" }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const body = await req.json();
    const { message, history = [], conversation_id } = body;

    if (!message || typeof message !== "string") {
      return new Response(JSON.stringify({ error: "Missing message" }), {
        status: 400,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const aiApiKey = Deno.env.get("API4STUDENTS_API_KEY");
    const aiBaseUrl = Deno.env.get("API4STUDENTS_BASE_URL") ?? "https://api.namansoni.in/v1";
    const fishAudioKey = Deno.env.get("FISH_AUDIO_API_KEY");

    if (!aiApiKey) {
      return new Response(JSON.stringify({ error: "AI provider not configured" }), {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    let activeConversationId = conversation_id;
    if (!activeConversationId) {
      const { data: conversation, error: convError } = await supabase
        .from("conversations")
        .insert({ user_id: user.id, title: message.slice(0, 80) })
        .select("id")
        .single();

      if (convError) throw convError;
      activeConversationId = conversation.id;
    }

    const { error: userMsgError } = await supabase.from("messages").insert({
      conversation_id: activeConversationId,
      user_id: user.id,
      role: "user",
      content: message,
    });

    if (userMsgError) throw userMsgError;

    const trimmedHistory = history.slice(-20);

    const aiResponse = await fetch(`${aiBaseUrl}/chat/completions`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${aiApiKey}`,
      },
      body: JSON.stringify({
        model: Deno.env.get("API4STUDENTS_DEFAULT_MODEL") ?? "llama-3.1-8b-instant",
        messages: [
          { role: "system", content: "You are Jarvis, a helpful assistant." },
          ...trimmedHistory,
          { role: "user", content: message },
        ],
        temperature: 0.7,
        max_tokens: 1024,
      }),
    });

    if (!aiResponse.ok) {
      const text = await aiResponse.text();
      return new Response(JSON.stringify({ error: "AI provider error", details: text }), {
        status: 502,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    const aiData = await aiResponse.json();
    const text = aiData.choices?.[0]?.message?.content ?? "";

    const { error: assistantMsgError } = await supabase.from("messages").insert({
      conversation_id: activeConversationId,
      user_id: user.id,
      role: "assistant",
      content: text,
      metadata: { model: aiData.model },
    });

    if (assistantMsgError) throw assistantMsgError;

    let audioBase64: string | undefined;
    if (fishAudioKey && text) {
      try {
        const ttsResponse = await fetch("https://api.fish.audio/v1/tts", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${fishAudioKey}`,
          },
          body: JSON.stringify({
            text,
            model: "s2.1-pro-free",
            format: "mp3",
          }),
        });

        if (ttsResponse.ok) {
          const audioBuffer = await ttsResponse.arrayBuffer();
          audioBase64 = btoa(
            new Uint8Array(audioBuffer).reduce(
              (data, byte) => data + String.fromCharCode(byte),
              ""
            )
          );
        }
      } catch (ttsError) {
        console.error("TTS failed:", ttsError);
      }
    }

    return new Response(
      JSON.stringify({
        text,
        conversation_id: activeConversationId,
        model: aiData.model,
        audio: audioBase64,
      }),
      {
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  } catch (err) {
    return new Response(
      JSON.stringify({
        error: err instanceof Error ? err.message : "Unexpected error",
      }),
      {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  }
});

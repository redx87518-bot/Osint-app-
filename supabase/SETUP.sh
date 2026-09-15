#!/bin/bash
set -e

echo "=== Jarvis AI Supabase Backend Setup ==="
echo ""

# Check prerequisites
command -v supabase >/dev/null 2>&1 || { echo "Supabase CLI not found. Install: npm install -g supabase"; exit 1; }

# Prompt for token
if [ -z "$SUPABASE_ACCESS_TOKEN" ]; then
  read -p "Enter Supabase access token: " SUPABASE_ACCESS_TOKEN
  export SUPABASE_ACCESS_TOKEN
fi

# Login
echo "Logging in to Supabase..."
supabase login --token "$SUPABASE_ACCESS_TOKEN"

# Link project
echo "Linking to project jklnlcrzpumcgezwkucb..."
supabase link --project-ref jklnlcrzpumcgezwkucb

# Set secrets
echo ""
echo "Setting Edge Function secrets..."
read -p "Enter API4Students API key: " API4_KEY
supabase secrets set API4STUDENTS_API_KEY="$API4_KEY" --project-ref jklnlcrzpumcgezwkucb
supabase secrets set API4STUDENTS_BASE_URL="https://api.namansoni.in/v1" --project-ref jklnlcrzpumcgezwkucb
supabase secrets set API4STUDENTS_DEFAULT_MODEL="llama-3.1-8b-instant" --project-ref jklnlcrzpumcgezwkucb

# Push database
echo ""
echo "Pushing database schema..."
supabase db push --project-ref jklnlcrzpumcgezwkucb

# Deploy function
echo ""
echo "Deploying Edge Function..."
supabase functions deploy jarvis-chat --project-ref jklnlcrzpumcgezwkucb

echo ""
echo "=== Setup Complete ==="
echo "Edge Function URL: https://jklnlcrzpumcgezwkucb.supabase.co/functions/v1/jarvis-chat"
echo ""
echo "Next steps:"
echo "1. Create storage buckets: audio, images, files"
echo "2. Update Android app with Supabase credentials"
echo "3. Test authentication and chat flow"

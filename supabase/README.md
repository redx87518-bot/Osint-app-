# Jarvis AI - Supabase Backend

Complete Supabase backend for Jarvis AI with Edge Functions, PostgreSQL schema, and storage setup.

## What's Included

- `migrations/20240101_init.sql` - Complete database schema with RLS
- `functions/jarvis-chat/index.ts` - Edge Function for AI chat with API4Students + Fish Audio
- `storage.sql` - Storage RLS policies for audio, images, and files
- `config.toml` - Supabase project config
- `.env.example` - Environment variables template
- `SETUP.sh` - Automated setup script

## Quick Setup

### 1. Install Supabase CLI
```bash
npm install -g supabase
```

### 2. Login
```bash
supabase login --token <your-access-token>
```

### 3. Link Project
```bash
supabase link --project-ref jklnlcrzpumcgezwkucb
```

### 4. Set Secrets
```bash
supabase secrets set API4STUDENTS_API_KEY=your-key
supabase secrets set API4STUDENTS_BASE_URL=https://api.namansoni.in/v1
supabase secrets set API4STUDENTS_DEFAULT_MODEL=llama-3.1-8b-instant
supabase secrets set FISH_AUDIO_API_KEY=your-key
```

### 5. Apply Database Migration
```bash
supabase db push
```

Or manually in Supabase Dashboard > SQL Editor:
```sql
-- Copy contents of migrations/20240101_init.sql
```

### 6. Deploy Edge Function
```bash
supabase functions deploy jarvis-chat
```

### 7. Create Storage Buckets
In Supabase Dashboard > Storage, create:
- `audio` - For TTS audio files
- `images` - For avatars and images
- `files` - For user uploads

Then run `storage.sql` in SQL Editor.

## Database Schema

- `profiles` - User profiles (extends auth.users)
- `conversations` - Chat conversations
- `messages` - Chat messages with history
- `memories` - User context/memories
- `tasks` - Tasks and todos
- `tool_logs` - Tool execution logs
- `files` - File metadata for storage

All tables have Row Level Security enabled. Users can only access their own data.

## Edge Function: jarvis-chat

**Endpoint:** `https://jklnlcrzpumcgezwkucb.supabase.co/functions/v1/jarvis-chat`

**Request:**
```json
{
  "message": "Hello",
  "history": [],
  "conversation_id": "optional-uuid"
}
```

**Response:**
```json
{
  "text": "AI response",
  "conversation_id": "uuid",
  "model": "llama-3.1-8b-instant",
  "audio": "base64-encoded-mp3"
}
```

## Environment Variables

See `.env.example` for all required variables.

## Security

- API4Students key is server-side only in Edge Function secrets
- Fish Audio key is server-side only in Edge Function secrets
- Supabase Auth handles user authentication
- RLS policies enforce per-user data access
- No secrets in client code

## Android Integration

1. Add Supabase Android SDK
2. Configure with:
   - URL: `https://jklnlcrzpumcgezwkucb.supabase.co`
   - Anon key: `sb_publishable_8ES2xILHwJ2o9ejn6k26pw_L8EqK0ND`
3. Use `SupabaseManager` for auth
4. Call Edge Function with user JWT

# Jarvis AI Setup Guide

## Prerequisites

- Node.js 20+
- Java 17 (JDK)
- Android SDK 34
- Supabase project (`jklnlcrzpumcgezwkucb`)
- API4Students API key
- Fish Audio API key
- GitHub repository with Actions enabled

## 1. Supabase Backend Setup

### Install Supabase CLI
```bash
npm install -g supabase
```

### Login and Link
```bash
supabase login --token <your-access-token>
supabase link --project-ref jklnlcrzpumcgezwkucb
```

### Database Setup
```bash
supabase db push
```

Or manually in Supabase Dashboard > SQL Editor:
- Copy `supabase/migrations/20240101_init.sql`
- Execute in SQL Editor

### Edge Function Deployment
```bash
supabase functions deploy jarvis-chat
```

### Set Secrets
```bash
supabase secrets set API4STUDENTS_API_KEY=your-key
supabase secrets set API4STUDENTS_BASE_URL=https://api.namansoni.in/v1
supabase secrets set API4STUDENTS_DEFAULT_MODEL=llama-3.1-8b-instant
supabase secrets set FISH_AUDIO_API_KEY=your-key
```

### Storage Setup
Create buckets in Supabase Dashboard > Storage:
- `audio` - For TTS audio files
- `images` - For avatars and images
- `files` - For user uploads

Run `supabase/storage.sql` in SQL Editor for RLS policies.

## 2. Android App Setup

### Dependencies
Add to `android/app/build.gradle.kts`:
```kotlin
implementation("io.supabase.android:supabase-auth:1.0.0")
implementation("io.supabase.android:supabase-storage:1.0.0")
```

### Configuration
Update `SupabaseManager.kt` with your Supabase credentials:
- URL: `https://jklnlcrzpumcgezwkucb.supabase.co`
- Anon key: `sb_publishable_8ES2xILHwJ2o9ejn6k26pw_L8EqK0ND`

### Build
```bash
cd android
./gradlew assembleDebug
```

## 3. GitHub Actions Secrets

Add these secrets in GitHub Repository Settings:

| Name | Purpose |
|------|---------|
| `ANDROID_KEYSTORE` | Base64-encoded keystore file |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

## 4. Security Checklist

- [ ] No API4Students key in APK
- [ ] No Fish Audio key in APK
- [ ] No production secrets in git
- [ ] Supabase RLS policies enabled
- [ ] Edge Function secrets configured
- [ ] Storage buckets have RLS policies

## Edge Function Endpoint

```
POST https://jklnlcrzpumcgezwkucb.supabase.co/functions/v1/jarvis-chat
Authorization: Bearer <user-jwt>
```

## Database Schema

- `profiles` - User profiles
- `conversations` - Chat conversations
- `messages` - Chat messages
- `memories` - User context/memories
- `tasks` - Tasks and todos
- `tool_logs` - Tool execution logs
- `files` - File metadata

# Jarvis AI

Production-capable Android AI assistant powered entirely by Supabase.

## Architecture

```
ANDROID
  -> Supabase Auth (JWT)
  -> Supabase Edge Function (jarvis-chat)
      -> API4Students (brain/reasoning)
      -> Fish Audio (TTS)
  -> Supabase PostgreSQL (conversations, messages, memories, tasks, tool logs)
  -> Supabase Storage (audio, images, files)
```

## Services

### 1. Supabase
- **Auth**: Email/password authentication
- **PostgreSQL**: User data with Row Level Security
- **Storage**: Audio files, images, user files
- **Edge Functions**: Server-side AI processing

### 2. API4Students
- **Purpose**: Conversational reasoning brain
- **Endpoint**: `https://api.namansoni.in/v1/chat/completions`
- **Secret**: `API4STUDENTS_API_KEY` — Edge Function secret only, never in APK

### 3. Fish Audio
- **Purpose**: Text-to-speech voice output
- **Secret**: `FISH_AUDIO_API_KEY` — Edge Function secret only, never in APK

## Setup

### Prerequisites
- Node.js 20+
- Java 17 (JDK)
- Android SDK 34
- Supabase project (`jklnlcrzpumcgezwkucb`)
- API4Students API key
- Fish Audio API key

### 1. Supabase Backend

```bash
cd supabase
bash SETUP.sh
```

Or manually:
```bash
supabase login --token <your-token>
supabase link --project-ref jklnlcrzpumcgezwkucb
supabase db push
supabase functions deploy jarvis-chat
supabase secrets set API4STUDENTS_API_KEY=your-key
supabase secrets set FISH_AUDIO_API_KEY=your-key
```

### 2. Android App

```bash
cd android
# Add Supabase Android SDK dependency
./gradlew assembleDebug
```

### Security

- No API4Students key in APK
- No Fish Audio key in APK
- No production secrets in repo
- Supabase Auth handles authentication
- RLS policies enforce per-user data access
- All AI processing happens server-side

## Project Structure

```
jarvis-ai/
  android/
    app/
      src/main/java/com/jarvis/ai/
      src/main/res/
    build.gradle.kts
  supabase/
    migrations/
    functions/jarvis-chat/
    storage.sql
    SETUP.sh
    README.md
  docs/
```

## Build

### Debug APK (GitHub Actions)
Push to `main` → Actions → Build Debug APK → download artifact

### Release APK
Tag a version (`git tag v0.1.0 && git push --tags`) → GitHub Release workflow builds and uploads signed APK

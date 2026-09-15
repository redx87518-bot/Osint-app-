-- Jarvis AI Supabase schema
-- UUIDs, RLS, indexes, and relationships

create extension if not exists "uuid-ossp";

-- Profiles extends auth.users
create table public.profiles (
  id uuid references auth.users on delete cascade primary key,
  email text,
  display_name text,
  avatar_url text,
  settings jsonb not null default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create or replace function public.handle_new_user()
returns trigger as $$
begin
  insert into public.profiles (id, email, display_name)
  values (new.id, new.email, new.raw_user_meta_data->>'display_name');
  return new;
end;
$$ language plpgsql security definer;

create or replace trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

-- Conversations
create table public.conversations (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  title text not null default 'New conversation',
  metadata jsonb not null default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index idx_conversations_user_id on public.conversations(user_id);

-- Messages
create table public.messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  role text not null check (role in ('user', 'assistant', 'system')),
  content text not null,
  metadata jsonb not null default '{}',
  created_at timestamptz not null default now()
);

create index idx_messages_conversation_id on public.messages(conversation_id);
create index idx_messages_user_id on public.messages(user_id);
create index idx_messages_created_at on public.messages(created_at desc);

-- Memories
create table public.memories (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  key text not null,
  value text not null,
  metadata jsonb not null default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  unique(user_id, key)
);

create index idx_memories_user_id on public.memories(user_id);

-- Tasks
create table public.tasks (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  conversation_id uuid references public.conversations(id) on delete cascade,
  title text not null,
  description text,
  status text not null default 'open' check (status in ('open', 'in_progress', 'done', 'cancelled')),
  payload jsonb not null default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index idx_tasks_user_id on public.tasks(user_id);
create index idx_tasks_conversation_id on public.tasks(conversation_id);

-- Tool/action logs
create table public.tool_logs (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  conversation_id uuid references public.conversations(id) on delete cascade,
  message_id uuid references public.messages(id) on delete set null,
  tool_name text not null,
  status text not null check (status in ('started', 'success', 'error', 'cancelled')),
  input jsonb not null default '{}',
  output jsonb not null default '{}',
  error text,
  created_at timestamptz not null default now()
);

create index idx_tool_logs_user_id on public.tool_logs(user_id);
create index idx_tool_logs_conversation_id on public.tool_logs(conversation_id);

-- Files / audio / images metadata
create table public.files (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  bucket text not null,
  path text not null,
  mime_type text,
  size bigint,
  metadata jsonb not null default '{}',
  created_at timestamptz not null default now()
);

create index idx_files_user_id on public.files(user_id);
create index idx_files_bucket_path on public.files(bucket, path);

-- Enable RLS
alter table public.profiles enable row level security;
alter table public.conversations enable row level security;
alter table public.messages enable row level security;
alter table public.memories enable row level security;
alter table public.tasks enable row level security;
alter table public.tool_logs enable row level security;
alter table public.files enable row level security;

-- RLS policies
create policy "Users can view own profile" on public.profiles for select using (auth.uid() = id);
create policy "Users can update own profile" on public.profiles for update using (auth.uid() = id);

create policy "Users can view own conversations" on public.conversations for select using (auth.uid() = user_id);
create policy "Users can insert own conversations" on public.conversations for insert with check (auth.uid() = user_id);
create policy "Users can update own conversations" on public.conversations for update using (auth.uid() = user_id);
create policy "Users can delete own conversations" on public.conversations for delete using (auth.uid() = user_id);

create policy "Users can view own messages" on public.messages for select using (auth.uid() = user_id);
create policy "Users can insert own messages" on public.messages for insert with check (auth.uid() = user_id);
create policy "Users can update own messages" on public.messages for update using (auth.uid() = user_id);
create policy "Users can delete own messages" on public.messages for delete using (auth.uid() = user_id);

create policy "Users can view own memories" on public.memories for select using (auth.uid() = user_id);
create policy "Users can insert own memories" on public.memories for insert with check (auth.uid() = user_id);
create policy "Users can update own memories" on public.memories for update using (auth.uid() = user_id);
create policy "Users can delete own memories" on public.memories for delete using (auth.uid() = user_id);

create policy "Users can view own tasks" on public.tasks for select using (auth.uid() = user_id);
create policy "Users can insert own tasks" on public.tasks for insert with check (auth.uid() = user_id);
create policy "Users can update own tasks" on public.tasks for update using (auth.uid() = user_id);
create policy "Users can delete own tasks" on public.tasks for delete using (auth.uid() = user_id);

create policy "Users can view own tool logs" on public.tool_logs for select using (auth.uid() = user_id);
create policy "Users can insert own tool logs" on public.tool_logs for insert with check (auth.uid() = user_id);

create policy "Users can view own files" on public.files for select using (auth.uid() = user_id);
create policy "Users can insert own files" on public.files for insert with check (auth.uid() = user_id);
create policy "Users can update own files" on public.files for update using (auth.uid() = user_id);
create policy "Users can delete own files" on public.files for delete using (auth.uid() = user_id);

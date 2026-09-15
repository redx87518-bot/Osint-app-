-- Supabase Storage RLS policies
-- Run these after creating buckets in the Supabase Dashboard

-- Audio bucket policies
create policy "Users can upload own audio" on storage.objects
  for insert with check (
    bucket_id = 'audio' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can read own audio" on storage.objects
  for select using (
    bucket_id = 'audio' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can update own audio" on storage.objects
  for update using (
    bucket_id = 'audio' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can delete own audio" on storage.objects
  for delete using (
    bucket_id = 'audio' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

-- Images bucket policies
create policy "Users can upload own images" on storage.objects
  for insert with check (
    bucket_id = 'images' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can read own images" on storage.objects
  for select using (
    bucket_id = 'images' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can update own images" on storage.objects
  for update using (
    bucket_id = 'images' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can delete own images" on storage.objects
  for delete using (
    bucket_id = 'images' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

-- Files bucket policies
create policy "Users can upload own files" on storage.objects
  for insert with check (
    bucket_id = 'files' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can read own files" on storage.objects
  for select using (
    bucket_id = 'files' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can update own files" on storage.objects
  for update using (
    bucket_id = 'files' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "Users can delete own files" on storage.objects
  for delete using (
    bucket_id = 'files' and
    auth.uid()::text = (storage.foldername(name))[1]
  );

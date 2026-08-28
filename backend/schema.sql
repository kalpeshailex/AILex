-- Run this once in the Supabase SQL editor (Dashboard → SQL Editor → New query).
-- Mirrors app/src/main/java/com/example/ailex/domain/incident/Incident.kt,
-- NotificationsViewModel.kt, and AppViewModel.kt's AppSessionState.

create table if not exists public.incidents (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  domain text not null,
  title text not null,
  status text not null default 'ACTIVE',
  tags text[] not null default '{}',
  date_location text,
  saved_detail text,
  summary text not null default '',
  key_facts jsonb not null default '[]',
  timeline jsonb not null default '[]',
  evidence jsonb not null default '[]',
  notes text not null default '',
  complaint_edits jsonb not null default '{}',
  saved_at timestamptz not null default now()
);

alter table public.incidents enable row level security;

create policy "Users manage their own incidents"
  on public.incidents for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null default '',
  language text not null default 'ENGLISH',
  created_at timestamptz not null default now()
);

alter table public.profiles enable row level security;

create policy "Users manage their own profile"
  on public.profiles for all
  using (auth.uid() = id)
  with check (auth.uid() = id);

create table if not exists public.notifications (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users(id) on delete cascade,
  title text not null,
  body text not null,
  when_text text not null,
  icon text not null,
  unread boolean not null default true,
  incident_id uuid references public.incidents(id) on delete set null,
  created_at timestamptz not null default now()
);

alter table public.notifications enable row level security;

create policy "Users manage their own notifications"
  on public.notifications for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

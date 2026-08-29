-- Legal knowledge base schema — run ONCE in the Supabase SQL editor
-- (Dashboard → SQL Editor → New query), before legal_kb_seed.sql.
--
-- Mirrors 08_LEGAL_KNOWLEDGE.md / 01_PRD_DETAILED_EXISTING.md §30 (Database)
-- and the shape of Mumbai_Legal_KB_All_Domains_Populated.xlsx (user-provided
-- research pass, 2026-08-27 — see that file's _README sheet for sourcing
-- notes and which rows are still `pending_review`).
--
-- This is reference data, not user-owned data: every table gets RLS enabled
-- with a SELECT-only policy for any authenticated user, and no INSERT/UPDATE/
-- DELETE policy at all — mutation only ever happens by a human running SQL
-- directly in the Supabase dashboard (which runs as postgres, bypassing RLS),
-- never through the Worker's anon-key-scoped client. See 10_API.md: "Legal
-- source mutation must not be exposed to normal mobile users."

create table if not exists public.legal_sources (
  source_id text primary key,
  title text not null,
  source_type text not null,
  official_url text,
  jurisdiction text not null,
  issuing_authority text,
  publication_date date,
  effective_date date,
  expiry_date date,
  last_verified_at date,
  verified_by text,
  verification_status text not null,
  language text not null default 'en',
  notes text,
  version text
);

create table if not exists public.law_sections (
  section_id text primary key,
  source_id text references public.legal_sources(source_id),
  law_short_name text,
  section_number text,
  heading text,
  full_text text,
  plain_summary text,
  effective_date date,
  verification_status text not null
);

create table if not exists public.penalties (
  penalty_id text primary key,
  law_section_id text references public.law_sections(section_id),
  offence text,
  amount_min numeric,
  amount_max numeric,
  currency text,
  imprisonment_max text,
  compoundable text,
  jurisdiction text,
  state_notified_amount numeric,
  effective_date date,
  last_verified_at date,
  verification_status text not null,
  note text
);

create table if not exists public.scenarios (
  scenario_id text primary key,
  domain text not null,
  title text not null,
  jurisdiction text not null,
  plain_language_summary text,
  language text not null default 'en',
  verification_status text not null
);

create table if not exists public.scenario_questions (
  question_id text primary key,
  scenario_id text references public.scenarios(scenario_id),
  question_text text not null,
  ask_order int
);

create table if not exists public.authorities (
  authority_id text primary key,
  name text not null,
  type text,
  jurisdiction text not null,
  parent_authority text
);

create table if not exists public.authority_contacts (
  contact_id text primary key,
  authority_id text references public.authorities(authority_id),
  channel text not null,
  value text not null,
  region text,
  last_verified_at date
);

create table if not exists public.escalation_paths (
  escalation_id text primary key,
  domain text not null,
  scenario_id text references public.scenarios(scenario_id),
  step_order int not null,
  step_description text not null,
  authority_id text references public.authorities(authority_id),
  legal_basis_section_id text references public.law_sections(section_id)
);

-- The primary retrieval unit for LegalKnowledgeService -- pre-chunked,
-- plain-language content already scoped to one domain/jurisdiction/scenario,
-- closely matching the LegalEvidence shape the AI pipeline expects.
create table if not exists public.document_chunks (
  chunk_id text primary key,
  source_id text references public.legal_sources(source_id),
  law_section_id text references public.law_sections(section_id),
  scenario_id text references public.scenarios(scenario_id),
  content text not null,
  domain text not null,
  jurisdiction text not null,
  authority text,
  language text not null default 'en',
  effective_date date,
  verification_status text not null,
  source_url text
);

alter table public.legal_sources enable row level security;
alter table public.law_sections enable row level security;
alter table public.penalties enable row level security;
alter table public.scenarios enable row level security;
alter table public.scenario_questions enable row level security;
alter table public.authorities enable row level security;
alter table public.authority_contacts enable row level security;
alter table public.escalation_paths enable row level security;
alter table public.document_chunks enable row level security;

drop policy if exists "Authenticated users can read legal_sources" on public.legal_sources;
create policy "Authenticated users can read legal_sources" on public.legal_sources for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read law_sections" on public.law_sections;
create policy "Authenticated users can read law_sections" on public.law_sections for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read penalties" on public.penalties;
create policy "Authenticated users can read penalties" on public.penalties for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read scenarios" on public.scenarios;
create policy "Authenticated users can read scenarios" on public.scenarios for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read scenario_questions" on public.scenario_questions;
create policy "Authenticated users can read scenario_questions" on public.scenario_questions for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read authorities" on public.authorities;
create policy "Authenticated users can read authorities" on public.authorities for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read authority_contacts" on public.authority_contacts;
create policy "Authenticated users can read authority_contacts" on public.authority_contacts for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read escalation_paths" on public.escalation_paths;
create policy "Authenticated users can read escalation_paths" on public.escalation_paths for select using (auth.role() = 'authenticated');

drop policy if exists "Authenticated users can read document_chunks" on public.document_chunks;
create policy "Authenticated users can read document_chunks" on public.document_chunks for select using (auth.role() = 'authenticated');

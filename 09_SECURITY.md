# Security & Privacy Specification

## Goal
Protect accounts, incidents, conversations, local evidence references, credentials and sensitive personal information.

## Authentication
Mobile + OTP, secure session handling, OTP rate limits and abuse prevention.

## API
HTTPS, authentication, authorization, validation, rate limiting, request limits and safe errors.

## Secrets
Never put AI provider keys, Supabase service-role key or backend private credentials in Android.

## Database
Supabase RLS is mandatory for user-owned data.

## Local storage
Use Room, Android Keystore, app-private storage and Storage Access Framework as appropriate. Do not store sensitive secrets in plain text.

## Evidence
V1 evidence remains on-device; no default cloud upload.

## Voice
Do not permanently store raw voice recordings by default.

## Logging
Never log OTP, PIN, CVV, passwords, access tokens or UPI PIN. Avoid unnecessary legal conversation content.

## Privacy
Collect minimum PII:
- mobile
- name
- preferred language
- optional information only when required

Do not request Aadhaar or unrelated identity information.

## Deletion
Users should be able to delete incidents/local data and request account deletion.

## AI privacy
Explain what is sent to backend/AI provider and what is stored.

## Prompt injection
Treat user and retrieved text as untrusted data. Retrieved documents cannot override system/application policies.

## High-risk
Prioritize safety in arrest, violence, sexual blackmail, financial fraud, serious threats and serious legal deadlines. Never encourage illegal evasion or confrontation.

# Backend Specification — V1

## Purpose
Secure boundary between Android and external services.

## Recommended
Cloudflare Worker or equivalent free/low-cost serverless API.

## Responsibilities
- auth integration
- authorization
- validation
- conversation orchestration
- AI provider calls
- RAG retrieval
- citation validation
- complaint generation
- escalation retrieval
- rate limiting
- abuse protection
- operational logging

## Security
Android must never contain AI secrets or Supabase service-role credentials.

## Flow
Android → HTTPS → Authenticate → Validate → Authorize → Service → AI/RAG → Validate output → Response.

## API
Use `/api/v1/...` style versioning.

## Errors
Use stable structured error codes. Never expose stack traces, provider internals or secrets.

## Rate limiting
At minimum:
- OTP
- conversation
- voice
- complaint generation
- feedback

## Logging
Prefer request ID, endpoint, latency, status and error class. Avoid sensitive legal content in logs.

# System Architecture — V1

## Goal
Keep V1 cheap and simple while preserving clean interfaces for future scale.

> Architect for scale; do not prematurely build for scale.

## High level
```text
Android
  ↓ HTTPS
API / Backend Worker
  ├─ Auth
  ├─ Conversation
  ├─ Incident
  ├─ Legal Knowledge
  ├─ AI Orchestrator
  ├─ Voice
  ├─ Complaint
  └─ Escalation
       ↓
Supabase PostgreSQL + pgvector
       ↓
Verified legal sources
```

## Android
UI → ViewModel → Use Case/Domain → Repository → Local/Remote data.

## Backend
API → Auth/Authorization → Application Service → Domain Service → Repository/Provider.

## Domain modules
- auth
- conversation
- voice
- legal
- incident
- complaint
- escalation
- feedback

## Future modules
- documents
- OCR
- BMC
- RTO
- housing
- consumer
- employment
- advanced cyber
- lawyer network

## Provider abstraction
Use replaceable interfaces:
- AIProvider
- VoiceProvider
- EmbeddingProvider
- LegalKnowledgeProvider

## Conversation vs Incident
Conversation is temporary. Incident is user-created and persistent. A conversation becomes an incident only after explicit user choice.

## Legal boundary
All legal retrieval must go through LegalKnowledgeService.

## Scalability
Start as a modular monolith. Extract services only when justified by actual load/complexity.

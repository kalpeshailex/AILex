# Testing & Quality Strategy

## Goal
The product must be legally cautious, grounded, secure, usable and stable.

## Test layers
### Unit
Risk rules, repositories, parsers, validators, citation mapping, incident logic.

### Integration
Android/API, API/Supabase, API/AI, RAG, voice, incident save.

### UI
Onboarding, conversation, voice, live situation, save/not-save, incidents, complaint.

### Legal
At least 100 scenario evaluations before beta.

## Evaluation case
Each case should include:
- input
- expected domain
- jurisdiction
- required facts
- expected source
- expected risk
- expected action
- forbidden advice

## Red team
Test hallucination, fabricated sections, unsupported jurisdiction, prompt injection, dangerous instructions, OTP/PIN disclosure, arrest/evasion, confrontation, financial fraud and sexual blackmail.

## Metrics
- legal correctness
- citation correctness
- jurisdiction correctness
- safety
- hallucination rate
- follow-up quality
- voice success
- latency
- crash-free sessions
- user usefulness

## Release gate
No critical hallucination, authorization flaw, secret exposure, unsafe legal instruction or core citation failure.

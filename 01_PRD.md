# Product Requirements Document — V1

## Product Definition
A native Android legal first-aid assistant for Mumbai citizens. Users describe a real-world situation by text or voice. The system identifies relevant facts, retrieves verified legal information, explains rights/obligations/authority powers, provides an action plan, and offers escalation.

## V1 scope
### Authentication
- Mobile + OTP
- Name
- Preferred language
- No guest mode

### Core
- Home
- Ask Legal AI
- Voice
- Live Situation
- Police
- Traffic
- Mumbai Local/Railway
- Government/RTS
- Cyber
- My Incidents
- Complaint drafts
- Escalation
- Feedback

### Live Situation
1. Identify domain/situation.
2. Gather only material facts.
3. Determine jurisdiction.
4. Assess immediate risk.
5. Retrieve verified legal sources.
6. Determine rights, obligations and authority powers.
7. Generate action plan.
8. Validate citations.
9. Offer save/not-save.
10. Offer escalation.

## V1 out of scope
- OCR
- document AI
- cloud document/evidence vault
- automatic filing
- lawyer marketplace
- BMC
- RTO
- housing
- consumer
- employment
- nationwide coverage
- continuous real-time voice
- complex multi-agent architecture

## Response structure
Where applicable:
- Situation
- What may apply
- Your position
- Your rights
- Authority powers
- What to do now
- Avoid
- Preserve
- Legal basis
- Escalation

## UX
Calm, readable, action-oriented, accessible, low cognitive load.

## Definition of Done
A beta user can authenticate, use text/voice, use all five domains, receive grounded guidance and citations, save/not-save incidents, view/delete incidents, create complaint drafts, view escalation, and report errors.

## Launch gate
- 100+ legal scenarios
- source verification
- citation testing
- high-risk tests
- cyber tests
- security tests
- hallucination tests
- prompt-injection tests
- unsupported-jurisdiction tests

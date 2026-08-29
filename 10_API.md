# API Contract Specification — V1

## Base
`/api/v1`

## Auth
- POST `/auth/otp/start`
- POST `/auth/otp/verify`
- POST `/auth/logout`

## Conversation
- POST `/conversation`
- POST `/conversation/message`
- POST `/conversation/voice`
- GET `/conversation/:id`

## Incidents
- POST `/incident/save`
- GET `/incidents`
- GET `/incident/:id`
- PATCH `/incident/:id`
- DELETE `/incident/:id`
- POST `/incident/:id/note`

## Legal
- GET `/legal/search`
- GET `/legal/source/:id`

Legal source mutation must not be exposed to normal mobile users.

## Complaint
- POST `/complaint/draft`

## Escalation
- GET `/authority/search`
- GET `/escalation`

## Feedback
- POST `/feedback`

## Request example
```json
{
  "conversation_id": "uuid",
  "input_type": "text",
  "message": "Police stopped me near Dadar.",
  "language": "en"
}
```

## Response example
```json
{
  "conversation_id": "uuid",
  "message_id": "uuid",
  "response": {
    "summary": "...",
    "situation": "...",
    "rights": [],
    "obligations": [],
    "authority_powers": [],
    "actions": [],
    "avoid": [],
    "preserve": [],
    "escalation": [],
    "citations": []
  },
  "risk": {
    "level": "medium",
    "reason": "..."
  },
  "needs_follow_up": true,
  "next_question": "..."
}
```

All endpoints must authenticate/authorize and return stable error codes.

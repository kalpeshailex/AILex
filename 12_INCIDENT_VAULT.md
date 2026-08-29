# Incident Vault Specification

## Core rule
A conversation is not an incident. The user must choose to save.

## Lifecycle
```text
Conversation
→ Save Prompt
→ Save
→ Incident Created
→ View/Edit/Delete
```

Alternative:
```text
Conversation
→ Not Now
→ No incident
```

## Suggested statuses
- Open
- Monitoring
- Resolved
- Archived

## Summary
AI may generate a summary, but user can edit it. Distinguish user facts from AI interpretation.

## Timeline
Events can be user-entered, AI-extracted or manually edited. AI timestamps must never be presented as facts unless provided by the user/system.

## Notes
Free-text user notes.

## Evidence
V1 stores local references only; no default cloud upload.

## Future
V2 may add encrypted cloud backup, cross-device sync, OCR and document intelligence.

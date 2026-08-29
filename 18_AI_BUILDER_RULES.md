# AI Coding Assistant Rules

1. Read `00_PROJECT_MASTER.md` and `01_PRD.md` before coding.
2. Read relevant architecture/security files before architecture changes.
3. Do not invent features.
4. Do not silently change decisions.
5. Never hard-code secrets.
6. Never put AI provider credentials in Android.
7. Never put Supabase service-role credentials in Android.
8. Never hard-code dynamic legal claims into UI.
9. Legal content belongs in the legal knowledge system.
10. No OCR in V1.
11. No cloud evidence storage in V1.
12. No continuous voice in V1.
13. No autonomous agents without approval.
14. Validate authentication and authorization on every protected API.
15. Apply RLS to user-owned tables.
16. Every feature needs loading/error/empty/success states.
17. Write tests for important logic.
18. Keep UI separate from business logic.
19. Keep provider integrations behind interfaces.
20. Do not replace the stack without approval.

## Workflow
Before coding: inspect repo, relevant files and dependencies.
During coding: make small changes and keep builds healthy.
After coding: run tests, build, inspect errors, summarize changes.

When uncertain, do not guess. State what is unknown, why it matters and the safest option.

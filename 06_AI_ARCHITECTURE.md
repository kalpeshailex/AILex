# AI Architecture — V1

## Goal
Reliable legal reasoning pipeline, not a generic chatbot.

## Pipeline
```text
Input
↓
Input Normalizer
↓
Situation Classifier
↓
Context Extractor
↓
Risk Engine
↓
Legal Knowledge Service
↓
Authority Service
↓
Action Planner
↓
Response Generator
↓
Citation Validator
↓
Safety Validator
↓
Response
```

## Input Normalizer
Normalize typed text or STT output without inventing facts.

## Situation Classifier
Return domain, scenario, jurisdiction and confidence. If uncertainty matters, ask a clarifying question.

## Context Extractor
Extract:
- what happened
- when
- where
- authority/person
- documents mentioned
- actions taken
- immediate risk
- user objective

## Risk Engine
Use deterministic rules for obvious high-risk cases before relying only on an LLM.

## Legal Knowledge Service
Returns verified laws, rules, procedures, authorities and citations.

## Action Planner
Turns verified facts/legal context into practical steps.

## Response Generator
Explains in plain language and separates fact from inference.

## Citation Validator
Checks legal claims against sources.

## Safety Validator
Checks dangerous instructions, evasion, fabricated claims, unsupported certainty and secret requests.

## V1 rule
No complex autonomous multi-agent system. Use logical modules/functions.

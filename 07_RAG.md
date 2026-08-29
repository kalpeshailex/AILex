# Legal RAG Specification

## Objective
Ground legal answers in verified legal sources.

## Storage
Supabase PostgreSQL + pgvector.

## Retrieval
Use metadata filtering plus keyword/vector retrieval where practical.

## Required metadata
- source_id
- jurisdiction
- domain
- authority
- effective_date
- verification_status
- source_type
- language
- section_reference

## Retrieval process
1. Filter jurisdiction.
2. Exclude unverified/superseded sources.
3. Identify domain/scenario.
4. Retrieve candidates.
5. Rank.
6. Pass relevant evidence to LLM.
7. Validate claims.

## Source status
Only verified sources normally support authoritative answers.

## Chunking
Preserve section titles, definitions, exceptions and qualifiers with relevant context.

## Abstraction
The app must use LegalKnowledgeService rather than directly depending on pgvector.

## Citation
Important claims must trace to source, section/reference, official URL where available and verification date.

## Failure
If verified support is insufficient:
- ask a material missing question,
- provide official verification route, or
- say it cannot be reliably verified.

Never fill retrieval gaps from model memory.

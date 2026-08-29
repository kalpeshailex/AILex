// Embedding abstraction, kept separate from AIProvider (see task's
// "Embeddings" section) — LegalKnowledgeService must depend on this
// interface, never directly on Gemini's embedding API, and embeddings are
// used only for retrieval ranking, never as a legal-reasoning model.
export interface EmbeddingProvider {
  embed(text: string): Promise<number[]>;
}

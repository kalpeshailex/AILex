// Authority Service abstraction (task's "Authority Service" section).
// Retrieves relevant authority/escalation info from verified sources only --
// never invents contact numbers, emails, offices, or procedures. Like
// LegalKnowledgeService, there is no real backing data source yet, so the
// mock returns nothing by default rather than fabricated-looking contacts.
import { AuthorityInfo, AuthorityQuery } from "../schemas/types";

export interface AuthorityService {
  retrieve(query: AuthorityQuery): Promise<AuthorityInfo[]>;
}

export class MockAuthorityService implements AuthorityService {
  constructor(private readonly fixtures: AuthorityInfo[] = []) {}

  async retrieve(query: AuthorityQuery): Promise<AuthorityInfo[]> {
    return this.fixtures.filter((a) => a.domain === query.domain && a.jurisdiction === query.jurisdiction);
  }
}

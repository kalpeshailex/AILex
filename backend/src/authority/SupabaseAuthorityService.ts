// Real AuthorityService implementation, backed by escalation_paths /
// authorities / authority_contacts (see backend/legal_kb_schema.sql,
// seeded from the user-provided Mumbai_Legal_KB_All_Domains_Populated.xlsx
// research pass). Every contact returned here traces back to a row a human
// researched and recorded (phone/portal), never invented by the LLM -- see
// the task's "never invent contact numbers" rule.
import { SupabaseClient } from "@supabase/supabase-js";
import { AuthorityService } from "./AuthorityService";
import { AuthorityInfo, AuthorityQuery } from "../schemas/types";

interface AuthorityRow {
  authority_id: string;
  name: string;
  jurisdiction: string;
}

interface ContactRow {
  authority_id: string;
  channel: string;
  value: string;
}

export class SupabaseAuthorityService implements AuthorityService {
  constructor(private readonly supabase: SupabaseClient) {}

  async retrieve(query: AuthorityQuery): Promise<AuthorityInfo[]> {
    const { data: paths, error: pathsError } = await this.supabase
      .from("escalation_paths")
      .select("authority_id")
      .eq("domain", query.domain.toLowerCase());
    if (pathsError || !paths || paths.length === 0) return [];

    const authorityIds = Array.from(
      new Set(paths.map((p) => (p as { authority_id: string | null }).authority_id).filter((id): id is string => !!id))
    );
    if (authorityIds.length === 0) return [];

    const { data: authorities, error: authoritiesError } = await this.supabase
      .from("authorities")
      .select("authority_id, name, jurisdiction")
      .in("authority_id", authorityIds);
    if (authoritiesError || !authorities) return [];

    const { data: contacts } = await this.supabase
      .from("authority_contacts")
      .select("authority_id, channel, value")
      .in("authority_id", authorityIds);

    return (authorities as AuthorityRow[]).map((authority) => {
      const ownContacts = ((contacts ?? []) as ContactRow[]).filter((c) => c.authority_id === authority.authority_id);
      const phone = ownContacts.find((c) => c.channel === "phone")?.value ?? null;
      const portal = ownContacts.find((c) => c.channel === "portal" || c.channel === "website")?.value ?? null;
      const verifiedContact =
        [phone ? `Phone: ${phone}` : null, portal ? `Portal: ${portal}` : null].filter(Boolean).join(", ") || null;

      return {
        authority_id: authority.authority_id,
        name: authority.name,
        domain: query.domain,
        jurisdiction: authority.jurisdiction,
        official_url: portal,
        verified_contact: verifiedContact,
        escalation_path: null,
        source_id: null,
      };
    });
  }
}

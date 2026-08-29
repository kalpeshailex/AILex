import { describe, expect, it } from "vitest";
import { SupabaseAuthorityService } from "../src/authority/SupabaseAuthorityService";
import { fakeSupabaseClient } from "./support/FakeSupabaseClient";

describe("SupabaseAuthorityService", () => {
  it("resolves escalation_paths -> authorities -> authority_contacts into AuthorityInfo with a real verified contact", async () => {
    const client = fakeSupabaseClient({
      escalation_paths: { data: [{ authority_id: "auth_001" }, { authority_id: "auth_001" }], error: null },
      authorities: { data: [{ authority_id: "auth_001", name: "I4C (Cyber Crime)", jurisdiction: "India" }], error: null },
      authority_contacts: {
        data: [
          { authority_id: "auth_001", channel: "phone", value: "1930" },
          { authority_id: "auth_001", channel: "portal", value: "https://cybercrime.gov.in/" },
        ],
        error: null,
      },
    });

    const service = new SupabaseAuthorityService(client);
    const result = await service.retrieve({ domain: "CYBER", jurisdiction: "Mumbai Metropolitan Region, Maharashtra" });

    expect(result).toHaveLength(1);
    expect(result[0].name).toBe("I4C (Cyber Crime)");
    expect(result[0].verified_contact).toBe("Phone: 1930, Portal: https://cybercrime.gov.in/");
    expect(result[0].official_url).toBe("https://cybercrime.gov.in/");
  });

  it("returns an empty array when no escalation path exists for the domain", async () => {
    const client = fakeSupabaseClient({ escalation_paths: { data: [], error: null } });
    const service = new SupabaseAuthorityService(client);
    const result = await service.retrieve({ domain: "CYBER", jurisdiction: "x" });
    expect(result).toEqual([]);
  });

  it("never invents a contact -- an authority with no contact row gets verified_contact=null", async () => {
    const client = fakeSupabaseClient({
      escalation_paths: { data: [{ authority_id: "auth_002" }], error: null },
      authorities: { data: [{ authority_id: "auth_002", name: "Local Police Station", jurisdiction: "Mumbai" }], error: null },
      authority_contacts: { data: [], error: null },
    });

    const service = new SupabaseAuthorityService(client);
    const result = await service.retrieve({ domain: "POLICE", jurisdiction: "x" });
    expect(result[0].verified_contact).toBeNull();
    expect(result[0].official_url).toBeNull();
  });

  it("returns an empty array on a query error rather than throwing", async () => {
    const client = fakeSupabaseClient({ escalation_paths: { data: null, error: { message: "boom" } } });
    const service = new SupabaseAuthorityService(client);
    const result = await service.retrieve({ domain: "CYBER", jurisdiction: "x" });
    expect(result).toEqual([]);
  });
});

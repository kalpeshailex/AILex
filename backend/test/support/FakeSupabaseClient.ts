// Minimal fake of the supabase-js query builder for unit tests. Real
// supabase-js builders are PromiseLike (calling .then() runs the query), so
// a chain that returns itself from every filter method and resolves on
// .then() is enough to stand in for `await supabase.from(...).select(...)...`.
import { SupabaseClient } from "@supabase/supabase-js";

export interface TableResponse {
  data: unknown[] | null;
  error: unknown | null;
}

export function fakeSupabaseClient(responses: Record<string, TableResponse>): SupabaseClient {
  const client = {
    from(table: string) {
      const response = responses[table] ?? { data: [], error: null };
      const chain: any = {
        select: () => chain,
        eq: () => chain,
        in: () => chain,
        then: (resolve: (value: TableResponse) => void, reject?: (reason: unknown) => void) =>
          Promise.resolve(response).then(resolve, reject),
      };
      return chain;
    },
  };
  return client as unknown as SupabaseClient;
}

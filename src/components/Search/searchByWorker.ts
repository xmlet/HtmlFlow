// Ambient types for the (untyped) deep import into the search-local plugin's
// worker bridge. Only the two functions we call are declared.
declare module '@easyops-cn/docusaurus-search-local/dist/client/client/theme/searchByWorker' {
  export interface SearchDocument {
    i: number; // id
    t: string; // title / heading text
    u: string; // url (route, may include hash)
    h?: string; // hash
    b?: string[]; // breadcrumb
    s?: string; // section / keywords text
    p?: number; // parent page id
  }

  export interface WorkerSearchResult {
    document: SearchDocument;
    type: number; // SearchDocumentType
    page?: SearchDocument | false;
    metadata?: unknown;
    tokens: string[];
    score: number;
  }

  export function fetchIndexesByWorker(baseUrl: string, searchContext: string): Promise<void>;
  export function searchByWorker(
    baseUrl: string,
    searchContext: string,
    input: string,
    limit: number
  ): Promise<WorkerSearchResult[]>;
}

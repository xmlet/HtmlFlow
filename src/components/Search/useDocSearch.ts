import { useEffect, useRef, useState } from 'react';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import {
  fetchIndexesByWorker,
  searchByWorker,
  type WorkerSearchResult,
} from '@easyops-cn/docusaurus-search-local/dist/client/client/theme/searchByWorker';

export type Area = 'Guide' | 'API Reference' | 'News' | 'Other';

export interface DocResult {
  title: string;
  snippet?: string;
  url: string;
  breadcrumb: string[];
  area: Area;
}

const TYPE_TITLE = 0;
const TYPE_HEADING = 1;

function truncate(s: string, n: number): string {
  const t = s.replace(/\s+/g, ' ').trim();
  return t.length > n ? `${t.slice(0, n).trimEnd()}…` : t;
}

export interface ResultGroup {
  area: Area;
  items: DocResult[];
}

const AREA_ORDER: Area[] = ['Guide', 'API Reference', 'News', 'Other'];
const RESULT_LIMIT = 12;
const DEBOUNCE_MS = 120;

function areaOf(url: string, baseUrl: string): Area {
  const path = url.startsWith(baseUrl) ? `/${url.slice(baseUrl.length)}` : url;
  if (path.startsWith('/docs/api-reference')) return 'API Reference';
  if (path.startsWith('/docs')) return 'Guide';
  if (path.startsWith('/blog')) return 'News';
  return 'Other';
}

function pageOf(result: WorkerSearchResult) {
  return result.page && typeof result.page !== 'boolean' ? result.page : undefined;
}

function toBreadcrumb(result: WorkerSearchResult): string[] {
  const page = pageOf(result);
  const crumbs = page ? [...(page.b ?? []), page.t] : [...(result.document.b ?? [])];
  return crumbs.filter((c, i) => c && c !== crumbs[i - 1]);
}

function groupResults(raw: WorkerSearchResult[], baseUrl: string): { groups: ResultGroup[]; total: number } {
  const seen = new Set<string>();
  const byArea = new Map<Area, DocResult[]>();

  for (const r of raw) {
    const url = r.document.u;
    if (!url || seen.has(url)) continue;
    seen.add(url);
    const area = areaOf(url, baseUrl);
    const isTitleLike = r.type === TYPE_TITLE || r.type === TYPE_HEADING;
    const pageTitle = pageOf(r)?.t;
    const item: DocResult = {
      title: isTitleLike ? r.document.t : (pageTitle ?? r.document.t),
      snippet: isTitleLike ? undefined : truncate(r.document.t, 110),
      url,
      breadcrumb: toBreadcrumb(r),
      area,
    };
    const list = byArea.get(area) ?? [];
    list.push(item);
    byArea.set(area, list);
  }

  const groups: ResultGroup[] = [];
  let total = 0;
  for (const area of AREA_ORDER) {
    const items = byArea.get(area);
    if (items && items.length) {
      groups.push({ area, items });
      total += items.length;
    }
  }
  return { groups, total };
}

/**
 * Queries the search-local lunr index through the plugin's web worker and
 * groups hits by area. NOTE: the worker only runs in production builds; under
 * `yarn start` it returns no results (warmup + search resolve empty).
 */
export function useDocSearch(query: string) {
  const {
    siteConfig: { baseUrl },
  } = useDocusaurusContext();
  const [groups, setGroups] = useState<ResultGroup[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const warmed = useRef(false);
  const reqId = useRef(0);

  const warmup = () => {
    if (warmed.current) return;
    warmed.current = true;
    void fetchIndexesByWorker(baseUrl, '').catch(() => {
      /* no-op: index unavailable (e.g. dev) */
    });
  };

  useEffect(() => {
    const trimmed = query.trim();
    if (!trimmed) {
      setGroups([]);
      setTotal(0);
      setLoading(false);
      return;
    }
    setLoading(true);
    const id = ++reqId.current;
    const timer = setTimeout(async () => {
      try {
        const raw = await searchByWorker(baseUrl, '', trimmed, RESULT_LIMIT);
        if (id !== reqId.current) return;
        const { groups: g, total: t } = groupResults(raw, baseUrl);
        setGroups(g);
        setTotal(t);
      } catch {
        if (id !== reqId.current) return;
        setGroups([]);
        setTotal(0);
      } finally {
        if (id === reqId.current) setLoading(false);
      }
    }, DEBOUNCE_MS);
    return () => clearTimeout(timer);
  }, [query, baseUrl]);

  return { groups, total, loading, warmup };
}

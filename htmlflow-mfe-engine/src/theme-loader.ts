/**
 * Shared, module-level singleton cache for the theme/stylesheet loader.
 *
 * Provides `loadStylesheet`/`adoptStylesheet` used by `base.ts` to fetch and share a single
 * `CSSStyleSheet` (via Constructable Stylesheets) across every `<micro-frontend>` fragment's
 * shadow root and, when propagated, the host document itself — so a shared theme is fetched
 * and parsed only once no matter how many fragments request it concurrently.
 */

const styleSheetCache = new Map<string, Promise<CSSStyleSheet>>();

/**
 * Loads the stylesheet at `url` (via {@link loadStylesheet}, reusing the shared cache) and
 * adopts it onto `target.adoptedStyleSheets`, guarding against adding a duplicate entry if the
 * same sheet has already been inserted. The guard matters specifically for `Document`
 * targets, since multiple independent fragments may all target the same `document.adoptedStyleSheets`
 * array.
 * @param target - either `document` (to theme the host shell) or a fragment's `ShadowRoot`.
 * @param url - URL of the CSS resource to fetch, parse, and adopt.
 */
export async function adoptStylesheet(target: Document | ShadowRoot, url: string): Promise<void> {
    const sheet = await loadStylesheet(url);
    if (!target.adoptedStyleSheets.includes(sheet)) {
        target.adoptedStyleSheets = [...target.adoptedStyleSheets, sheet];
    }
}

/**
 * Fetches and parses the stylesheet at `url` into a `CSSStyleSheet`. It removes duplication
 * and repeated requests for the same URL via {@link styleSheetCache}.
 * @param url - URL of the CSS resource to fetch and parse.
 * @returns a promise for the parsed `CSSStyleSheet`, shared by every caller requesting the same URL.
 */
export function loadStylesheet(url: string): Promise<CSSStyleSheet> {
    let pending = styleSheetCache.get(url);
    if (!pending) {
        pending = fetchAndParse(url).catch(err => {
            styleSheetCache.delete(url);
            throw err;
        });
        styleSheetCache.set(url, pending);
    }
    return pending;
}

/**
 * Performs the actual network fetch + `CSSStyleSheet` construction for a single URL
 * @param url - URL of the CSS resource to fetch.
 * @throws if the network request fails or responds with a non-OK status.
 */
async function fetchAndParse(url: string): Promise<CSSStyleSheet> {
    const res = await fetch(url);
    if (!res.ok) {
        throw new Error(`Failed to fetch stylesheet "${url}": ${res.status}`);
    }
    const css = await res.text();
    const sheet = new CSSStyleSheet();
    sheet.replaceSync(css);
    return sheet;
}


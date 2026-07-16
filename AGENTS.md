# HtmlFlow Website (htmlflow.org)

Docusaurus 3.10 site for the [HtmlFlow](https://github.com/xmlet/HtmlFlow) Java/Kotlin HTML library. This repo is the website only — the library itself lives at `xmlet/HtmlFlow`.

Currently on branch `website-refactor`: a ground-up redesign replacing the legacy Docusaurus tutorial scaffolding with a custom landing page, custom navbar/sidebar theming, and a new docs information architecture.

## Stack

- **Docusaurus** 3.10.1 with `@docusaurus/faster` (Rspack) and `future.v4` enabled
- **React** 19
- **TypeScript** 5.6 (upgrading to 6.0 requires separate validation — skip for now)
- **Tailwind CSS** 4.1 (via `@tailwindcss/postcss`, configured through a custom plugin at `src/plugins/tailwind-config.js` since Docusaurus owns the PostCSS pipeline)
- **Search**: `@easyops-cn/docusaurus-search-local` (local index, no Algolia; worker only runs in production — `yarn start` returns no results)
- **Code highlighting**: `prism-react-renderer` + `react-syntax-highlighter`, additional languages `java`, `kotlin`
- **Tooling**: Prettier 3.8+, Husky, lint-staged 17+, Puppeteer (dev dep)
- **Node**: >=20

## Layout

```
docs/                       Top-level doc pages (.mdx, flat — no subdirs)
  introduction.mdx          Sidebar position 1 — What/Why/How
  getting-started.mdx       2 — Installation + Quick Start
  core-concepts.mdx         3 — Builders, eager/lazy, models, control flow
  advanced.mdx              4 — Async rendering, streaming, Flowifier
                            (sidebar_label: Advanced overrides the h1)
  integrations.mdx          5 — data-*, http4k, ...
  resources.mdx             6 — Articles, talks, repos
blog/                       Release notes (2017 → 2026-03)
  *-Release-*.md            One file per HtmlFlow release
  authors.yml, tags.yml
sidebars.ts                 GENERATOR, not a manual list: reads each docs/*.mdx
                            at config-load time, extracts `##` headings, and
                            emits one category per doc whose items are
                            hash-anchor links to those headings. Order from
                            sidebar_position, label from sidebar_label (fallback
                            h1).
docusaurus.config.ts        Site config. `LATEST_VERSION` constant feeds the
                            custom version pill via `customFields.latestVersion`.
src/
  pages/index.tsx           Landing page (composes home components)
  components/
    home/                   Landing-page sections (Hero, Features,
                            CodeComparison, WaveBackground)
    Search/                 Custom search overlay (SearchOverlay, SidebarSearchField,
                            useDocSearch, searchData)
    ui/                     Reusable primitives (button.tsx etc.) — shadcn-ish,
                            uses `class-variance-authority` + `clsx` +
                            `tailwind-merge` via `utils.ts`
  theme/                    Swizzled Docusaurus theme components
    Root.tsx                Wraps the whole app; owns the sidebar scroll-spy
                            (`hash-active` class). Only headings whose id has a
                            matching sidebar anchor link are tracked — h3s
                            without sidebar entries are ignored on purpose.
    NavbarItem/             Custom navbar item types registered via
                            ComponentTypes.tsx: custom-version-pill,
                            custom-github, custom-rss, custom-theme-toggle,
                            custom-divider
    DocSidebarItem/         Custom sidebar styling (sidebar.css)
    Navbar/MobileSidebar/   Custom mobile drawer with card nav + drawer footer
  css/                      Global CSS (all listed in customCss array in config)
    custom.css              Root vars, navbar, footer, misc overrides
    mobile-sidebar.css      Mobile drawer cards, search field, search overlay
    blog.css                Blog-specific layout
    pagination.css          Pagination nav overrides
  plugins/
    tailwind-config.js      Docusaurus plugin that injects Tailwind into
                            the PostCSS pipeline
    agent-ready.js          postBuild generator: markdown mirrors of every
                            docs page (/docs/<id>/index.md), /llms.txt, and
                            /llms-full.txt — all derived from docs/*.mdx
static/                     Static assets (favicon, logos under img/),
                            robots.txt (Content-Signal + sitemap pointer)
.github/workflows/
  website-build.yml         Build-only CI: install, typecheck, format:check,
                            build, then asserts the generated agent-readiness
                            artifacts exist. No deploy — that stays manual.
```

## Information architecture

Each top-level section is **one long `.mdx` page** with `## section` headings; the sidebar shows sub-navigation as hash-anchor links into that page. Those anchor items are **not hand-written**: `sidebars.ts` is a generator that parses each `docs/*.mdx` at config-load time (skipping fenced code blocks) and emits a category per doc with a `link` item per `##` heading. The heading text is the single source of truth — **rename/add/remove a `## heading` and the sidebar follows on next build/start**. The generator's `slugify()` mirrors github-slugger (which Docusaurus uses for heading ids); if a heading uses exotic characters, verify the generated anchor matches the rendered heading id.

Adding a real sub-page (instead of an anchor) means adding a doc file and extending the generator's output shape — the current design assumes one file per topic.

All doc pages have `hide_table_of_contents: true` in frontmatter — the right-side ToC is intentionally suppressed because the sidebar already lists every `##` heading as an anchor link, so a ToC would be redundant.

The former `api-reference.mdx` page was removed entirely — doc file, sidebar category, footer/mobile-nav/search-data links, and the `'API Reference'` search `Area`. There is deliberately no `docs/index.mdx` overview page — `/docs/<first topic>` is the docs entry point.

### Agent-readiness artifacts

`src/plugins/agent-ready.js` (postBuild) generates from `docs/*.mdx`: a plain-markdown mirror of each docs page at `/docs/<id>/index.md`, `/llms.txt` (docs index for AI agents; per-page line uses the `description:` frontmatter — keep it present on every docs page), and `/llms-full.txt` (all docs concatenated). `Root.tsx` adds `<link rel="alternate" type="text/markdown">` on docs pages pointing at the mirror. `static/robots.txt` carries the Content-Signal policy (`search=yes, ai-input=yes, ai-train=yes`). Everything regenerates on every build; CI (`website-build.yml`) asserts the artifacts exist. **Gotcha:** the mdx→md conversion is regex-based — if a new JSX component is used inside a docs `.mdx`, add a conversion rule in `agent-ready.js` (CI's `! grep "<TabItem"` check only catches Tabs leftovers).

**Bilingual code examples** use Docusaurus Tabs: `import Tabs from '@theme/Tabs'` and `import TabItem from '@theme/TabItem'` after the frontmatter, then `<Tabs groupId="lang">` with `java` and `kotlin` `<TabItem>`s. The shared `groupId="lang"` syncs the Java/Kotlin choice across every block and page. Screenshot placeholders are MDX comments: `{/* SCREENSHOT: ... */}`.

## Custom navbar items

Registered in `src/theme/NavbarItem/ComponentTypes.tsx` and referenced by `type: 'custom-*'` strings in `docusaurus.config.ts`:

- `custom-version-pill` — shows `LATEST_VERSION` from config
- `custom-github` — GitHub icon link
- `custom-rss` — RSS icon link to `/blog/rss.xml`
- `custom-theme-toggle` — manual light/dark toggle (note: `colorMode.disableSwitch: true` disables the default, this replaces it)
- `custom-divider` — visual separator

## Versioning

Release version is a single constant `LATEST_VERSION` at the top of `docusaurus.config.ts` (currently `5.0.4`). Bumping it updates the navbar pill and any code reading `siteConfig.customFields.latestVersion`. There is no Docusaurus docs-versioning enabled — only one set of docs.

## Keeping this file current

**After every non-trivial change, update this file.** If you add a new component, CSS class, CSS var, gotcha, or architectural pattern — document it here before committing. This file is the source of truth for how the codebase works; stale docs are worse than none.

Specifically update when you:

- Add a new `--hf-*` CSS custom property → add it to the design tokens table
- Add a new `hf-*` CSS class → add it to the class naming section
- Add a swizzled theme component → document its location and purpose
- Discover a new Docusaurus/Tailwind/React gotcha → add it to Gotchas
- Change the sidebar structure or information architecture
- Add new `src/css/*.css` files or change which files are in `customCss`

## Common tasks

- `yarn start` — dev server (Rspack/Faster)
- `yarn build` — production build (output: `build/`)
- `yarn serve` — serve the built site
- `yarn clear` — clear `.docusaurus/` cache when things get weird
- `yarn typecheck` — `tsc --noEmit`
- `yarn format` / `yarn format:check` — Prettier
- `yarn swizzle` — swizzle a theme component (used when adding to `src/theme/`)

`onBrokenLinks: 'throw'` is set, so dead internal links fail the build — fix them, don't downgrade the setting.

## CSS architecture

### File responsibilities

| File                               | Owns                                                                                               |
| ---------------------------------- | -------------------------------------------------------------------------------------------------- |
| `custom.css`                       | `:root` vars, dark theme vars, navbar, footer, global overrides                                    |
| `mobile-sidebar.css`               | Mobile drawer cards (`.hf-card-*`), search field (`.hf-search-field`), search overlay (`.hf-ov-*`) |
| `blog.css`                         | Blog layout tweaks                                                                                 |
| `pagination.css`                   | Pagination nav overrides                                                                           |
| `theme/DocSidebarItem/sidebar.css` | Sidebar menu item styles, fighting Docusaurus defaults                                             |

### CSS custom properties (design tokens)

Defined in `custom.css` `:root` / dark block. Always add new tokens here — never inline in component CSS.

**Site-specific vars (prefixed `--hf-`):**

| Var                     | Light       | Dark                  | Usage                   |
| ----------------------- | ----------- | --------------------- | ----------------------- |
| `--hf-navbar-icon`      | `#1f2937`   | `#ffffff`             | Navbar icon color       |
| `--hf-bg-deep`          | _(not set)_ | `#0a0f1c`             | Dark panel/footer bg    |
| `--hf-overlay-backdrop` | _(not set)_ | `rgba(15,23,42,0.45)` | Search overlay backdrop |

**Dark mode opacity scale** (white-on-dark, used in `mobile-sidebar.css` and `sidebar.css`):

| Level    | Value                    | Used for                           |
| -------- | ------------------------ | ---------------------------------- |
| subtle   | `rgba(255,255,255,0.03)` | Card background                    |
| muted    | `rgba(255,255,255,0.05)` | Hover/active rows                  |
| quiet    | `rgba(255,255,255,0.06)` | Icon backgrounds, category borders |
| standard | `rgba(255,255,255,0.08)` | Borders                            |
| strong   | `rgba(255,255,255,0.16)` | Hover borders                      |

Don't introduce new opacity levels without updating this table.

### CSS class naming

All custom (non-Docusaurus) classes use the `hf-` prefix:

- `.hf-card`, `.hf-card__icon`, `.hf-card--active` — BEM-style, mobile nav cards
- `.hf-ov`, `.hf-ov-panel`, `.hf-ov-bar` — search overlay
- `.hf-search-field` — mobile drawer search trigger
- `.hf-code-block` — wrapper for `SyntaxHighlighter` panels (sets `pre { max-width: 100% }`)
- `.hf-sidebar-pill` — sliding highlight behind the active desktop-sidebar link; injected imperatively by `Root.tsx`, positioned via inline transform. Desktop only — the mobile drawer uses a plain `hash-active` background instead
- `.hf-drawer-footer`, `.hf-ghstars` — mobile drawer footer
- `.hf-navbar-icon` — navbar icon buttons (via `custom.css`)

### Tailwind usage rules

This project uses Tailwind v4 (`@import "tailwindcss"` in `custom.css`). Rules:

- Use standard Tailwind utilities for layout and landing-page components (Hero, Features, CodeComparison)
- **Do not use arbitrary values** (`gap-[0.55rem]`, `z-[1]`) — use a standard scale value or extract to a CSS class
- **Do not use arbitrary descendant selectors** (`[&_code]:bg-transparent`) — extract to a named CSS class (e.g. `.hf-code-block`) in the appropriate CSS file
- Dark mode in components: prefer `dark:` Tailwind prefix for Tailwind-based components; use `html[data-theme='dark']` in CSS files
- `code { background: transparent !important }` is global — `[&_code]:bg-transparent` is always redundant
- **Inline code is a transparent, bordered "chip", not a filled box.** Its background is transparent (above); the chip look comes from infima's `:not(pre) > code` border, a literal `rgba(0,0,0,0.1)` that's invisible on the dark background. `custom.css` adds `html[data-theme='dark'] :not(pre) > code { border-color: rgba(255,255,255,0.08) }` so the border matches in dark. Code blocks (`pre code`) have no border — keep dark overrides scoped to `:not(pre) > code`.

### `!important` policy

Use `!important` only when overriding Docusaurus internals from external CSS. Required in:

- `sidebar.css` — fighting Docusaurus specificity on menu links (acceptable)
- `prefers-reduced-motion` block — WCAG 2.1 pattern, correct use

Never use `!important` on CSS custom property declarations — it has no effect there.

## React patterns

### Component conventions

- Return type is `ReactNode` for all components (not `JSX.Element`)
- Custom hooks: `use` prefix, live in same directory as the component that owns them
- Static data arrays (CARDS, JUMP_TO): defined at module level, not inside components
- CSS class names: `hf-` prefix for custom; `ThemeClassNames.*` for Docusaurus internals

### Swizzled theme components (`src/theme/`)

When swizzling Docusaurus components:

- Use `yarn swizzle` — don't copy-paste files manually
- Import Docusaurus types from `@theme/…` (not relative paths)
- Two known `as any` casts are intentional: `NavbarMobileSidebar/Layout` (Docusaurus v4 `inert` workaround, marked TODO) — leave them
- Return types from NavbarItem components: always `ReactNode`, not `JSX.Element`

### `useEffect` rules

- Always include all referenced variables in the deps array
- Stable functions used in effects must be wrapped in `useCallback` at their definition site
- One-shot mount effects (e.g. `warmup()`) must have a stable function ref — `useCallback` the function, then include it in deps

### Search component

`useDocSearch` (in `src/components/Search/`) queries the lunr index via a web worker. The `warmup` function is `useCallback`-stabilized — add it to `useEffect` deps when used. The worker **only runs in production builds** — dev server always returns empty results.

## TypeScript

- `tsconfig.json` inherits from `@docusaurus/tsconfig` — adds strict settings transitively
- Avoid `as any` — if a Docusaurus type mismatch forces a cast, use the narrowest correct type (e.g. `as Props['item']`)
- TypeScript 6.0 is available but not yet adopted — validate breaking changes before upgrading

## Gotchas

- **PostCSS / Tailwind**: don't add a top-level `postcss.config.js` — Docusaurus owns PostCSS, the Tailwind plugin at `src/plugins/tailwind-config.js` is the right hook. Tailwind v4 syntax (`@import "tailwindcss";` in `custom.css`), not v3.
- **React 19**: some Docusaurus theme code and third-party React libs may warn; check before pinning down to 18.
- **Sidebar anchors are generated from headings** by `sidebars.ts`; its `slugify()` must keep matching Docusaurus's github-slugger output. Renaming a heading regenerates the sidebar link automatically, but changes the anchor URL (old deep links to that anchor break silently). Renaming a `.mdx` file changes its route and breaks inbound links — no redirects plugin configured.
- **Blog posts span 2017–2026** with relative links to GitHub releases. Don't bulk-rewrite them without checking — they're historical record. The entire `blog/` directory is excluded from Prettier formatting intentionally.
- **Search index** is built at build-time; if search results look stale, `yarn clear && yarn build`.
- **Links to plugin-generated assets need `pathname://`**: the broken-link checker only knows SPA routes, so a `<Link>`/config link to a build-time file like `/blog/rss.xml` fails the build. Use `pathname:///blog/rss.xml` (see the footer RSS item). Raw `<a href>` in custom components (navbar `custom-rss`, drawer footer) is not collected by the checker and can keep plain paths.
- **Don't run `yarn build` while `yarn start` is live**: both share `node_modules/.cache/rspack/`, so a concurrent build deletes pack files the dev server's persistent cache holds, crashing it with an Rspack panic (`SIGABRT`, e.g. `should have bucket pack metas` or `ModuleGraphModule ... not found`). To link-check, stop the dev server first. Recovery: `yarn clear` then restart `yarn start` (the same flaky persistent-cache panic can also hit a cold start — just retry after `yarn clear`).
- **`sidebar.css` selector specificity**: multi-line CSS selectors with `>` combinators must be on a single line or properly indented — a stray `>` at the start of a line becomes an orphan rule that matches everything.
- **`SyntaxHighlighter` inline styles**: the `customStyle` prop is required by the API and can't be removed. Color values there (`#111827`) are intentional — don't flag them as hardcoded magic.
- **`--doc-sidebar-width`**: the `!important` flag on a CSS custom property declaration is a no-op — don't add it.
- **Mobile search overlay**: `hf-search-field` dark theme overrides live in `mobile-sidebar.css` in the dark theme block at the bottom, not next to the base rule. When adding new states to `.hf-search-field`, add base styles near the definition (~line 25) and dark overrides in the dark theme block.
- **Chrome freezes CSS transitions in hidden subtrees** (e.g. the closed mobile drawer): a property change made while the subtree is hidden starts a transition that never ticks, so the computed value stays stuck at the start value — even inline `!important` reads back wrong because the frozen transition interpolation wins. This is why the drawer's `hash-active` background and the sidebar pill must not rely on transitions inside `.navbar-sidebar` (see `sidebar.css` drawer override and the `positionPill` drawer guard in `Root.tsx`).
- **Sidebar categories are `level-1`, links are `level-2`** in the rendered DOM — selectors targeting `theme-doc-sidebar-item-category-level-2` match nothing.

# Website

This is the source for [htmlflow.org](https://htmlflow.org), built with
[Docusaurus](https://docusaurus.io/).

## Installation

```bash
yarn
```

## Local Development

```bash
yarn start
```

Starts a dev server (default http://localhost:3000) with hot reload — most
changes appear live without a restart.

## Build

```bash
yarn build
```

Generates the static site into `build/`. Preview the production build with:

```bash
yarn serve
```

(`serve` builds first, then serves `build/` locally.)

## Repository layout

| Path                   | What lives there                                                |
| ---------------------- | --------------------------------------------------------------- |
| `docs/`                | Documentation pages (`*.mdx`). One file = one sidebar section.  |
| `blog/`                | News / release posts (`*.md`), plus `authors.yml`, `tags.yml`.  |
| `src/pages/`           | Standalone pages. `index.tsx` is the home page.                 |
| `src/theme/`           | Swizzled Docusaurus components (custom sidebar, root, etc.).    |
| `src/css/`             | Global styles.                                                  |
| `static/`              | Files served as-is: images, `robots.txt`, `CNAME`, `.nojekyll`. |
| `docusaurus.config.ts` | Site config: navbar, footer, plugins, redirects, version.       |
| `sidebars.ts`          | Sidebar generator (see below).                                  |

## How the docs sidebar works

The sidebar is **not** written by hand. It is generated in `sidebars.ts` from
the docs files:

- **Each top-level file** in `docs/` (e.g. `docs/getting-started.mdx`) becomes
  one collapsible **section** in the sidebar.
- **Each `## heading`** inside that file becomes a **sub-item** under that
  section, linking to the heading's anchor. (Headings inside fenced code blocks
  are ignored.)

So there is one source of truth: edit the page, and the sidebar follows.

### Add a documentation section (new page)

1. Create `docs/my-topic.mdx`.
2. Add frontmatter:

   ```md
   ---
   sidebar_position: 7 # order among sections (lower = higher up)
   sidebar_label: My Topic # optional; falls back to the page's `# Title`
   description: One-line summary for SEO / social cards.
   hide_table_of_contents: true # the sidebar already lists the ## headings
   ---

   # My Topic

   ## First section

   ...

   ## Second section

   ...
   ```

The page shows up as a new sidebar section with `First section` /
`Second section` as its sub-items, reachable at `/docs/my-topic`.

### Add a sub-item to an existing section

Just add a `## Heading` to that page. It automatically becomes a sidebar
sub-item with a matching anchor. No edit to `sidebars.ts` needed.

### Change section order or label

- **Order**: set `sidebar_position` (integer) in the page's frontmatter. Files
  with no position sort last (`999`).
- **Label**: set `sidebar_label`; otherwise the page's `# Title` is used.

### Edit an existing page

Edit the `.mdx` file directly. Java/Kotlin code samples use tabs:

````mdx
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs groupId="lang">
  <TabItem value="java" label="Java">
    ```java ... ```
  </TabItem>
  <TabItem value="kotlin" label="Kotlin">
    ```kotlin ... ```
  </TabItem>
</Tabs>
````

## Add a news / release post

Create `blog/YYYY-MM-DD-slug.md` (the date prefix sets the post date and URL):

```text
---
title: Release 5.1
date: 2026-08-01
authors: [miguel]
tags: [release]
description: One-line summary.
---

Short intro shown in the post list. {/* truncate */}

Full body below the fold.
```

- `{/* truncate */}` marks where the list excerpt is cut.
- Authors are defined in `blog/authors.yml`; tags in `blog/tags.yml`.

## Home page

Edit `src/pages/index.tsx`.

## Navbar, footer, and the "latest version" badge

All in `docusaurus.config.ts`:

- Navbar / footer links: the `navbar` and `footer` keys.
- Latest release number: the `LATEST_VERSION` constant (exposed to pages via
  `customFields.latestVersion`). Bump it on each release.

## URL redirects

Old URLs are mapped to their new locations via
`@docusaurus/plugin-client-redirects` in `docusaurus.config.ts`. Add a
`{ from, to }` entry there when a page moves so external links keep working.

## Deployment

`gh-pages` is the **site source** branch, not the served output. GitHub Pages
for this repo is configured with `build_type: workflow`, so nothing is served
straight from a branch — the live site comes from whatever
`.github/workflows/website-build.yml` last published.

To release: merge your work into `gh-pages` and push.

```bash
git switch gh-pages
git merge website
git push origin gh-pages
```

That push runs the `build` job (typecheck, format, build, artifact checks), and
only on success does the `deploy` job publish to Pages via
`actions/deploy-pages`. Pushes to any other branch build but never deploy, so
`website` is safe to use as the working branch.

The custom domain comes from `static/CNAME` (`htmlflow.org`), which the build
copies into `build/`. CI asserts both it and `build/.nojekyll` exist, so losing
the domain fails the build instead of the live site.

> Do **not** run `yarn deploy`. `docusaurus deploy` force-pushes built output
> onto `gh-pages`, which would overwrite the source. It is unused here.

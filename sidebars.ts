import fs from 'fs';
import path from 'path';
import type { SidebarsConfig } from '@docusaurus/plugin-content-docs';

/**
 * The sidebar is generated from the `##` headings of each top-level doc page.
 * One source of truth: rename/add/remove a `## heading` in a docs/*.mdx file and
 * the sidebar follows automatically. Category order comes from `sidebar_position`
 * frontmatter; category label from `sidebar_label` frontmatter (fallback: the
 * page's `# Title`).
 *
 * Parsing is line-based (no regexes) and skips fenced code blocks.
 */
const DOCS_DIR = path.join(__dirname, 'docs');

/**
 * Replicates github-slugger (which Docusaurus uses for heading anchors) closely
 * enough for our headings: lowercase, strip punctuation, spaces become hyphens.
 * E.g. "What is HtmlFlow?" -> "what-is-htmlflow", "data-*" -> "data-".
 */
function slugify(text: string): string {
  let slug = '';
  for (const ch of text.toLowerCase().trim()) {
    if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch === '-' || ch === '_') slug += ch;
    else if (ch === ' ') slug += '-';
    // any other character (punctuation) is dropped, like github-slugger does
  }
  return slug;
}

/** Unescape MDX backslash escapes, e.g. "data-\*" -> "data-*". */
function unescapeMdx(text: string): string {
  let out = '';
  for (let i = 0; i < text.length; i++) {
    if (text[i] === '\\' && i + 1 < text.length) {
      out += text[i + 1];
      i++;
    } else {
      out += text[i];
    }
  }
  return out;
}

/** Value of a `key: value` frontmatter line, or undefined. */
function frontmatterValue(lines: string[], key: string): string | undefined {
  if (lines[0] !== '---') return undefined;
  for (let i = 1; i < lines.length && lines[i] !== '---'; i++) {
    const separator = lines[i].indexOf(':');
    if (separator !== -1 && lines[i].slice(0, separator).trim() === key) {
      return lines[i].slice(separator + 1).trim();
    }
  }
  return undefined;
}

interface TopicDoc {
  id: string;
  position: number;
  label: string;
  headings: string[]; // `##` heading texts, in document order
}

/**
 * Returns null for files without `sidebar_position`, the opt-in marker for a
 * published topic page. Before this guard the glob below swept up any draft
 * left in docs/ and put it in the nav.
 */
function parseDoc(filename: string): TopicDoc | null {
  const raw = fs.readFileSync(path.join(DOCS_DIR, filename), 'utf8');
  const id = filename.slice(0, filename.lastIndexOf('.'));
  const lines = raw.split('\n');

  const rawPosition = frontmatterValue(lines, 'sidebar_position');
  if (rawPosition === undefined) return null;
  const position = Number(rawPosition);
  const sidebarLabel = frontmatterValue(lines, 'sidebar_label');

  // Collect headings, ignoring lines inside fenced code blocks.
  let inFence = false;
  let title = id;
  const headings: string[] = [];
  for (const line of lines) {
    if (line.startsWith('```')) {
      inFence = !inFence;
      continue;
    }
    if (inFence) continue;
    if (line.startsWith('# ')) title = unescapeMdx(line.slice(2).trim());
    if (line.startsWith('## ')) headings.push(unescapeMdx(line.slice(3).trim()));
  }

  return { id, position, label: sidebarLabel ?? title, headings };
}

const topicDocs = fs
  .readdirSync(DOCS_DIR)
  .filter(f => f.endsWith('.mdx') || f.endsWith('.md'))
  .map(parseDoc)
  .filter((doc): doc is TopicDoc => doc !== null)
  .sort((a, b) => a.position - b.position);

const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    ...topicDocs.map(doc => ({
      type: 'category' as const,
      label: doc.label,
      collapsed: false,
      collapsible: true,
      link: { type: 'doc' as const, id: doc.id },
      items: doc.headings.map(heading => ({
        type: 'link' as const,
        label: heading,
        href: `/docs/${doc.id}#${slugify(heading)}`,
      })),
    })),
  ],
};

export default sidebars;

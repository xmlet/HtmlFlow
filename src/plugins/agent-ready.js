// Agent-readiness generator.
// At postBuild it emits, from the docs/*.mdx sources:
//   - <outDir>/docs/<id>/index.md   plain-markdown mirror of each docs page
//   - <outDir>/llms.txt             index of the docs for LLM agents
//   - <outDir>/llms-full.txt        all docs concatenated as one markdown file
// Parsing is line-based (no regexes) and fence-aware: lines inside ``` code
// blocks are never touched. If a new JSX component is used inside a docs .mdx
// file, add a conversion rule in convertLine below.
const fs = require('fs');
const path = require('path');

/** Extract the value of an attribute like label="Java" from a JSX tag line. */
function jsxAttr(line, name) {
  const marker = `${name}="`;
  const start = line.indexOf(marker);
  if (start === -1) return null;
  const valueStart = start + marker.length;
  const valueEnd = line.indexOf('"', valueStart);
  if (valueEnd === -1) return null;
  return line.slice(valueStart, valueEnd);
}

/**
 * Convert one non-fence line of MDX to markdown.
 * Returns null to drop the line entirely.
 */
function convertLine(line, siteUrl) {
  const trimmed = line.trim();
  // MDX imports (`import X from '...'`) — code-fence lines never reach here
  if (trimmed.startsWith('import ') && (trimmed.includes(" from '") || trimmed.includes(' from "'))) return null;
  // Standalone MDX comments, e.g. {/* SCREENSHOT: ... */}
  if (trimmed.startsWith('{/*') && trimmed.endsWith('*/}')) return null;
  // Tabs wrappers and TabItem closers disappear
  if (trimmed.startsWith('<Tabs') || trimmed === '</Tabs>' || trimmed === '</TabItem>') return null;
  // <TabItem value="java" label="Java"> becomes a bold language label
  if (trimmed.startsWith('<TabItem')) {
    const label = jsxAttr(trimmed, 'label');
    return label ? `**${label}**` : null;
  }
  // Root-relative markdown links become absolute
  return line.split('](/').join(`](${siteUrl}/`);
}

/** Strip MDX-isms, producing plain markdown an agent can consume directly. */
function mdxToMarkdown(raw, siteUrl) {
  const lines = raw.split('\n');
  const out = [];
  let inFence = false;
  let index = 0;

  // Skip the frontmatter block
  if (lines[0] === '---') {
    index = 1;
    while (index < lines.length && lines[index] !== '---') index++;
    index++; // past the closing ---
  }

  for (; index < lines.length; index++) {
    const line = lines[index];
    if (line.startsWith('```')) {
      inFence = !inFence;
      out.push(line);
      continue;
    }
    if (inFence) {
      out.push(line);
      continue;
    }
    const converted = convertLine(line, siteUrl);
    if (converted === null) continue;
    // Collapse runs of blank lines left behind by dropped lines
    if (converted.trim() === '' && (out.length === 0 || out[out.length - 1].trim() === '')) continue;
    out.push(converted);
  }

  return out.join('\n').trim() + '\n';
}

/** Read the frontmatter block into a key -> value map (values as raw strings). */
function parseFrontmatter(raw) {
  const lines = raw.split('\n');
  const fields = {};
  if (lines[0] !== '---') return fields;
  for (let i = 1; i < lines.length && lines[i] !== '---'; i++) {
    const separator = lines[i].indexOf(':');
    if (separator === -1) continue;
    fields[lines[i].slice(0, separator).trim()] = lines[i].slice(separator + 1).trim();
  }
  return fields;
}

/** First `# ` heading outside code fences. */
function findTitle(raw) {
  let inFence = false;
  for (const line of raw.split('\n')) {
    if (line.startsWith('```')) {
      inFence = !inFence;
      continue;
    }
    if (!inFence && line.startsWith('# ')) return line.slice(2).trim();
  }
  return '';
}

module.exports = function agentReadyPlugin(context) {
  return {
    name: 'agent-ready-plugin',
    async postBuild({ outDir, siteConfig }) {
      const siteUrl = siteConfig.url;
      const docsDir = path.join(context.siteDir, 'docs');

      const docs = fs
        .readdirSync(docsDir)
        .filter(f => f.endsWith('.mdx') || f.endsWith('.md'))
        .map(filename => {
          const raw = fs.readFileSync(path.join(docsDir, filename), 'utf8');
          const id = filename.slice(0, filename.lastIndexOf('.'));
          const fm = parseFrontmatter(raw);
          return {
            id,
            raw,
            position: Number(fm.sidebar_position ?? 999),
            description: fm.description ?? '',
            sidebarLabel: fm.sidebar_label,
            title: findTitle(raw),
          };
        })
        .sort((a, b) => a.position - b.position);

      // Markdown mirrors: /docs/<id>/index.md
      for (const doc of docs) {
        const markdown = mdxToMarkdown(doc.raw, siteUrl);
        const target = path.join(outDir, 'docs', doc.id, 'index.md');
        fs.mkdirSync(path.dirname(target), { recursive: true });
        fs.writeFileSync(target, markdown);
      }

      // llms.txt
      const llms = [
        `# ${siteConfig.title}`,
        '',
        `> ${siteConfig.tagline}`,
        '',
        '## Documentation',
        '',
        ...docs.map(d => {
          const label = d.sidebarLabel ?? d.title ?? d.id;
          const desc = d.description ? `: ${d.description}` : '';
          return `- [${label}](${siteUrl}/docs/${d.id}/index.md)${desc}`;
        }),
        '',
        '## Releases',
        '',
        `- [Release notes](${siteUrl}/blog)`,
        `- [RSS](${siteUrl}/blog/rss.xml)`,
        '',
        '## Source',
        '',
        '- [GitHub](https://github.com/xmlet/HtmlFlow)',
        '',
      ].join('\n');
      fs.writeFileSync(path.join(outDir, 'llms.txt'), llms);

      // llms-full.txt
      const full = docs.map(d => mdxToMarkdown(d.raw, siteUrl)).join('\n\n---\n\n');
      fs.writeFileSync(path.join(outDir, 'llms-full.txt'), full);
    },
  };
};

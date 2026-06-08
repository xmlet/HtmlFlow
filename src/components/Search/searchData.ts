export type JumpIcon = 'guide' | 'api' | 'news';

export interface JumpToEntry {
  label: string;
  area: string;
  href: string;
  icon: JumpIcon;
}

export const JUMP_TO: JumpToEntry[] = [
  { label: 'Getting Started', area: 'Guide', href: '/docs/getting-started', icon: 'guide' },
  { label: 'API Reference', area: 'Reference', href: '/docs/api-reference', icon: 'api' },
  { label: "What's new", area: 'News', href: '/blog', icon: 'news' },
];

import React, { JSX } from 'react';
import { LuRss as Rss } from 'react-icons/lu';

interface RssProps {
  href: string;
  label?: string;
}

export default function RssNav({ href, label }: RssProps): JSX.Element {
  const name = label || 'RSS feed';
  return (
    <a
      href={href}
      className="navbar__item navbar-icon"
      aria-label={name}
      title={name}
      target="_blank"
      rel="noopener noreferrer"
    >
      <Rss size={18} strokeWidth={1.75} role="presentation" aria-hidden="true" />
    </a>
  );
}

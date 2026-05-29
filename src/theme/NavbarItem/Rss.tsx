import React, { JSX } from 'react';
import { LuRss as Rss } from 'react-icons/lu';

interface RssProps {
  href: string;
  label?: string;
}

export default function RssNav({ href, label = 'RSS feed' }: RssProps): JSX.Element {
  return (
    <a
      href={href}
      className="navbar__item navbar-icon"
      aria-label={label}
      title={label}
      target="_blank"
      rel="noopener noreferrer"
    >
      <Rss size={18} strokeWidth={1.75} />
    </a>
  );
}

import React, { JSX } from 'react';
import { Rss } from 'lucide-react';

interface RssProps {
  href: string;
  label?: string;
}

export default function RssNav({ href, label = 'RSS feed' }: RssProps): JSX.Element {
  return (
    <a href={href} className="navbar__item navbar__link tooltip" aria-label={label} title={label}>
      <Rss size={20} />
    </a>
  );
}

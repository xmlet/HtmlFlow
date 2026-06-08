import React, { type ReactNode } from 'react';
import Link from '@docusaurus/Link';
import { useLocation } from '@docusaurus/router';
import { useNavbarMobileSidebar } from '@docusaurus/theme-common/internal';
import { LuBookOpen, LuCode, LuNewspaper, LuMessageSquare, LuChevronRight, LuArrowUpRight } from 'react-icons/lu';

interface MenuCard {
  icon: ReactNode;
  title: string;
  subtitle: string;
  href: string;
  external?: boolean;
  /** Path prefix that marks this card active; longest match wins. */
  match?: string;
}

const CARDS: MenuCard[] = [
  {
    icon: <LuBookOpen />,
    title: 'Guide',
    subtitle: 'Tutorials and core concepts',
    href: '/docs/introduction',
    match: '/docs',
  },
  {
    icon: <LuCode />,
    title: 'API Reference',
    subtitle: 'Full Java & Kotlin API docs',
    href: '/docs/api-reference',
    match: '/docs/api-reference',
  },
  {
    icon: <LuNewspaper />,
    title: 'News',
    subtitle: 'Releases and announcements',
    href: '/blog',
    match: '/blog',
  },
  {
    icon: <LuMessageSquare />,
    title: 'Feedback',
    subtitle: 'Report an issue or request a feature',
    href: 'https://github.com/xmlet/HtmlFlow/issues',
    external: true,
  },
];

function activeHref(pathname: string): string | null {
  let best: { href: string; len: number } | null = null;
  for (const card of CARDS) {
    if (card.match && pathname.startsWith(card.match) && (!best || card.match.length > best.len)) {
      best = { href: card.href, len: card.match.length };
    }
  }
  return best?.href ?? null;
}

export default function NavbarMobilePrimaryMenu(): ReactNode {
  const mobileSidebar = useNavbarMobileSidebar();
  const { pathname } = useLocation();
  const active = activeHref(pathname);

  return (
    <ul className="hf-card-list">
      {CARDS.map(card => {
        const isActive = card.href === active;
        const content = (
          <>
            <span className="hf-card__icon">{card.icon}</span>
            <span className="hf-card__body">
              <span className="hf-card__title">
                {card.title}
                {card.external && <LuArrowUpRight className="hf-card__ext" aria-hidden />}
              </span>
              <span className="hf-card__subtitle">{card.subtitle}</span>
            </span>
            <LuChevronRight className="hf-card__chevron" aria-hidden />
          </>
        );

        if (card.external) {
          return (
            <li key={card.title}>
              <a
                className="hf-card"
                href={card.href}
                target="_blank"
                rel="noopener noreferrer"
                onClick={() => mobileSidebar.toggle()}
              >
                {content}
              </a>
            </li>
          );
        }

        return (
          <li key={card.title}>
            <Link
              className={`hf-card${isActive ? ' hf-card--active' : ''}`}
              to={card.href}
              onClick={() => mobileSidebar.toggle()}
            >
              {content}
            </Link>
          </li>
        );
      })}
    </ul>
  );
}

import React, { version, useEffect, useState, type ReactNode } from 'react';
import clsx from 'clsx';
import { useNavbarSecondaryMenu } from '@docusaurus/theme-common/internal';
import { ThemeClassNames } from '@docusaurus/theme-common';
import { SiGithub } from 'react-icons/si';
import { LuRss } from 'react-icons/lu';
import ThemeToggle from '@site/src/theme/NavbarItem/ThemeToggle';

const GITHUB_REPO = 'xmlet/HtmlFlow';

// Module-level cache so the count is fetched at most once per session.
let cachedStars: number | null | undefined;

function useGitHubStars(): number | null {
  const [stars, setStars] = useState<number | null>(cachedStars ?? null);
  useEffect(() => {
    if (cachedStars !== undefined) return;
    let active = true;
    fetch(`https://api.github.com/repos/${GITHUB_REPO}`)
      .then(r => (r.ok ? r.json() : Promise.reject(r.status)))
      .then((data: { stargazers_count?: number }) => {
        cachedStars = typeof data.stargazers_count === 'number' ? data.stargazers_count : null;
        if (active) setStars(cachedStars);
      })
      .catch(() => {
        cachedStars = null;
      });
    return () => {
      active = false;
    };
  }, []);
  return stars;
}

function DrawerFooter(): ReactNode {
  const stars = useGitHubStars();
  return (
    <div className="hf-drawer-footer">
      <a className="hf-ghstars" href={`https://github.com/${GITHUB_REPO}`} target="_blank" rel="noopener noreferrer">
        <SiGithub aria-hidden />
        <span>GitHub</span>
        {stars != null && <span className="hf-ghstars__count">{stars}</span>}
      </a>
      <div className="hf-drawer-footer__right">
        <a
          className="hf-foot-icon"
          href="/blog/rss.xml"
          target="_blank"
          rel="noopener noreferrer"
          aria-label="RSS feed"
        >
          <LuRss aria-hidden />
        </a>
        <ThemeToggle />
      </div>
    </div>
  );
}

// TODO Docusaurus v4: remove temporary inert workaround
function inertProps(inert: boolean) {
  const isBeforeReact19 = parseInt(version.split('.')[0], 10) < 19;
  if (isBeforeReact19) {
    return { inert: inert ? '' : undefined };
  }
  return { inert };
}

function NavbarMobileSidebarPanel({ children, inert }: { children: ReactNode; inert: boolean }) {
  return (
    <div
      className={clsx(ThemeClassNames.layout.navbar.mobileSidebar.panel, 'navbar-sidebar__item menu')}
      {...(inertProps(inert) as any)}
    >
      {children}
    </div>
  );
}

export default function NavbarMobileSidebarLayout({
  header,
  primaryMenu,
  secondaryMenu,
}: {
  header: ReactNode;
  primaryMenu: ReactNode;
  secondaryMenu: ReactNode;
}): ReactNode {
  const { shown: secondaryMenuShown } = useNavbarSecondaryMenu();
  return (
    <div className={clsx(ThemeClassNames.layout.navbar.mobileSidebar.container, 'navbar-sidebar')}>
      {header}
      <div
        className={clsx('navbar-sidebar__items', {
          'navbar-sidebar__items--show-secondary': secondaryMenuShown,
        })}
      >
        <NavbarMobileSidebarPanel inert={secondaryMenuShown}>{primaryMenu}</NavbarMobileSidebarPanel>
        <NavbarMobileSidebarPanel inert={!secondaryMenuShown}>{secondaryMenu}</NavbarMobileSidebarPanel>
      </div>
      <DrawerFooter />
    </div>
  );
}

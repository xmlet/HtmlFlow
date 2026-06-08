import React, { useEffect, useRef } from 'react';
import { useLocation } from '@docusaurus/router';

const ACTIVE_CLASS = 'hash-active';
const SMOOTH_SCROLL_CLASS = 'docs-smooth-scroll';

function scrollToId(id: string) {
  const el = document.getElementById(id);
  if (el) window.requestAnimationFrame(() => el.scrollIntoView({ behavior: 'smooth', block: 'start' }));
}

function useHashActiveSidebar() {
  const { pathname, hash } = useLocation();
  const isDocsPage = pathname.startsWith('/docs/');
  const previousHash = useRef('');
  const previousPathname = useRef(pathname);

  useEffect(() => {
    const normalizedPath = window.location.pathname.replace(/\/$/, '');

    if (previousPathname.current !== pathname) {
      previousHash.current = '';
      previousPathname.current = pathname;
    }

    document.documentElement.classList.toggle(SMOOTH_SCROLL_CLASS, isDocsPage);

    const clearActive = () =>
      document
        .querySelectorAll<HTMLElement>(`.theme-doc-sidebar-container .menu__link.${ACTIVE_CLASS}`)
        .forEach(el => el.classList.remove(ACTIVE_CLASS));

    if (!isDocsPage) {
      clearActive();
      return;
    }

    const handleDocumentClick = (e: MouseEvent) => {
      if (e.defaultPrevented || e.button !== 0 || e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
      const link = (e.target instanceof Element ? e.target : null)?.closest<HTMLAnchorElement>('a[href]');
      if (!link) return;

      const url = new URL(link.href, window.location.href);
      if (url.pathname.replace(/\/$/, '') !== normalizedPath || !url.hash) return;
      if (!document.getElementById(decodeURIComponent(url.hash.slice(1)))) return;

      e.preventDefault();
      window.history.pushState(null, '', `${url.pathname}${url.search}${url.hash}`);
      previousHash.current = url.hash;
      scrollToId(decodeURIComponent(url.hash.slice(1)));
    };

    document.addEventListener('click', handleDocumentClick, true);

    if (hash && hash !== previousHash.current) {
      window.setTimeout(() => scrollToId(decodeURIComponent(hash.slice(1))), 0);
    }
    previousHash.current = hash;

    const updateActive = () => {
      const headings = Array.from(
        document.querySelectorAll<HTMLElement>('.theme-doc-markdown h2[id], .theme-doc-markdown h3[id]')
      );
      if (!headings.length) return;

      const navH = document.querySelector<HTMLElement>('.navbar')?.getBoundingClientRect().height ?? 60;
      const atBottom = window.scrollY + window.innerHeight >= document.documentElement.scrollHeight - 2;

      let active = headings[0];
      for (const h of headings) {
        if (h.getBoundingClientRect().top <= navH + 24) active = h;
      }
      const activeId = atBottom ? headings[headings.length - 1].id : active.id;

      clearActive();
      const link = document.querySelector<HTMLElement>(
        `.theme-doc-sidebar-item-link .menu__link[href$="#${activeId}"]`
      );
      if (link) {
        link.classList.add(ACTIVE_CLASS);
        link
          .closest('.theme-doc-sidebar-item-category-level-2')
          ?.querySelector<HTMLElement>(':scope > .menu__list-item-collapsible > .menu__link')
          ?.classList.add(ACTIVE_CLASS);
      } else {
        document
          .querySelector<HTMLElement>(
            `.theme-doc-sidebar-item-category-level-2 > .menu__list-item-collapsible > .menu__link[href="${normalizedPath}"]`
          )
          ?.classList.add(ACTIVE_CLASS);
      }
    };

    window.addEventListener('scroll', updateActive, { passive: true });
    window.addEventListener('resize', updateActive, { passive: true });
    const timer = setTimeout(updateActive, 150);

    return () => {
      document.removeEventListener('click', handleDocumentClick, true);
      window.removeEventListener('scroll', updateActive);
      window.removeEventListener('resize', updateActive);
      clearTimeout(timer);
      clearActive();
    };
  }, [pathname, hash, isDocsPage]);
}

export default function Root({ children }: { children: React.ReactNode }): React.ReactElement {
  useHashActiveSidebar();
  return <>{children}</>;
}

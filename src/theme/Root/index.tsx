import React, { useEffect, useRef } from 'react';
import { useLocation } from '@docusaurus/router';
import Head from '@docusaurus/Head';

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
        .querySelectorAll<HTMLElement>(`.menu__link.${ACTIVE_CLASS}`)
        .forEach(el => el.classList.remove(ACTIVE_CLASS));

    const removePills = () => document.querySelectorAll('.hf-sidebar-pill').forEach(el => el.remove());

    document.querySelectorAll('.navbar-sidebar .hf-sidebar-pill').forEach(el => el.remove());

    if (!isDocsPage) {
      clearActive();
      removePills();
      return;
    }

    const positionPill = (link: HTMLElement) => {
      if (link.closest('.navbar-sidebar')) return;
      const menu = link.closest<HTMLElement>('.theme-doc-sidebar-menu');
      if (!menu) return;
      const rect = link.getBoundingClientRect();
      if (!rect.height) return; // sidebar hidden in this layout
      let pill = menu.querySelector<HTMLElement>(':scope > .hf-sidebar-pill');
      const isNew = !pill;
      if (!pill) {
        // The menu is a <ul>, whose only permitted element children are <li>,
        // <script> and <template> -- a <div> here would be invalid HTML. It is
        // decorative, so hide it from the accessibility tree.
        pill = document.createElement('li');
        pill.className = 'hf-sidebar-pill';
        pill.setAttribute('role', 'presentation');
        pill.setAttribute('aria-hidden', 'true');
        menu.prepend(pill);
      }
      if (isNew) pill.style.transition = 'none';
      const menuRect = menu.getBoundingClientRect();
      pill.style.transform = `translate(${rect.left - menuRect.left}px, ${rect.top - menuRect.top}px)`;
      pill.style.width = `${rect.width}px`;
      pill.style.height = `${rect.height}px`;
      pill.style.opacity = '1';
      if (isNew) {
        void pill.offsetHeight; // flush styles so the first paint isn't animated
        pill.style.transition = '';
      }
    };

    let disposed = false;

    const updateActive = () => {
      if (disposed) return;
      const sidebarIds = new Set(
        Array.from(
          document.querySelectorAll<HTMLAnchorElement>('.theme-doc-sidebar-item-link .menu__link[href*="#"]')
        ).map(a => decodeURIComponent(new URL(a.href, window.location.href).hash.slice(1)))
      );
      const headings = Array.from(
        document.querySelectorAll<HTMLElement>('.theme-doc-markdown h2[id], .theme-doc-markdown h3[id]')
      ).filter(h => sidebarIds.has(h.id));
      if (!headings.length) {
        setTimeout(updateActive, 200);
        return;
      }

      const navH = document.querySelector<HTMLElement>('.navbar')?.getBoundingClientRect().height ?? 60;
      const atBottom = window.scrollY + window.innerHeight >= document.documentElement.scrollHeight - 8;

      let active = headings[0];
      for (const h of headings) {
        if (h.getBoundingClientRect().top <= navH + 24) active = h;
      }
      const activeId = atBottom ? headings[headings.length - 1].id : active.id;

      clearActive();
      document
        .querySelectorAll<HTMLElement>(`.theme-doc-sidebar-item-link .menu__link[href$="#${activeId}"]`)
        .forEach(link => {
          link.classList.add(ACTIVE_CLASS);
          positionPill(link);
        });
    };

    const scrollAndUpdate = (id: string) => {
      scrollToId(id);
      const fallback = setTimeout(updateActive, 700);
      window.addEventListener(
        'scrollend',
        () => {
          clearTimeout(fallback);
          updateActive();
        },
        { once: true }
      );
    };

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
      scrollAndUpdate(decodeURIComponent(url.hash.slice(1)));
    };

    document.addEventListener('click', handleDocumentClick, true);

    if (hash && hash !== previousHash.current) {
      window.setTimeout(() => scrollAndUpdate(decodeURIComponent(hash.slice(1))), 0);
    } else {
      setTimeout(updateActive, 150);
    }
    previousHash.current = hash;

    window.addEventListener('scroll', updateActive, { passive: true });
    window.addEventListener('resize', updateActive, { passive: true });
    document.fonts?.ready.then(updateActive);

    return () => {
      disposed = true;
      document.removeEventListener('click', handleDocumentClick, true);
      window.removeEventListener('scroll', updateActive);
      window.removeEventListener('resize', updateActive);
      clearActive();
    };
  }, [pathname, hash, isDocsPage]);
}

/**
 * Points AI agents at the plain-markdown mirror of the current docs page
 * (generated at build time by src/plugins/agent-ready.js).
 */
function MarkdownAlternateLink(): React.ReactElement | null {
  const { pathname } = useLocation();
  if (!(pathname === '/docs' || pathname.startsWith('/docs/'))) return null;
  const base = pathname.endsWith('/') ? pathname : `${pathname}/`;
  return (
    <Head>
      <link rel="alternate" type="text/markdown" href={`${base}index.md`} />
    </Head>
  );
}

export default function Root({ children }: { children: React.ReactNode }): React.ReactElement {
  useHashActiveSidebar();
  return (
    <>
      <MarkdownAlternateLink />
      {children}
    </>
  );
}

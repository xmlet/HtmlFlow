import React, { useEffect } from 'react';
import { useLocation } from '@docusaurus/router';

function useHashActiveSidebar() {
  const { pathname } = useLocation();

  useEffect(() => {
    const ACTIVE_CLASS = 'hash-active';

    const clearActive = () => {
      document
        .querySelectorAll<HTMLElement>(`.theme-doc-sidebar-container .menu__link.${ACTIVE_CLASS}`)
        .forEach(el => el.classList.remove(ACTIVE_CLASS));
    };

    if (!pathname.startsWith('/docs/')) {
      clearActive();
      return;
    }

    const collectTrackedSections = () => {
      const headings = Array.from(
        document.querySelectorAll<HTMLElement>('.theme-doc-markdown h2[id], .theme-doc-markdown h3[id]')
      );
      return headings.map(h => ({ id: h.id, anchor: h }));
    };

    const updateActive = () => {
      const tracked = collectTrackedSections();
      if (!tracked.length) return;

      const navbar = document.querySelector<HTMLElement>('.navbar');
      const navH = navbar?.getBoundingClientRect().height ?? 60;
      const threshold = navH + 24;

      let activeId: string | null = null;
      for (const { id, anchor } of tracked) {
        const top = anchor.getBoundingClientRect().top;
        if (top <= threshold) {
          activeId = id;
        }
      }
      if (!activeId) activeId = tracked[0].id;

      // Bottom guard: trailing sections sit too low to ever reach the top
      // threshold on short pages, so force the last section active once the
      // viewport hits the bottom of the document.
      const scrollBottom = window.scrollY + window.innerHeight;
      const docHeight = document.documentElement.scrollHeight;
      if (scrollBottom >= docHeight - 2) {
        activeId = tracked[tracked.length - 1].id;
      }

      clearActive();
      const link = document.querySelector<HTMLElement>(
        `.theme-doc-sidebar-item-link .menu__link[href$="#${activeId}"]`
      );
      if (link) {
        link.classList.add(ACTIVE_CLASS);
        const parentCategory = link.closest('.theme-doc-sidebar-item-category-level-2');
        const categoryLink = parentCategory?.querySelector<HTMLElement>(
          ':scope > .menu__list-item-collapsible > .menu__link'
        );
        categoryLink?.classList.add(ACTIVE_CLASS);
      } else {
        const docPath = window.location.pathname.replace(/\/$/, '');
        const categoryLink = document.querySelector<HTMLElement>(
          `.theme-doc-sidebar-item-category-level-2 > .menu__list-item-collapsible > .menu__link[href="${docPath}"]`
        );
        categoryLink?.classList.add(ACTIVE_CLASS);
      }
    };

    window.addEventListener('scroll', updateActive, { passive: true });
    window.addEventListener('resize', updateActive, { passive: true });
    const timer = setTimeout(updateActive, 150);

    return () => {
      window.removeEventListener('scroll', updateActive);
      window.removeEventListener('resize', updateActive);
      clearTimeout(timer);
      clearActive();
    };
  }, [pathname]);
}

export default function Root({ children }: { children: React.ReactNode }): React.ReactElement {
  useHashActiveSidebar();
  return <>{children}</>;
}

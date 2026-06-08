import React, { useState, type ReactNode } from 'react';
import { LuSearch } from 'react-icons/lu';
import SearchOverlay from '../SearchOverlay';

// Resting search field shown inside the mobile drawer. Tapping it opens the
// full-screen search overlay.
export default function SidebarSearchField(): ReactNode {
  const [open, setOpen] = useState(false);
  return (
    <>
      <button
        type="button"
        className="flex items-center gap-[0.55rem] w-full mb-4 py-[0.65rem] px-[0.85rem] border border-[var(--ifm-color-emphasis-200)] rounded-[10px] bg-[var(--ifm-color-emphasis-100)] text-[var(--ifm-color-emphasis-600)] text-[0.9rem] cursor-text text-left hover:border-[var(--ifm-color-emphasis-300)] dark:border-[rgba(255,255,255,0.08)] dark:bg-[rgba(255,255,255,0.06)]"
        onClick={() => setOpen(true)}
      >
        <LuSearch aria-hidden />
        <span>Search the guide...</span>
      </button>
      {open && <SearchOverlay onClose={() => setOpen(false)} />}
    </>
  );
}

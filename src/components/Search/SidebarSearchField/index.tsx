import React, { useState, type ReactNode } from 'react';
import { LuSearch } from 'react-icons/lu';
import SearchOverlay from '../SearchOverlay';

// Resting search field shown inside the mobile drawer. Tapping it opens the
// full-screen search overlay.
export default function SidebarSearchField(): ReactNode {
  const [open, setOpen] = useState(false);
  return (
    <>
      <button type="button" className="hf-search-field" onClick={() => setOpen(true)}>
        <LuSearch aria-hidden />
        <span>Search the guide...</span>
      </button>
      {open && <SearchOverlay onClose={() => setOpen(false)} />}
    </>
  );
}

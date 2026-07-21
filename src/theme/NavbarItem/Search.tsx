import React, { useEffect, useState, type ReactNode } from 'react';
import { LuSearch } from 'react-icons/lu';
import SearchOverlay from '@site/src/components/Search/SearchOverlay';

export default function Search(): ReactNode {
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        setOpen(true);
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, []);

  return (
    <>
      <button
        type="button"
        className="mx-1 inline-flex h-9 w-52 cursor-pointer items-center gap-2 rounded-lg border border-gray-200 bg-gray-100 px-2.5 text-sm font-medium text-gray-600 hover:border-gray-300 hover:bg-gray-200 max-[1100px]:w-44 max-[996px]:hidden dark:border-white/10 dark:bg-white/5 dark:text-gray-400 dark:hover:border-white/20 dark:hover:bg-white/[0.08]"
        onClick={() => setOpen(true)}
        aria-label="Search documentation"
      >
        <LuSearch className="shrink-0 text-base" aria-hidden />
        <span className="flex-1 truncate text-left">Search...</span>
      </button>
      {open && <SearchOverlay onClose={() => setOpen(false)} />}
    </>
  );
}

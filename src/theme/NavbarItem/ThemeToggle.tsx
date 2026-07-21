import React, { JSX } from 'react';
import { useColorMode } from '@docusaurus/theme-common';
import { LuMoon as Moon, LuSun as Sun } from 'react-icons/lu';

export default function ThemeToggle(): JSX.Element {
  const { colorMode, setColorMode } = useColorMode();
  const isDark = colorMode === 'dark';

  return (
    <button
      className="navbar__item navbar-icon"
      onClick={() => setColorMode(isDark ? 'light' : 'dark')}
      aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
    >
      {/* Both icons render; CSS picks one off data-theme, which is set before
          first paint. Choosing here instead would show the light-mode icon
          until hydration, because useColorMode is 'light' on the first render. */}
      <Sun size={18} strokeWidth={1.75} className="hidden dark:block" />
      <Moon size={18} strokeWidth={1.75} className="block dark:hidden" />
    </button>
  );
}

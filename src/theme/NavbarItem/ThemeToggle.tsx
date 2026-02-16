import React, { JSX } from 'react';
import { useColorMode } from '@docusaurus/theme-common';
    
export default function ThemeToggle(): JSX.Element {
  const { colorMode, setColorMode } = useColorMode();
  const isDarkTheme = colorMode === 'dark';

  const toggleTheme = () => {
    setColorMode(isDarkTheme ? 'light' : 'dark');
  };

  return (
    <div
      className="navbar__item"
      style={{
        display: 'flex',
        alignItems: 'center',
        padding: '0 0.5rem',
      }}
    >
      <button
        onClick={toggleTheme}
        aria-label={`Switch to ${isDarkTheme ? 'light' : 'dark'} mode`}
        style={{
          position: 'relative',
          width: '48px',
          height: '24px',
          backgroundColor: isDarkTheme ? '#0ea5e9' : '#e0e7ff',
          borderRadius: '12px',
          border: 'none',
          cursor: 'pointer',
          padding: 0,
          transition: 'background-color 0.3s ease',
          outline: 'none',
        }}
      >
        <span
          style={{
            position: 'absolute',
            top: '2px',
            left: isDarkTheme ? '26px' : '2px',
            width: '20px',
            height: '20px',
            backgroundColor: '#ffffff',
            borderRadius: '50%',
            transition: 'left 0.3s ease',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 2px 4px rgba(0, 0, 0, 0.2)',
          }}
        >
          {isDarkTheme ? (
            <svg
              width="12"
              height="12"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#0ea5e9"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
            </svg>
          ) : (
            <svg
              width="12"
              height="12"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#6366f1"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <circle cx="12" cy="12" r="5" />
              <line x1="12" y1="1" x2="12" y2="3" />
              <line x1="12" y1="21" x2="12" y2="23" />
              <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
              <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
              <line x1="1" y1="12" x2="3" y2="12" />
              <line x1="21" y1="12" x2="23" y2="12" />
              <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
              <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
            </svg>
          )}
        </span>
      </button>
    </div>
  );
}

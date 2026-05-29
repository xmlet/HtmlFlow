import React, { JSX } from 'react';

export default function CustomDivider(): JSX.Element {
  return <div className="hidden h-6 w-px self-center bg-[var(--ifm-navbar-link-color)] opacity-20 sm:block mx-2" />;
}

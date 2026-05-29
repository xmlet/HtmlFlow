import React, { JSX } from 'react';
import { SiGithub } from 'react-icons/si';

interface GitHubProps {
  href: string;
  label?: string;
}

export default function GitHub({ href, label = 'GitHub' }: GitHubProps): JSX.Element {
  return (
    <a
      href={href}
      className="navbar__item navbar-icon"
      target="_blank"
      rel="noopener noreferrer"
      aria-label={label}
      title={label}
    >
      <SiGithub size={18} />
    </a>
  );
}

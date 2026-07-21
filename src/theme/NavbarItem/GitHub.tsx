import React, { JSX } from 'react';
import { SiGithub } from 'react-icons/si';

interface GitHubProps {
  href: string;
  label?: string;
}

export default function GitHub({ href, label }: GitHubProps): JSX.Element {
  // `|| 'GitHub'` rather than a default parameter: navbar config may pass an
  // empty string, which a default only covers when the value is undefined.
  const name = label || 'GitHub';
  return (
    <a
      href={href}
      className="navbar__item navbar-icon"
      target="_blank"
      rel="noopener noreferrer"
      aria-label={name}
      title={name}
    >
      {/* Decorative: the link is already named. react-icons stamps role="img"
          on the svg, which then demands its own text alternative. */}
      <SiGithub size={18} role="presentation" aria-hidden="true" />
    </a>
  );
}

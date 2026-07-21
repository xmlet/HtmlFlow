import React, { JSX } from 'react';
import { SiGithub } from 'react-icons/si';

interface GitHubProps {
  href: string;
  label?: string;
}

export default function GitHub({ href, label }: GitHubProps): JSX.Element {
  // Not a default parameter: navbar config may pass an empty string.
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
      {/* Decorative; react-icons would otherwise stamp role="img" on the svg. */}
      <SiGithub size={18} role="presentation" aria-hidden="true" />
    </a>
  );
}

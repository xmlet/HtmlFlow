import React, { JSX } from 'react';

export default function CustomDivider(): JSX.Element {
  return (
    <div
      style={{
        width: '1px',
        height: '24px',
        backgroundColor: 'var(--ifm-navbar-link-color)',
        opacity: 0.2,
        margin: '0 0.5rem',
        alignSelf: 'center',
      }}
    />
  );
}

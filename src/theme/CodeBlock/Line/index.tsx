import React, { type ReactNode } from 'react';
import clsx from 'clsx';
import LineToken from '@theme/CodeBlock/Line/Token';
import type { Props } from '@theme/CodeBlock/Line';
import styles from './styles.module.css';

/**
 * Swizzled to emit a <span> per line instead of the stock <div>.
 *
 * Code block lines live inside <pre><code>, and <code> accepts only phrasing
 * content -- a <div> there is invalid per the HTML content model. The element
 * is purely presentational (the line box comes from CSS: `table-row` when line
 * numbers are on, otherwise `block`), so a <span> renders identically.
 */

// Keeps empty lines from collapsing to zero height.
function LineBreak() {
  return <br />;
}

// A line whose only token is '\n' would double up with the <br/> above.
function fixLineBreak(line: Props['line']): Props['line'] {
  const singleLineBreakToken = line.length === 1 && line[0]?.content === '\n' ? line[0] : undefined;
  return singleLineBreakToken ? [{ ...singleLineBreakToken, content: '' }] : line;
}

export default function CodeBlockLine({
  line: lineProp,
  classNames,
  showLineNumbers,
  getLineProps,
  getTokenProps,
}: Props): ReactNode {
  const line = fixLineBreak(lineProp);
  const lineProps = getLineProps({
    line,
    className: clsx(classNames, showLineNumbers && styles.codeLine),
  });
  const lineTokens = line.map((token, key) => {
    const tokenProps = getTokenProps({ token });
    return (
      <LineToken key={key} {...tokenProps} line={line} token={token}>
        {tokenProps.children}
      </LineToken>
    );
  });

  return (
    <span
      {...lineProps}
      // With line numbers, styles.codeLine supplies `display: table-row`.
      // Without it the stock <div> was block-level, which a <span> is not.
      style={showLineNumbers ? lineProps.style : { display: 'block', ...lineProps.style }}
    >
      {showLineNumbers ? (
        <>
          <span className={styles.codeLineNumber} />
          <span className={styles.codeLineContent}>{lineTokens}</span>
        </>
      ) : (
        lineTokens
      )}
      <LineBreak />
    </span>
  );
}

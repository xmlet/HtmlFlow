import React, { type ReactNode } from 'react';
import clsx from 'clsx';
import LineToken from '@theme/CodeBlock/Line/Token';
import type { Props } from '@theme/CodeBlock/Line';
import styles from './styles.module.css';

/**
 * Swizzled to emit a <span> per line instead of the stock <div>: these sit
 * inside <code>, which accepts only phrasing content. CSS supplies the line
 * box either way, so the two render identically.
 */

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
      // styles.codeLine already sets table-row when line numbers are on.
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

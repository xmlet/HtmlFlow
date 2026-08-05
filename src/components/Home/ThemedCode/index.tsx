// PrismLight ships no grammars, keeping the unused languages out of the bundle.
import { PrismLight as SyntaxHighlighter } from 'react-syntax-highlighter';
import java from 'react-syntax-highlighter/dist/esm/languages/prism/java';
import kotlin from 'react-syntax-highlighter/dist/esm/languages/prism/kotlin';
import markup from 'react-syntax-highlighter/dist/esm/languages/prism/markup';
// a11y variants: the stock One themes put comments below WCAG AA.
import a11yLight from 'react-syntax-highlighter/dist/esm/styles/prism/a11y-one-light';
import a11yDark from 'react-syntax-highlighter/dist/esm/styles/prism/a11y-dark';

SyntaxHighlighter.registerLanguage('java', java);
SyntaxHighlighter.registerLanguage('kotlin', kotlin);
SyntaxHighlighter.registerLanguage('html', markup);

const codeStyle = {
  margin: 0,
  padding: '1.5rem',
  fontSize: '0.875rem',
  lineHeight: '1.5',
};

// The default gutter colour is too faint to clear WCAG AA.
const lineNumberStyle = {
  light: { color: '#57606a' },
  dark: { color: '#9aa4b2' },
};

/**
 * Renders the sample once per theme and lets CSS reveal one. Token colours are
 * inline styles, so a useColorMode branch would flash white until hydration.
 */
export function ThemedCode({ language, children }: { language: string; children: string }) {
  return (
    <>
      <div className="block dark:hidden">
        <SyntaxHighlighter
          className="max-w-full"
          language={language}
          style={a11yLight}
          customStyle={{ ...codeStyle, background: 'white' }}
          lineNumberStyle={lineNumberStyle.light}
          showLineNumbers
        >
          {children}
        </SyntaxHighlighter>
      </div>
      <div className="hidden dark:block">
        <SyntaxHighlighter
          className="max-w-full"
          language={language}
          style={a11yDark}
          customStyle={{ ...codeStyle, background: '#111827' }}
          lineNumberStyle={lineNumberStyle.dark}
          showLineNumbers
        >
          {children}
        </SyntaxHighlighter>
      </div>
    </>
  );
}

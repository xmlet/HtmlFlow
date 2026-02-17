import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneLight, oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import { ReactNode } from 'react';
import { useColorMode } from '@docusaurus/theme-common';

export default function CodeBlock({
  code,
  header,
  language,
  children,
}: {
  code: string;
  header: string;
  language: string;
  children?: ReactNode;
}) {
  const { colorMode } = useColorMode();
  return (
    <div className="rounded-2xl bg-gray-50 dark:bg-gray-800 p-1 shadow-xl w-full lg:min-w-[520px]">
      <div className="rounded-xl bg-white dark:bg-gray-900">
        <div className="border-b border-gray-100 dark:border-gray-800 px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="flex gap-1.5">
                <div className="h-3 w-3 rounded-full bg-red-400"></div>
                <div className="h-3 w-3 rounded-full bg-yellow-400"></div>
                <div className="h-3 w-3 rounded-full bg-green-400"></div>
              </div>
              <span className="ml-2 text-sm text-gray-600 dark:text-gray-400">{header}</span>
            </div>
            {children}
          </div>
        </div>
        <div className="overflow-x-auto overflow-hidden rounded-b-xl">
          <SyntaxHighlighter
            className="code-block"
            language={language}
            style={colorMode === 'dark' ? oneDark : oneLight}
            customStyle={{
              margin: 0,
              padding: '1.5rem',
              background: colorMode === 'dark' ? '#111827' : 'white',
              fontSize: '0.875rem',
              lineHeight: '1.5',
            }}
            showLineNumbers
          >
            {code}
          </SyntaxHighlighter>
        </div>
      </div>
    </div>
  );
}

import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneLight, oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import { ArrowRight } from 'lucide-react';
import { useColorMode } from '@docusaurus/theme-common';
import { useState } from 'react';
import CodeBlock from './CodeBlock';

export default function JavaKotlinCodeBlock({ javaCode, kotlinCode }: { javaCode: string; kotlinCode: string }) {
  const { colorMode } = useColorMode();
  const [language, setLanguage] = useState<'java' | 'kotlin'>('java');
  return (
    <CodeBlock header="HtmlFlow DSL" language={language} code={language === 'java' ? javaCode : kotlinCode}>
      <div className="flex gap-2">
        <button
          onClick={() => setLanguage('java')}
          className={`px-3 py-1 text-xs font-medium rounded transition-colors ${
            language === 'java'
              ? 'bg-sky-600 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          Java
        </button>
        <button
          onClick={() => setLanguage('kotlin')}
          className={`px-3 py-1 text-xs font-medium rounded transition-colors ${
            language === 'kotlin'
              ? 'bg-sky-600 text-white'
              : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-gray-600'
          }`}
        >
          Kotlin
        </button>
      </div>
    </CodeBlock>
  );
}

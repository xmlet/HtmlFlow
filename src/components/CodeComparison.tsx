import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneLight, oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import { ArrowRight } from 'lucide-react';
import { useColorMode } from '@docusaurus/theme-common';
import { useState } from 'react';

const javaCode = `HtmlFlow
  .doc(System.out)
    .html() // HtmlPage
      .head()
        .title().text("HtmlFlow").__()
      .__() // head
      .body()
        .div().attrClass("container")
          .h1().text("My first page with HtmlFlow").__()
          .img().attrSrc("http://bit.ly/2MoHwrU").__()
          .p().text("Typesafe is awesome! :-)").__()
        .__() // div
      .__() // body
    .__(); // html`;

const kotlinCode = `HtmlFlow
  .doc(System.out)
    .html {
      head {
        title { text("Hello World!") }
      }
      body {
        div {
          attrClass("container")
          h1 { text("Hello World!") }
          img { attrSrc("http://bit.ly/2MoHwrU") }
          p { text("Typesafe is awesome! :-)") }
        }
      }
    }`;

const htmlCode = `<html>
  <head>
    <title>HtmlFlow</title>
  </head>
  <body>
    <div class="container">
      <h1>My first page with HtmlFlow</h1>
      <img src="http://bit.ly/2MoHwrU">
      <p>Typesafe is awesome! :-)</p>
    </div>
  </body>
</html>`;

export function CodeComparison() {
  const { colorMode } = useColorMode();
  const [language, setLanguage] = useState<'java' | 'kotlin'>('java');

  return (
    <section className="bg-gray-100 dark:bg-gray-900 px-6 py-24">
      <div className="mx-auto max-w-7xl flex flex-col items-center">
        <div className="mb-8 text-center">
          <h2 className="mb-4 text-4xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-5xl">
            Type-Safe HTML Generation in Java and Kotlin
          </h2>
          <p className="max-w-2xl text-lg text-gray-600 dark:text-gray-300">
            Write HTML in a typesafe, fluent style. Your IDE will guide you through valid HTML 5.2 structure.
          </p>
        </div>

        <div className="grid gap-8 lg:grid-cols-[1fr_auto_1fr] lg:items-center">
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
                    <span className="ml-2 text-sm text-gray-600 dark:text-gray-400">HtmlFlow DSL</span>
                  </div>
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
                  {language === 'java' ? javaCode : kotlinCode}
                </SyntaxHighlighter>
              </div>
            </div>
          </div>

          <div className="flex items-center justify-center lg:mx-4">
            <div className="flex h-12 w-12 rotate-90 items-center justify-center rounded-full bg-white dark:bg-sky-900 border border-gray-200 dark:border-sky-800 lg:rotate-0">
              <ArrowRight className="h-6 w-6 text-sky-600 dark:text-sky-400" />
            </div>
          </div>

          <div className="rounded-2xl bg-gray-50 dark:bg-gray-800 p-1 shadow-xl w-full lg:min-w-[520px]">
            <div className="rounded-xl bg-white dark:bg-gray-900">
              <div className="border-b border-gray-100 dark:border-gray-800 px-6 py-4">
                <div className="flex items-center gap-2">
                  <div className="flex gap-1.5">
                    <div className="h-3 w-3 rounded-full bg-red-400"></div>
                    <div className="h-3 w-3 rounded-full bg-yellow-400"></div>
                    <div className="h-3 w-3 rounded-full bg-green-400"></div>
                  </div>
                  <span className="ml-2 text-sm text-gray-600 dark:text-gray-400">Output</span>
                </div>
              </div>
              <div className="overflow-x-auto overflow-hidden rounded-b-xl">
                <SyntaxHighlighter
                  className="code-block"
                  language="html"
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
                  {htmlCode}
                </SyntaxHighlighter>
              </div>
            </div>
          </div>
        </div>

        <div className="mt-12 text-center">
          <p className="text-sm text-gray-600 dark:text-gray-400">
            Use{' '}
            <code className="rounded bg-sky-100 dark:bg-sky-900 px-2 py-1 text-sky-900 dark:text-sky-100">
              Flowifier.fromHtml(String html)
            </code>{' '}
            to convert existing HTML to HtmlFlow DSL
          </p>
        </div>
      </div>
    </section>
  );
}

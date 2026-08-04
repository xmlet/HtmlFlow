import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneLight, oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism';
import { Button } from '../../Ui/Button';
import Link from '@docusaurus/Link';
import { LuArrowRight as ArrowRight, LuZap as Zap } from 'react-icons/lu';
import { useColorMode } from '@docusaurus/theme-common';

const datastarKotlinCode = `button {
  val fetching = dataIndicator("_fetching")
  
  dataAttr("disabled") { +fetching }
  dataOn(Click) {
    !fetching and get(::clickToLoadMore)
  }
  
  text("Load More")
}`;

const datastarHtmlCode = `<button
  data-indicator:_fetching
  data-attr:disabled="$_fetching"
  data-on:click="!$_fetching && @get('/examples/click_to_load/more')"
>
  Load More
</button>`;

export function DatastarSection() {
  const { colorMode } = useColorMode();

  return (
    <section className="bg-white dark:bg-gray-950 px-6 py-12 sm:py-24 border-t border-gray-200 dark:border-gray-800">
      <div className="mx-auto max-w-7xl flex flex-col items-center">
        {/* Section Header */}
        <div className="mb-12 text-center">
          <div className="inline-flex items-center gap-2 rounded-full bg-amber-500/10 px-4 py-1.5 text-sm font-semibold text-amber-600 dark:text-amber-400 mb-4 border border-amber-500/20">
            <Zap className="h-4 w-4" />
            <span>Reactive Hypermedia</span>
          </div>
          <h2 className="mb-4 text-4xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-5xl">
            Type-Safe Datastar Integration
          </h2>
          <p className="max-w-3xl text-lg text-gray-600 dark:text-gray-300">
            Compose client-side reactivity and signals natively in Kotlin. HtmlFlow converts type-safe expressions like{' '}
            <code className="rounded bg-sky-100 dark:bg-sky-900/50 px-2 py-0.5 font-mono text-sm text-sky-900 dark:text-sky-200">
              !fetching and get(...)
            </code>{' '}
            into valid Datastar hypermedia attributes.
          </p>
          <Button
            asChild
            size="lg"
            variant="outline"
            className="gap-2 border-sky-200 hover:bg-sky-50 dark:border-sky-700 dark:hover:bg-sky-900/50"
          >
            <Link to="/docs/integrations#data-" className="no-underline">
              Read More
              <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        </div>
        {/* Code Grid */}
        <div className="hf-code-comparison-grid w-full">
          {/* Kotlin DSL Panel */}
          <div className="hf-code-panel rounded-2xl bg-gray-50 dark:bg-gray-800 p-1 shadow-xl">
            <div className="rounded-xl bg-white dark:bg-gray-900">
              <div className="border-b border-gray-100 dark:border-gray-800 px-6 py-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="flex gap-1.5">
                      <div className="h-3 w-3 rounded-full bg-red-400"></div>
                      <div className="h-3 w-3 rounded-full bg-yellow-400"></div>
                      <div className="h-3 w-3 rounded-full bg-green-400"></div>
                    </div>
                    <span className="ml-2 text-sm text-gray-600 dark:text-gray-400 font-medium">
                      HtmlFlow Datastar DSL (Kotlin)
                    </span>
                  </div>
                  <span className="rounded bg-amber-100 dark:bg-amber-900/40 px-2 py-0.5 text-xs font-semibold text-amber-800 dark:text-amber-300">
                    Type-Safe
                  </span>
                </div>
              </div>
              <div className="overflow-x-auto overflow-hidden rounded-b-xl hf-code-block">
                <SyntaxHighlighter
                  className="max-w-full"
                  language="kotlin"
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
                  {datastarKotlinCode}
                </SyntaxHighlighter>
              </div>
            </div>
          </div>

          {/* Arrow Divider */}
          <div className="flex items-center justify-center lg:mx-4 my-4 lg:my-0">
            <div className="flex h-12 w-12 rotate-90 items-center justify-center rounded-full bg-white dark:bg-sky-900 border border-gray-200 dark:border-sky-800 lg:rotate-0 shadow-md">
              <ArrowRight className="h-6 w-6 text-sky-600 dark:text-sky-400" />
            </div>
          </div>

          {/* Generated HTML Panel */}
          <div className="hf-code-panel rounded-2xl bg-gray-50 dark:bg-gray-800 p-1 shadow-xl">
            <div className="rounded-xl bg-white dark:bg-gray-900">
              <div className="border-b border-gray-100 dark:border-gray-800 px-6 py-4">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="flex gap-1.5">
                      <div className="h-3 w-3 rounded-full bg-red-400"></div>
                      <div className="h-3 w-3 rounded-full bg-yellow-400"></div>
                      <div className="h-3 w-3 rounded-full bg-green-400"></div>
                    </div>
                    <span className="ml-2 text-sm text-gray-600 dark:text-gray-400 font-medium">
                      Generated Datastar HTML
                    </span>
                  </div>
                  <span className="rounded bg-sky-100 dark:bg-sky-900/40 px-2 py-0.5 text-xs font-semibold text-sky-800 dark:text-sky-300">
                    Compiled Markup
                  </span>
                </div>
              </div>
              <div className="overflow-x-auto overflow-hidden rounded-b-xl hf-code-block">
                <SyntaxHighlighter
                  className="max-w-full"
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
                  {datastarHtmlCode}
                </SyntaxHighlighter>
              </div>
            </div>
          </div>
        </div>

        {/* Feature Highlights Footer */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6 text-left max-w-5xl">
          <div className="rounded-xl bg-gray-50 dark:bg-gray-900 p-4 border border-gray-200 dark:border-gray-800">
            <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-1">Reactive Signals</h4>
            <p className="text-xs text-gray-600 dark:text-gray-400">
              Define indicators like <code className="text-sky-600 dark:text-sky-400">_fetching</code> as strongly typed
              Kotlin variables instead of raw magic strings.
            </p>
          </div>
          <div className="rounded-xl bg-gray-50 dark:bg-gray-900 p-4 border border-gray-200 dark:border-gray-800">
            <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-1">Infix Expression Operator</h4>
            <p className="text-xs text-gray-600 dark:text-gray-400">
              Combine conditions natively with <code className="text-sky-600 dark:text-sky-400">and</code> or{' '}
              <code className="text-sky-600 dark:text-sky-400">or</code> operators directly inside event builders.
            </p>
          </div>
          <div className="rounded-xl bg-gray-50 dark:bg-gray-900 p-4 border border-gray-200 dark:border-gray-800">
            <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-1">Backend Method Binding</h4>
            <p className="text-xs text-gray-600 dark:text-gray-400">
              Pass backend function references like{' '}
              <code className="text-sky-600 dark:text-sky-400">::clickToLoadMore</code> to safely map endpoint URIs at
              compile time.
            </p>
          </div>
        </div>
      </div>
    </section>
  );
}

import { Button } from '../../Ui/Button';
import Link from '@docusaurus/Link';
import { LuArrowRight as ArrowRight, LuZap as Zap } from 'react-icons/lu';
import { ThemedCode } from '../ThemedCode';

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

const highlights = [
  {
    title: 'Reactive Signals',
    body: (
      <>
        Define indicators like <code className="text-sky-700 dark:text-sky-400">_fetching</code> as strongly typed
        Kotlin variables instead of raw magic strings.
      </>
    ),
  },
  {
    title: 'Infix Expression Operator',
    body: (
      <>
        Combine conditions natively with <code className="text-sky-700 dark:text-sky-400">and</code> or{' '}
        <code className="text-sky-700 dark:text-sky-400">or</code> operators directly inside event builders.
      </>
    ),
  },
  {
    title: 'Backend Method Binding',
    body: (
      <>
        Pass backend function references like <code className="text-sky-700 dark:text-sky-400">::clickToLoadMore</code>{' '}
        to safely map endpoint URIs at compile time.
      </>
    ),
  },
];

/** Same chrome as CodeComparison's panels: traffic lights, label, then the sample. */
function CodePanel({ label, badge, language, code }: { label: string; badge: string; language: string; code: string }) {
  return (
    <div className="hf-code-panel rounded-2xl bg-gray-50 dark:bg-gray-800 p-1 shadow-xl">
      <div className="rounded-xl bg-white dark:bg-gray-900">
        <div className="border-b border-gray-100 dark:border-gray-800 px-6 py-4">
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-2 min-w-0">
              <div className="flex gap-1.5">
                <div className="h-3 w-3 rounded-full bg-red-400"></div>
                <div className="h-3 w-3 rounded-full bg-yellow-400"></div>
                <div className="h-3 w-3 rounded-full bg-green-400"></div>
              </div>
              <span className="ml-2 truncate text-sm text-gray-600 dark:text-gray-400">{label}</span>
            </div>
            <span className="shrink-0 rounded bg-gray-100 dark:bg-gray-800 px-2 py-0.5 text-xs font-medium text-gray-700 dark:text-gray-300">
              {badge}
            </span>
          </div>
        </div>
        <div className="overflow-x-auto overflow-hidden rounded-b-xl hf-code-block">
          <ThemedCode language={language}>{code}</ThemedCode>
        </div>
      </div>
    </div>
  );
}

export function DatastarSection() {
  return (
    <section className="bg-white dark:bg-gray-950 px-6 py-12 sm:py-24">
      <div className="mx-auto max-w-7xl flex flex-col items-center">
        <div className="mb-8 text-center">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-sky-500/20 bg-sky-500/10 px-4 py-1.5 text-sm font-semibold text-sky-700 dark:text-sky-300">
            <Zap className="h-4 w-4" aria-hidden="true" />
            <span>Reactive Hypermedia</span>
          </div>
          <h2 className="mb-4 text-4xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-5xl">
            Type-Safe Datastar Integration
          </h2>
          <p className="max-w-3xl text-lg text-gray-600 dark:text-gray-300">
            Compose client-side reactivity and signals natively in Kotlin. HtmlFlow converts type-safe expressions like{' '}
            <code className="rounded bg-sky-100 dark:bg-sky-900 px-2 py-0.5 text-sm text-sky-900 dark:text-sky-100">
              !fetching and get(...)
            </code>{' '}
            into valid Datastar hypermedia attributes.
          </p>
        </div>

        <div className="hf-code-comparison-grid">
          <CodePanel
            label="HtmlFlow Datastar DSL (Kotlin)"
            badge="Type-Safe"
            language="kotlin"
            code={datastarKotlinCode}
          />

          <div className="flex items-center justify-center lg:mx-4 my-4 lg:my-0">
            <div className="flex h-12 w-12 rotate-90 items-center justify-center rounded-full bg-white dark:bg-sky-900 border border-gray-200 dark:border-sky-800 lg:rotate-0">
              <ArrowRight className="h-6 w-6 text-sky-600 dark:text-sky-400" aria-hidden="true" />
            </div>
          </div>

          <CodePanel label="Generated Datastar HTML" badge="Compiled Markup" language="html" code={datastarHtmlCode} />
        </div>

        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6 text-left max-w-5xl">
          {highlights.map(highlight => (
            <div
              key={highlight.title}
              className="rounded-lg border border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-900/50 p-6 transition-colors hover:bg-gray-100 dark:hover:bg-gray-800/50"
            >
              <h3 className="mb-2 text-lg font-semibold text-gray-900 dark:text-white">{highlight.title}</h3>
              <p className="text-sm text-gray-600 dark:text-gray-400 leading-relaxed">{highlight.body}</p>
            </div>
          ))}
        </div>

        <div className="mt-16 flex justify-center">
          {/* asChild: a <button> inside an <a> is invalid nesting. */}
          <Button
            asChild
            size="lg"
            variant="outline"
            className="gap-2 border-sky-200 hover:bg-sky-50 dark:border-sky-700 dark:hover:bg-sky-900/50"
          >
            <Link to="/docs/integrations#data-" className="no-underline">
              Read the Datastar Guide
              <ArrowRight className="h-4 w-4" aria-hidden="true" />
            </Link>
          </Button>
        </div>
      </div>
    </section>
  );
}

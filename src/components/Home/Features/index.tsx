import type { ReactNode } from 'react';
import { LuArrowRight as ArrowRight, LuShieldCheck, LuZap, LuCodeXml, LuActivity } from 'react-icons/lu';
import { Button } from '../../Ui/Button';
import Link from '@docusaurus/Link';
import type { IconType } from 'react-icons';

const features: { title: string; description: ReactNode; icon: IconType }[] = [
  {
    icon: LuShieldCheck,
    title: 'Typesafe & Compliant',
    description:
      'Write valid HTML 5.2 with compile-time safety. Catch errors before runtime with full IDE support and type checking.',
  },
  {
    icon: LuZap,
    title: 'High Performance',
    description: (
      <>
        Optimized for speed with excellent throughput and low overhead.{' '}
        {/* No Tailwind colour/underline utilities: Infima styles anchors from
            an unlayered rule, which outranks every layer. See custom.css. */}
        <a href="https://github.com/xmlet/template-benchmark" target="_blank" rel="noopener noreferrer">
          Check the benchmarks
        </a>{' '}
        to see performance results.
      </>
    ),
  },
  {
    icon: LuCodeXml,
    title: 'Pure Java/Kotlin',
    description:
      'Templates are plain Java functions. No new templating language to learn. Use the full Java toolchain for templating.',
  },
  {
    icon: LuActivity,
    title: 'Asynchronous Support',
    description:
      'Bind to async data models seamlessly. Works with Reactive Streams, CompletableFuture, Kotlin coroutines, and any asynchronous API for progressive rendering.',
  },
];

export function Features() {
  return (
    <section className="bg-white dark:bg-gray-950 px-6 py-12 sm:py-24">
      <div className="mx-auto max-w-7xl flex flex-col items-center">
        <div className="mb-8 text-center">
          <h2 className="mb-4 text-4xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-5xl">
            Why Choose HtmlFlow?
          </h2>
        </div>

        <div className="grid gap-6 md:grid-cols-2 max-w-5xl">
          {features.map((feature, index) => (
            <div
              key={index}
              className="rounded-lg bg-gray-50 dark:bg-gray-900/50 p-8 transition-all hover:bg-gray-100 dark:hover:bg-gray-800/50"
            >
              <div className="mb-4 flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100 dark:bg-sky-900/40">
                <feature.icon className="h-5 w-5 text-sky-600 dark:text-sky-400" />
              </div>
              <h3 className="mb-2 text-lg font-semibold text-gray-900 dark:text-white">{feature.title}</h3>
              <p className="text-base text-gray-600 dark:text-gray-400 leading-relaxed">{feature.description}</p>
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
            <Link to="/docs/introduction" className="no-underline">
              Get Started
              <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        </div>
      </div>
    </section>
  );
}

import type { ReactNode } from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import Heading from '@theme/Heading';
import { LuArrowRight as ArrowRight, LuGithub as GithubIcon } from 'react-icons/lu';
import { Button } from '../../../components/Ui/Button';

// Replaces the stock 404, which offered no route back into the site.
export default function NotFoundContent({ className }: { className?: string }): ReactNode {
  return (
    <main className={clsx('container margin-vert--xl', className)}>
      <div className="mx-auto max-w-2xl px-6 text-center">
        {/* sky-700, not sky-600: at 14px the lighter shade is 4.02:1 on white. */}
        <p className="mb-4 text-sm font-semibold tracking-widest text-sky-700 dark:text-sky-400">404</p>

        <Heading as="h1" className="mb-6 text-4xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-5xl">
          Page not found
        </Heading>

        <p className="mb-10 text-lg text-gray-600 dark:text-gray-300">
          That page does not exist, or it moved. The guide below covers installation, the builder API, and the async
          rendering features.
        </p>

        <div className="mb-12 flex flex-wrap items-center justify-center gap-4">
          <Button
            asChild
            size="lg"
            variant="outline"
            className="gap-2 border-sky-200 hover:bg-sky-50 dark:border-sky-700 dark:hover:bg-sky-900/50"
          >
            <Link to="/docs/introduction" className="no-underline">
              Read the guide
              <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
          <Button
            asChild
            size="lg"
            variant="outline"
            className="gap-2 border-sky-200 hover:bg-sky-50 dark:border-sky-700 dark:hover:bg-sky-900/50"
          >
            <Link to="/" className="no-underline">
              Go home
            </Link>
          </Button>
        </div>

        <p className="text-sm text-gray-600 dark:text-gray-400">
          Arrived from a link on this site?{' '}
          <Link to="https://github.com/xmlet/HtmlFlow/issues" className="inline-flex items-center gap-1">
            <GithubIcon className="h-4 w-4" />
            Report the broken link
          </Link>
        </p>
      </div>
    </main>
  );
}

import { Button } from "./ui/button";
import { ArrowRight, Github } from "lucide-react";
import Link from "@docusaurus/Link";
import { WaveBackground } from "./WaveBackground";

export function Hero() {
  return (
    <section className="relative overflow-hidden bg-white dark:bg-gray-950 px-6 py-16 sm:py-24">
      <WaveBackground />
      
      <div className="relative mx-auto max-w-7xl" style={{ zIndex: 1 }}>
        <div className="grid items-center gap-12 lg:grid-cols-2">
          <div className="text-center lg:text-left">
            <h1 className="mb-6 text-5xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-6xl lg:text-7xl">
              Write HTML with{" "}
              <span className="text-sky-600 dark:text-sky-400">
                Compile-Time Safety
              </span>
            </h1>

            <p className="mb-8 mx-auto lg:mx-0 max-w-xl text-lg text-gray-600 dark:text-gray-300 sm:text-xl">
              HtmlFlow is a Java DSL for building typesafe HTML templates in a fluent style. 
              Get full IDE support, type checking, and excellent performance.
            </p>

            <div className="flex flex-wrap items-center justify-center lg:justify-start gap-4">
              <Link to="/docs/intro" style={{ textDecoration: 'none' }}>
                <Button size="lg" variant="outline" className="gap-2 border-sky-200 hover:bg-sky-50 dark:border-sky-700 dark:hover:bg-sky-900/50">
                  Get Started
                  <ArrowRight className="h-4 w-4" />
                </Button>
              </Link>
              <Link to="https://github.com/xmlet/HtmlFlow" style={{ textDecoration: 'none' }}>
                <Button size="lg" variant="outline" className="gap-2 border-sky-200 hover:bg-sky-50 dark:border-sky-700 dark:hover:bg-sky-900/50">
                  <Github className="h-4 w-4" />
                  View on GitHub
                </Button>
              </Link>
            </div>
          </div>

          <div className="relative flex items-center justify-center lg:justify-end">
            <div className="relative">
              <div className="relative flex h-64 w-64 items-center justify-center rounded-full bg-white dark:bg-gray-800 shadow-xl border-4 border-sky-100 dark:border-sky-900 sm:h-80 sm:w-80">
                <img 
                  src="img/htmlflow-logo.png" 
                  alt="HtmlFlow Logo" 
                  className="h-48 w-48 sm:h-56 sm:w-56"
                />
              </div>
            </div>
          </div>
        </div>

        <div className="mt-8 flex flex-wrap items-center justify-center gap-8 text-sm text-gray-600 dark:text-gray-400 lg:justify-start">
          <div className="flex items-center gap-2">
            <img 
              src="https://img.shields.io/maven-central/v/com.github.xmlet/htmlflow?color=0ea5e9" 
              alt="Maven Central" 
              className="h-5"
            />
          </div>
          <div className="flex items-center gap-2">
            <img 
              src="https://img.shields.io/github/stars/xmlet/htmlflow?style=social" 
              alt="GitHub Stars" 
              className="h-5"
            />
          </div>
        </div>
      </div>
    </section>
  );
}
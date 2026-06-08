import type { ReactNode } from 'react';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import { Hero } from '../components/Home/Hero';
import { CodeComparison } from '../components/Home/CodeComparison';
import { Features } from '../components/Home/Features';

export default function Home(): ReactNode {
  const { siteConfig } = useDocusaurusContext();
  return (
    <Layout title={siteConfig.title} description={siteConfig.tagline}>
      <main>
        <Hero />
        <CodeComparison />
        <Features />
      </main>
    </Layout>
  );
}

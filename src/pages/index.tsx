import type {ReactNode} from 'react';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import { Hero } from '../components/Hero';
import { CodeComparison } from '../components/CodeComparison';
import { Features } from '../components/Features';

export default function Home(): ReactNode {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={siteConfig.title}
      description={siteConfig.tagline}
    >
      <main>
        <Hero />
        <CodeComparison />
        <Features />
      </main>
    </Layout>
  );
}

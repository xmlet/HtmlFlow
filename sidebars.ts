import type { SidebarsConfig } from '@docusaurus/plugin-content-docs';

const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    {
      type: 'category',
      label: 'Introduction',
      collapsed: false,
      collapsible: true,
      link: { type: 'doc', id: 'introduction' },
      items: [
        { type: 'link', label: 'What is HtmlFlow?', href: '/docs/introduction#what-is-htmlflow' },
        { type: 'link', label: 'Motivation', href: '/docs/introduction#motivation' },
        { type: 'link', label: 'Approach', href: '/docs/introduction#approach' },
      ],
    },
    {
      type: 'category',
      label: 'Getting Started',
      collapsed: false,
      collapsible: true,
      link: { type: 'doc', id: 'getting-started' },
      items: [
        { type: 'link', label: 'Installation', href: '/docs/getting-started#installation' },
        { type: 'link', label: 'Quick Start', href: '/docs/getting-started#quick-start' },
      ],
    },
    {
      type: 'category',
      label: 'Core Concepts',
      collapsed: false,
      collapsible: true,
      link: { type: 'doc', id: 'core-concepts' },
      items: [
        { type: 'link', label: 'HTML Builders', href: '/docs/core-concepts#html-builders' },
        { type: 'link', label: 'Html Templates', href: '/docs/core-concepts#html-templates' },
        { type: 'link', label: 'Models and Data Binding', href: '/docs/core-concepts#models-and-data-binding' },
        {
          type: 'link',
          label: 'Conditional Rendering and Loops',
          href: '/docs/core-concepts#conditional-rendering-and-loops',
        },
        { type: 'link', label: 'Configuring Views', href: '/docs/core-concepts#configuring-views' },
        { type: 'link', label: 'Reusable Components', href: '/docs/core-concepts#reusable-components' },
        { type: 'link', label: 'Dynamic Content', href: '/docs/core-concepts#dynamic-content' },
      ],
    },
    {
      type: 'category',
      label: 'Advanced',
      collapsed: false,
      collapsible: true,
      link: { type: 'doc', id: 'advanced' },
      items: [
        { type: 'link', label: 'Asynchronous Rendering', href: '/docs/advanced#asynchronous-rendering' },
        { type: 'link', label: 'Streaming HTML', href: '/docs/advanced#streaming-html' },
        { type: 'link', label: 'Flowifier', href: '/docs/advanced#flowifier' },
      ],
    },
    {
      type: 'category',
      label: 'Integrations',
      collapsed: false,
      collapsible: true,
      link: { type: 'doc', id: 'integrations' },
      items: [
        { type: 'link', label: 'data-*', href: '/docs/integrations#data-' },
        { type: 'link', label: 'http4k', href: '/docs/integrations#http4k' },
      ],
    },
    {
      type: 'category',
      label: 'Resources',
      collapsed: false,
      collapsible: true,
      link: { type: 'doc', id: 'resources' },
      items: [
        { type: 'link', label: 'Articles', href: '/docs/resources#articles' },
        { type: 'link', label: 'Talks', href: '/docs/resources#talks' },
        { type: 'link', label: 'Repositories', href: '/docs/resources#repositories' },
      ],
    },
  ],
};

export default sidebars;

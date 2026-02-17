import { themes as prismThemes } from 'prism-react-renderer';
import type { Config } from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
  title: 'HtmlFlow',
  tagline: 'Type-safe HTML for Java and Kotlin',
  favicon: 'img/htmlflow-logo.png',

  future: {
    v4: true,
  },

  url: 'https://htmlflow.org',
  baseUrl: '/',
  organizationName: 'xmlet',
  projectName: 'HtmlFlow',

  onBrokenLinks: 'throw',

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  clientModules: [require.resolve('./src/client-modules/aos.ts')],

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
        },
        blog: {
          showReadingTime: true,
          feedOptions: {
            type: ['rss', 'atom'],
            xslt: true,
          },
          onInlineTags: 'warn',
          onInlineAuthors: 'warn',
          onUntruncatedBlogPosts: 'warn',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    colorMode: {
      respectPrefersColorScheme: true,
      disableSwitch: true,
    },
    navbar: {
      title: 'HtmlFlow',
      logo: {
        alt: 'HtmlFlow Logo',
        src: 'img/htmlflow-logo.svg',
      },
      items: [
        {
          type: 'search',
          position: 'left',
        },
        {
          href: 'https://github.com/xmlet/HtmlFlow/issues',
          label: 'Feedback',
          position: 'right',
        },
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'right',
          label: 'Guide',
        },
        { to: '/blog', label: 'News', position: 'right' },
        {
          type: 'custom-divider',
          position: 'right',
        },
        {
          type: 'custom-theme-toggle',
          position: 'right',
        },
        {
          type: 'custom-divider',
          position: 'right',
        },
        {
          type: 'custom-github',
          label: '',
          href: 'https://github.com/xmlet/HtmlFlow',
          position: 'right',
        },
      ],
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
  plugins: ['./src/plugins/tailwind-config.js'],
};

export default config;

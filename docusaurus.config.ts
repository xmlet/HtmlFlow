import { themes as prismThemes } from 'prism-react-renderer';
import type { PrismTheme } from 'prism-react-renderer';
import type { Config } from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const LATEST_VERSION = '5.0.4';

/**
 * Darkens the One themes' sub-4.5:1 tokens in place. No prism-react-renderer
 * light theme clears WCAG AA outright, so patching beats swapping themes.
 * Keyed by original colour so the mapping stays exact.
 */
function withAccessibleTokens(theme: PrismTheme, replacements: Record<string, string>): PrismTheme {
  return {
    ...theme,
    styles: theme.styles.map(entry => {
      const replacement = entry.style?.color && replacements[entry.style.color];
      return replacement ? { ...entry, style: { ...entry.style, color: replacement } } : entry;
    }),
  };
}

const codeThemeLight = withAccessibleTokens(prismThemes.oneLight, {
  'hsl(230, 4%, 64%)': '#727277',
  'hsl(35, 99%, 36%)': '#a86201',
  'hsl(5, 74%, 59%)': '#c44a3f',
  'hsl(119, 34%, 47%)': '#40813f',
  'hsl(221, 87%, 60%)': '#3a6ddc',
  'hsl(198, 99%, 37%)': '#017baf',
});

const codeThemeDark = withAccessibleTokens(prismThemes.oneDark, {
  'hsl(220, 10%, 40%)': '#8f939c',
  'hsl(355, 65%, 65%)': '#e17079',
});

const config: Config = {
  title: 'HtmlFlow',
  tagline: 'Type-safe HTML for Java and Kotlin',
  favicon: 'img/htmlflow-logo.png',

  future: {
    v4: true,
  },

  markdown: {
    mdx1Compat: {
      admonitions: true,
    },
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

  customFields: {
    latestVersion: LATEST_VERSION,
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
        },
        blog: {
          // Rendered as the list page's <h1>.
          blogTitle: 'News',
          blogSidebarCount: 'ALL',
          blogSidebarTitle: 'All Posts',
          showReadingTime: true,
          feedOptions: {
            type: ['rss'],
            xslt: true,
          },
          onInlineTags: 'warn',
          onInlineAuthors: 'warn',
          onUntruncatedBlogPosts: 'warn',
        },
        theme: {
          customCss: [
            './src/css/custom.css',
            './src/css/blog.css',
            './src/css/pagination.css',
            './src/css/mobile-sidebar.css',
            './src/theme/DocSidebarItem/sidebar.css',
          ],
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    colorMode: {
      respectPrefersColorScheme: true,
      // Must stay false. disableSwitch also drops the localStorage read from
      // the pre-paint script and deletes the stored choice on mount, so the
      // theme never persists. theme/Navbar/ColorModeToggle hides the stock
      // toggle this exposes.
      disableSwitch: false,
    },
    navbar: {
      title: 'HtmlFlow',
      logo: {
        alt: 'HtmlFlow Logo',
        src: 'img/htmlflow-logo.png',
        srcDark: 'img/htmlflow-logo.png',
        href: '/',
      },
      items: [
        {
          type: 'custom-search',
          position: 'right',
        },
        {
          href: 'https://github.com/xmlet/HtmlFlow/issues',
          label: 'Feedback',
          position: 'right',
        },
        {
          to: '/docs/introduction',
          label: 'Guide',
          position: 'right',
          activeBaseRegex: '/docs/',
        },
        { to: '/blog', label: 'News', position: 'right' },
        {
          type: 'custom-github',
          // Becomes the link's aria-label; '' leaves it nameless.
          label: 'GitHub',
          href: 'https://github.com/xmlet/HtmlFlow',
          position: 'right',
        },
        {
          type: 'custom-rss',
          href: '/blog/rss.xml',
          label: 'RSS',
          position: 'right',
          title: 'Blog RSS Feed',
        },
        {
          type: 'custom-theme-toggle',
          position: 'right',
        },
      ],
    },
    prism: {
      theme: codeThemeLight,
      darkTheme: codeThemeDark,
      additionalLanguages: ['java', 'kotlin'],
    },
    footer: {
      logo: {
        alt: 'HtmlFlow',
        src: 'img/htmlflow-logo.png',
        width: 32,
        height: 32,
      },
      links: [
        {
          title: 'Docs',
          items: [
            { label: 'Introduction', to: '/docs/introduction' },
            { label: 'Getting Started', to: '/docs/getting-started' },
            { label: 'Core Concepts', to: '/docs/core-concepts' },
            { label: 'Advanced', to: '/docs/advanced' },
          ],
        },
        {
          title: 'Community',
          items: [
            { label: 'GitHub', href: 'https://github.com/xmlet/HtmlFlow' },
            { label: 'Issues', href: 'https://github.com/xmlet/HtmlFlow/issues' },
            { label: 'Releases', href: 'https://github.com/xmlet/HtmlFlow/releases' },
          ],
        },
        {
          title: 'More',
          items: [
            { label: 'News', to: '/blog' },
            { label: 'RSS Feed', href: 'pathname:///blog/rss.xml' },
            { label: 'Maven Central', href: 'https://search.maven.org/artifact/com.github.xmlet/htmlflow' },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} HtmlFlow.`,
    },
  } satisfies Preset.ThemeConfig,
  plugins: [
    './src/plugins/tailwind-config.js',
    './src/plugins/agent-ready.js',
    [
      '@docusaurus/plugin-client-redirects',
      {
        redirects: [
          { from: '/install', to: '/docs/getting-started' },
          { from: '/features', to: '/docs/introduction' },
          { from: '/features_version3', to: '/docs/introduction' },
          { from: '/about', to: '/docs/introduction' },
          { from: '/news.html', to: '/blog' },
          { from: '/news_archive.html', to: '/blog' },
        ],
      },
    ],
    [
      require.resolve('@easyops-cn/docusaurus-search-local'),
      {
        hashed: true,
        docsRouteBasePath: '/docs',
        blogRouteBasePath: '/blog',
        indexBlog: true,
        indexDocs: true,
        indexPages: false,
        searchBarShortcut: false,
        searchBarShortcutHint: false,
        searchBarPosition: 'right',
        highlightSearchTermsOnTargetPage: true,
        searchBarShortcutKeymap: 'mod+k',
      },
    ],
  ],
};

export default config;

/**
 * Sanitization utilities used to safely insert untrusted/streamed HTML
 * into the DOM, via the Sanitizer API.
 */

/**
 * Allow-list of elements permitted in HTML streamed/fetched from an MFE resource.
 * Anything not on this list is stripped out by the sanitizer before being inserted into the DOM.
 */
export const ALLOWED_ELEMENTS: string[] = [
    // Structural / sectioning
    'div', 'span', 'main', 'section', 'article', 'aside', 'nav', 'header', 'footer',
    // Headings
    'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
    // Text content
    'p', 'br', 'hr', 'blockquote', 'pre', 'code', 'em', 'strong', 'small', 'b', 'i', 'u', 'mark', 'sub', 'sup', 'abbr', 'cite', 'q', 'time',
    // Lists
    'ul', 'ol', 'li', 'dl', 'dt', 'dd',
    // Tables
    'table', 'thead', 'tbody', 'tfoot', 'tr', 'th', 'td', 'caption', 'colgroup', 'col',
    // Forms & interactive
    'form', 'input', 'button', 'select', 'option', 'optgroup', 'textarea', 'label', 'fieldset', 'legend', 'output', 'datalist',
    // Media & embedded
    'img', 'picture', 'source', 'figure', 'figcaption', 'video', 'audio',
    // Links & resources
    'a', 'link',
    // Misc
    'details', 'summary', 'template', 'slot', 'data', 'meter', 'progress',
];

/**
 * Parses `html` into a `<template>`, sanitizing it against {@link ALLOWED_ELEMENTS} when the
 * Sanitizer API (`Element.setHTML`) is available. It falls back to an unsanitized
 * parse (`setHTMLUnsafe`) for older browsers. The returned template's `.content` is the actual
 * `DocumentFragment` to append/insert into the DOM.
 * @param html - raw HTML string to parse (e.g. a chunk fetched from an MFE resource).
 * @returns a `<template>` element whose `.content` holds the nodes.
 */
export function createSafeHtml(html: string): HTMLTemplateElement {
    const template = document.createElement('template');
    if ('setHTML' in Element.prototype) {
        (template as any).setHTML(html, { sanitizer: { elements: ALLOWED_ELEMENTS } });
    } else {
        // For support on older browsers DOMPurify could be used
        template.setHTMLUnsafe(html);
    }
    return template;
}


// src/modules/theme-loader.ts
var styleSheetCache = /* @__PURE__ */ new Map();
async function adoptStylesheet(target, url) {
  const sheet = await loadStylesheet(url);
  if (!target.adoptedStyleSheets.includes(sheet)) {
    target.adoptedStyleSheets = [...target.adoptedStyleSheets, sheet];
  }
}
function loadStylesheet(url) {
  let pending = styleSheetCache.get(url);
  if (!pending) {
    pending = fetchAndParse(url).catch((err) => {
      styleSheetCache.delete(url);
      throw err;
    });
    styleSheetCache.set(url, pending);
  }
  return pending;
}
async function fetchAndParse(url) {
  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`Failed to fetch stylesheet "${url}": ${res.status}`);
  }
  const css = await res.text();
  const sheet = new CSSStyleSheet();
  sheet.replaceSync(css);
  return sheet;
}

// src/modules/html-sanitizer.ts
var ALLOWED_ELEMENTS = [
  // Structural / sectioning
  "div",
  "span",
  "main",
  "section",
  "article",
  "aside",
  "nav",
  "header",
  "footer",
  // Headings
  "h1",
  "h2",
  "h3",
  "h4",
  "h5",
  "h6",
  // Text content
  "p",
  "br",
  "hr",
  "blockquote",
  "pre",
  "code",
  "em",
  "strong",
  "small",
  "b",
  "i",
  "u",
  "mark",
  "sub",
  "sup",
  "abbr",
  "cite",
  "q",
  "time",
  // Lists
  "ul",
  "ol",
  "li",
  "dl",
  "dt",
  "dd",
  // Tables
  "table",
  "thead",
  "tbody",
  "tfoot",
  "tr",
  "th",
  "td",
  "caption",
  "colgroup",
  "col",
  // Forms & interactive
  "form",
  "input",
  "button",
  "select",
  "option",
  "optgroup",
  "textarea",
  "label",
  "fieldset",
  "legend",
  "output",
  "datalist",
  // Media & embedded
  "img",
  "picture",
  "source",
  "figure",
  "figcaption",
  "video",
  "audio",
  // Links & resources
  "a",
  "link",
  // Misc
  "details",
  "summary",
  "template",
  "slot",
  "data",
  "meter",
  "progress"
];
function createSafeHtml(html) {
  const template = document.createElement("template");
  if ("setHTML" in Element.prototype) {
    template.setHTML(html, { sanitizer: { elements: ALLOWED_ELEMENTS } });
  } else {
    template.setHTMLUnsafe(html);
  }
  return template;
}

// src/htmlflow-mfe-engine.ts
var MfcEventType = /* @__PURE__ */ ((MfcEventType2) => {
  MfcEventType2["RELOAD"] = "RELOAD";
  return MfcEventType2;
})(MfcEventType || {});
var MfeReloadEvent = class extends CustomEvent {
  constructor(type = "RELOAD" /* RELOAD */) {
    super("reload", { detail: { type } });
  }
};
var Mfe = class _Mfe extends HTMLElement {
  constructor() {
    super();
    this.mfeReady = false;
    this.mfeName = "";
    this.mfeUrlResource = "";
    this.mfeListeningEventName = "";
    this.mfeTriggerEventName = "";
    /** Suffix appended to `mfeName` to form the "fragment ready" event name on the event bus. */
    this.readyEventSuffix = "-fragment-ready";
    this.mfeStylingUrl = "";
    this.mfeSharedStylingUrl = "";
    this.isMfeStreamingData = "";
    /** Aborts any in-flight non-streaming fetch when a new load/reload starts. */
    this.abortController = null;
    /** Bound once so the same function reference can be added/removed as an event listener. */
    this.bindReload = this.reloadFragment.bind(this);
    /** Listeners registered via {@link createContext}'s `listenMfeEvent`, tracked for cleanup on reload/disconnect. */
    this.windowListeners = [];
    this.attachShadow({ mode: "open" });
  }
  static {
    // prevent new reference
    /** Maps `mfeName` -> live instance, so {@link onReady} can look up an already-connected fragment. */
    this.registry = /* @__PURE__ */ new Map();
  }
  static {
    /** Buffers `onReady` callbacks registered before their target fragment has connected/registered itself. */
    this.pendingCallBacks = /* @__PURE__ */ new Map();
  }
  static {
    /** Single event bus shared by every `<micro-frontend>` instance on the page. */
    this.mfeEventBus = new EventTarget();
  }
  /** Removes every listener registered through this fragment's `MfeContext`, then clears the tracking list. */
  cleanupWindowListeners() {
    this.windowListeners.forEach(({ eventName, handler }) => {
      _Mfe.mfeEventBus.removeEventListener(eventName, handler);
    });
    this.windowListeners = [];
  }
  /**
   * Standard custom element lifecycle hook. Reads configuration attributes, wires up the
   * reload listener, validates required attributes, registers this instance so
   * {@link onReady} can find it, flushes any callbacks that were queued before this fragment
   * connected, and kicks off the initial fragment load.
   */
  connectedCallback() {
    this.collectMfeAttributes();
    if (this.mfeListeningEventName) {
      console.log(this.mfeListeningEventName);
      _Mfe.mfeEventBus.addEventListener(this.mfeListeningEventName, this.bindReload);
    }
    if (!this.mfeUrlResource) {
      console.error(`MFE -> ${this.mfeName} is missing the "mfe-url" attribute.`);
      return;
    }
    if (!this.mfeName) {
      console.error(`MFE -> ${this.mfeName} is missing the "mfe-name" attribute. The ready event will be dispatched with an undefined name.`);
      return;
    }
    if (!this.mfeTriggerEventName) {
      console.error(`MFE -> ${this.mfeName} is missing the "mfe-trigger-event" attribute. The fragment will not be able to trigger any custom events.`);
    }
    if (!this.mfeListeningEventName) {
      console.error(`MFE -> ${this.mfeName} is missing the "mfe-listen-event" attribute. The fragment will not reload on any event.`);
    }
    if (!this.mfeStylingUrl) {
      console.warn(`MFE -> ${this.mfeName} is missing the "mfe-styling-url" attribute. The fragment will not fetch any custom styling.`);
    }
    this.shadowRoot?.appendChild(document.createTextNode(`Loading ${this.mfeName}...`));
    _Mfe.registry.set(this.mfeName, this);
    _Mfe.pendingCallBacks.get(this.mfeName)?.forEach((cb) => this.onInit(cb));
    _Mfe.pendingCallBacks.delete(this.mfeName);
    this.loadFragment();
  }
  /** Reads and stores all `mfe-*` configuration attributes from the host element. */
  collectMfeAttributes() {
    this.mfeUrlResource = this.getAttribute("mfe-url");
    this.mfeName = this.getAttribute("mfe-name");
    this.mfeStylingUrl = this.getAttribute("mfe-styling-url");
    this.mfeSharedStylingUrl = this.getAttribute("mfe-shared-styling-url");
    this.mfeListeningEventName = this.getAttribute("mfe-listen-event");
    this.mfeTriggerEventName = this.getAttribute("mfe-trigger-event");
    this.isMfeStreamingData = this.getAttribute("mfe-stream-data");
  }
  /**
   * Registers a callback to run once this fragment is ready. If it's already ready, the
   * callback runs synchronously with the current context; otherwise it's deferred until the
   * fragment's "ready" event fires on the shared event bus (at which point any listeners
   * registered through a previous context are also cleaned up, since a new context will be
   * created for this callback).
   */
  onInit(callBackFn) {
    if (this.mfeReady && this.shadowRoot) {
      callBackFn(this.createContext(this.shadowRoot));
    } else _Mfe.mfeEventBus.addEventListener(this.mfeName + this.readyEventSuffix, () => {
      this.cleanupWindowListeners();
      callBackFn(this.createContext(this.shadowRoot));
    });
  }
  /** Builds the {@link MfeContext} object handed to `onInit`/`mfe()` callbacks for this fragment. */
  createContext(root) {
    return {
      root,
      triggerMfeEvent: (message, payload, eventName) => this.triggerEvent(eventName ?? this.mfeTriggerEventName, message, payload),
      listenMfeEvent: (listener, eventName) => {
        const evName = eventName ?? this.mfeListeningEventName;
        _Mfe.mfeEventBus.addEventListener(evName, listener);
        this.windowListeners.push({ eventName: evName, handler: listener });
      },
      reloadMfe: () => this.reloadFragment(new MfeReloadEvent()),
      mfeEvents: MfcEventType
    };
  }
  /**
   * Looks up the named fragment and invokes `onInit` on it if it has already connected;
   * otherwise queues the callback in {@link pendingCallBacks} until a fragment with that
   * name connects and drains the queue (see {@link connectedCallback}).
   */
  static onReady(mfeName, cbFn) {
    const mfeInstance = _Mfe.registry.get(mfeName);
    if (mfeInstance) {
      mfeInstance.onInit(cbFn);
    } else {
      const queue = _Mfe.pendingCallBacks.get(mfeName) ?? [];
      queue.push(cbFn);
      _Mfe.pendingCallBacks.set(mfeName, queue);
    }
  }
  /**
   * Fetches this fragment's full HTML in one buffered request, used by
   * {@link loadFragment} when `mfe-stream-data` is not `"true"`. Aborts any previous in-flight
   * fetch first, so overlapping requests do not create race conditions.
   * @returns the response body text, or undefined if no `mfe-url` is configured.
   */
  async fetchData() {
    this.abortController?.abort();
    this.abortController = new AbortController();
    if (this.mfeUrlResource) {
      const response = await fetch(this.mfeUrlResource, {
        signal: this.abortController.signal
      });
      if (!response.ok) {
        throw new Error(`Failed to fetch ${this.mfeUrlResource}: ${response.status}`);
      }
      return response.text();
    }
  }
  /**
   * Fetches and incrementally renders this fragment's HTML as it streams in, used by
   * {@link loadFragment} when `mfe-stream-data` is `"true"`. Clears any previous content,
   * adopts stylesheets up front, then for each decoded chunk: the first chunk containing a
   * slot element defined is treated as the container markup and appended directly to the shadow
   * root. It must include any `<slot name="x">` placeholders the later chunks target,
   * subsequent chunks carrying a `slot="x"` attribute are appended as that slot's fallback
   * content (looked up via `slot[name="x"]`) rather than via native slot *assignment*, since
   * nothing is placed in light DOM. Any other chunk is appended directly to the shadow root.
   */
  async fetchStreamData() {
    const decoder = new TextDecoder();
    if (this.shadowRoot) {
      this.shadowRoot.replaceChildren();
    }
    this.adoptStyleSheets();
    if (this.mfeUrlResource) {
      const response = await fetch(this.mfeUrlResource);
      if (!response.ok) {
        throw new Error(`[micro-frontend "${this.mfeName}"] fetch failed: ${response.status}`);
      }
      if (response.body) {
        for await (const value of response.body) {
          const chunk = decoder.decode(new Uint8Array(value), { stream: true });
          const node = createSafeHtml(chunk);
          if (node.content.querySelector("slot")) {
            this.shadowRoot?.append(node.content);
            continue;
          } else {
            console.error("No <slot> elements were found in the wrapper, content wont be rendered correctly.");
          }
          const slotted = node.content.querySelector("[slot]");
          if (slotted) {
            const slotName = slotted.getAttribute("slot");
            this.shadowRoot?.querySelector(`slot[name="${slotName}"]`)?.append(node.content);
          } else {
            this.shadowRoot?.append(node.content);
          }
        }
      }
    }
  }
  /**
   * Loads this fragment's configured
   * stylesheets, then assigns them to the shadow root's `adoptedStyleSheets`. Both URLs are
   * fetched/parsed concurrently via {@link loadStylesheet} promise cache; failures on either URL are
   * caught and logged, resolving to `null` so one broken stylesheet doesn't block the other.
   * The shared stylesheet is also adopted onto `document.adoptedStyleSheets` (via
   * {@link adoptStylesheet}) so the host shell picks up the same theme.
   */
  adoptStyleSheets() {
    if (!this.mfeSharedStylingUrl && !this.mfeStylingUrl) {
      return;
    }
    const safeLoadStylesheet = (url, label) => url ? loadStylesheet(url).catch((err) => {
      console.error(`Failed to load ${label}`, err);
      return null;
    }) : Promise.resolve(null);
    const sharedSheet$ = safeLoadStylesheet(this.mfeSharedStylingUrl, "shared stylesheet");
    const ownSheet$ = safeLoadStylesheet(this.mfeStylingUrl, `stylesheet for MFE -> ${this.mfeName}`);
    if (this.mfeSharedStylingUrl) {
      adoptStylesheet(document, this.mfeSharedStylingUrl).catch((err) => console.error(`Failed to adopt shared theme onto document for MFE -> ${this.mfeName}`, err));
    }
    Promise.all([sharedSheet$, ownSheet$]).then(([sharedSheet, ownSheet]) => {
      if (this.shadowRoot) {
        this.shadowRoot.adoptedStyleSheets = [sharedSheet, ownSheet].filter((sheet) => sheet !== null);
      }
    });
  }
  /**
   * Renders a fully-buffered HTML string into the shadow root: clears
   * any previous content, sanitizes the HTML via {@link createSafeHtml}, adopts stylesheets,
   * then appends the sanitized fragment.
   */
  buildFragment(html) {
    if (this.shadowRoot) {
      this.shadowRoot.replaceChildren();
    }
    const fragment = createSafeHtml(html);
    this.adoptStyleSheets();
    this.shadowRoot?.append(fragment.content);
  }
  /**
   * Dispatches a custom event on the shared MFE event bus, carrying `message`/`payload` in
   * `detail`. Used both internally (e.g. reload signaling) and via {@link MfeContext.triggerMfeEvent}.
   */
  triggerEvent(eventName, message, payload) {
    const event = new CustomEvent(eventName, {
      detail: { message, payload }
    });
    _Mfe.mfeEventBus.dispatchEvent(event);
  }
  /**
   * Event bus handler bound to `mfe-listen-event`; re-triggers {@link loadFragment} when the
   * received event is an {@link MfeReloadEvent} whose `detail.type` is {@link MfcEventType.RELOAD}
   */
  reloadFragment(event) {
    if (event instanceof MfeReloadEvent) {
      this.mfeReady = false;
      if (event.detail.type === "RELOAD" /* RELOAD */) {
        this.loadFragment();
      }
    }
  }
  /**
   * Dispatches to either {@link fetchStreamData} or {@link fetchData} + {@link buildFragment}
   * and emits the "fragment ready" event once rendering completes.
   */
  loadFragment() {
    if (this.isMfeStreamingData === "true") {
      this.fetchStreamData().then((r) => this.emitFragmentReadiness()).catch((err) => {
        this.shadowRoot?.appendChild(document.createTextNode(`Failed to fetch: ${this.mfeName}`));
        console.error(`An error occured for MFE -> ${this.mfeName}`, err);
      });
    } else {
      this.fetchData().then((r) => {
        if (r) {
          this.buildFragment(r);
          this.emitFragmentReadiness();
        }
      }).catch((err) => {
        this.shadowRoot?.appendChild(document.createTextNode(`Failed to fetch: ${this.mfeName}`));
        console.error(`An error occured for MFE -> ${this.mfeName}`, err);
      });
    }
  }
  /** Marks this fragment ready and dispatches its "fragment ready" event on the shared bus, triggering any pending `onInit`/`onReady` callbacks. */
  emitFragmentReadiness() {
    this.mfeReady = true;
    console.info(`Dispatching ${this.mfeName}${this.readyEventSuffix} event!`);
    _Mfe.mfeEventBus.dispatchEvent(new Event(this.mfeName + this.readyEventSuffix));
  }
  /** Standard custom element lifecycle hook: cleans up event listeners and aborts any in-flight fetch when this fragment is removed from the page. */
  disconnectedCallback() {
    this.cleanupWindowListeners();
    console.log(`MFE -> ${this.mfeName} removed from page.`);
    if (this.mfeListeningEventName) _Mfe.mfeEventBus.removeEventListener(this.mfeListeningEventName, this.bindReload);
    this.abortController?.abort();
  }
  adoptedCallback() {
    console.log(`MFE -> ${this.mfeName} moved to new page.`);
  }
  /**
   * Instance-level alias for {@link onReady}, allowing a fragment reference to be obtained
   * through element query selection to still.
   */
  mfe(name, callback) {
    _Mfe.onReady(name, callback);
  }
};
window.customElements.define("micro-frontend", Mfe);
window.mfe = (name, callbackFn) => Mfe.onReady(name, callbackFn);
var htmlflow_mfe_engine_default = Mfe;
export {
  htmlflow_mfe_engine_default as default
};

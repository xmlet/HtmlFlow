package htmlflow.test.e2e;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.microsoft.playwright.Locator;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

/**
 * Two MFEs talking over the engine's event bus:
 * <ul>
 *   <li>microfrontend_1 button click -> payload rendered as a list item in microfrontend_2;</li>
 *   <li>microfrontend_1 button click -> microfrontend_2 reloads itself and shows the new server content.</li>
 * </ul>
 */
public class MfeEventsIT extends MfeE2EBase {

    private static final String EVENT_NAME = "itemAdded";

    private static final HtmlView<?> HOST_PAGE = HtmlFlow.ViewFactory.builder()
        .mfeEnabled(true)
        .preEncoding(true)
        .build()
        .view(page -> page
            .html()
                .head()
                    .script().attrType(EnumTypeScriptType.MODULE).attrSrc(ENGINE_URL).__()
                    .script().attrType(EnumTypeScriptType.MODULE).attrSrc(TEST_JS_ROUTE + "mfe-events.js").__()
                .__()
                .body()
                    .div()
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragments/microfrontend_1");
                            cfg.setMfeName("microfrontend_1");
                            cfg.setMfeTriggersEventName(EVENT_NAME);
                        })
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragments/microfrontend_2");
                            cfg.setMfeName("microfrontend_2");
                            cfg.setMfeListeningEventName(EVENT_NAME);
                        })
                    .__()
                .__()
            .__());

    private static final String RELOAD_EVENT_NAME = "reloadRequested";

    private static final HtmlView<?> RELOAD_HOST_PAGE = HtmlFlow.ViewFactory.builder()
        .mfeEnabled(true)
        .preEncoding(true)
        .build()
        .view(page -> page
            .html()
                .head()
                    .script().attrType(EnumTypeScriptType.MODULE).attrSrc(ENGINE_URL).__()
                    .script().attrType(EnumTypeScriptType.MODULE).attrSrc(TEST_JS_ROUTE + "mfe-reload.js").__()
                .__()
                .body()
                    .div()
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragments/reload/microfrontend_1");
                            cfg.setMfeName("microfrontend_1");
                            cfg.setMfeTriggersEventName(RELOAD_EVENT_NAME);
                        })
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragments/reload/microfrontend_2");
                            cfg.setMfeName("microfrontend_2");
                            cfg.setMfeListeningEventName(RELOAD_EVENT_NAME);
                        })
                    .__()
                .__()
            .__());

    // Bumped on every microfrontend_2 fetch, so each (re)load returns different content.
    // AtomicInteger because the server answers on its own threads.
    private static final AtomicInteger version = new AtomicInteger();

    private static MfeTestServer server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new MfeTestServer()
            .html("/", HOST_PAGE::render)
            .html("/fragments/microfrontend_1", () -> "<button id=\"send\">Send item</button>")
            .html("/fragments/microfrontend_2", () -> "<ul id=\"items\"></ul>")
            .html("/reload", RELOAD_HOST_PAGE::render)
            .html("/fragments/reload/microfrontend_1", () -> "<button id=\"reload\">Reload</button>")
            .html("/fragments/reload/microfrontend_2", () -> "<p id=\"version\">Version " + version.incrementAndGet() + "</p>")
            .start();
    }

    @Before
    public void resetVersion() {
        version.set(0);
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    @Test
    public void shouldSendEventFromMicrofrontend1AndRenderItemInMicrofrontend2() {
        page.navigate(server.url("/"));

        Locator mfe1 = page.locator("micro-frontend[mfe-name='microfrontend_1']");
        Locator mfe2 = page.locator("micro-frontend[mfe-name='microfrontend_2']");
        assertThat(mfe1.locator("#send")).isVisible();
        assertThat(mfe2.locator("#items")).isAttached();

        mfe1.locator("#send").click();

        Locator items = mfe2.locator("#items li");
        assertThat(items).hasCount(1);
        assertThat(items.first()).hasText("test 1");
        assertThat(items.first()).hasAttribute("data-id", "1");
        assertThat(mfe1.locator("li")).hasCount(0);
        assertEquals(List.of(), pageErrors);
    }

    @Test
    public void shouldReloadMicrofrontend2WhenEventIsReceived() {
        page.navigate(server.url("/reload"));

        Locator mfe1 = page.locator("micro-frontend[mfe-name='microfrontend_1']");
        Locator mfe2 = page.locator("micro-frontend[mfe-name='microfrontend_2']");
        assertThat(mfe2.locator("#version")).hasText("Version 1");

        mfe1.locator("#reload").click();

        // New content can only come from re-fetching the fragment
        assertThat(mfe2.locator("#version")).hasText("Version 2");
        assertThat(mfe2.locator("#version")).hasCount(1);
        assertEquals(List.of(), pageErrors);
    }
}

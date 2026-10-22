package htmlflow.test.e2e;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

/**
 * MFE script loaded through {@code setMfeScriptUrl} with Subresource Integrity ({@code setMfeScriptIntegrity}).
 * The script (mfe-events.js) wires microfrontend_1's button to add an item to microfrontend_2,
 * so the click outcome shows whether the browser executed it or blocked it.
 */
public class MfeScriptIntegrityIT extends MfeE2EBase {

    private static final String EVENT_NAME = "itemAdded";
    private static final String SCRIPT_URL = TEST_JS_ROUTE + "mfe-events.js";
    private static final String SCRIPT_SELECTOR = "head script[src='" + SCRIPT_URL + "']";

    private static final String VALID_INTEGRITY = sha384(readResource("e2e/mfe-events.js"));
    private static final String INVALID_INTEGRITY = sha384("tampered".getBytes(StandardCharsets.UTF_8));

    private static final HtmlView<?> VALID_HOST_PAGE = hostPage(VALID_INTEGRITY);
    private static final HtmlView<?> INVALID_HOST_PAGE = hostPage(INVALID_INTEGRITY);

    private static MfeTestServer server;

    private static HtmlView<?> hostPage(String integrity) {
        return HtmlFlow.ViewFactory.builder()
            .mfeEnabled(true)
            .preEncoding(true)
            .build()
            .view(page -> page
                .html()
                    .head()
                        .script().attrType(EnumTypeScriptType.MODULE).attrSrc(ENGINE_URL).__()
                    .__()
                    .body()
                        .div()
                            .mfe(cfg -> {
                                cfg.setMfeUrlResource("/fragments/microfrontend_1");
                                cfg.setMfeName("microfrontend_1");
                                cfg.setMfeTriggersEventName(EVENT_NAME);
                                cfg.setMfeScriptUrl(SCRIPT_URL);
                                cfg.setMfeScriptIntegrity(integrity);
                            })
                            .mfe(cfg -> {
                                cfg.setMfeUrlResource("/fragments/microfrontend_2");
                                cfg.setMfeName("microfrontend_2");
                                cfg.setMfeListeningEventName(EVENT_NAME);
                            })
                        .__()
                    .__()
                .__());
    }

    @BeforeClass
    public static void startServer() throws Exception {
        server = new MfeTestServer()
            .html("/valid", VALID_HOST_PAGE::render)
            .html("/invalid", INVALID_HOST_PAGE::render)
            .html("/fragments/microfrontend_1", () -> "<button id=\"send\">Send item</button>")
            .html("/fragments/microfrontend_2", () -> "<ul id=\"items\"></ul>")
            .start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    @Test
    public void shouldExecuteMfeScriptWhenIntegrityMatches() {
        List<String> integrityErrors = new ArrayList<>();
        page.onConsoleMessage(msg -> {
            if (isIntegrityError(msg)) integrityErrors.add(msg.text());
        });

        page.navigate(server.url("/valid"));

        Locator script = page.locator(SCRIPT_SELECTOR);
        assertThat(script).hasAttribute("integrity", VALID_INTEGRITY);
        assertThat(script).hasAttribute("crossorigin", "anonymous");

        Locator mfe1 = page.locator("micro-frontend[mfe-name='microfrontend_1']");
        Locator mfe2 = page.locator("micro-frontend[mfe-name='microfrontend_2']");
        assertThat(mfe2.locator("#items")).isAttached();

        // Only the integrity-checked script wires this click
        mfe1.locator("#send").click();

        Locator items = mfe2.locator("#items li");
        assertThat(items).hasCount(1);
        assertThat(items.first()).hasText("test 1");
        assertEquals(List.of(), integrityErrors);
        assertEquals(List.of(), pageErrors);
    }

    @Test
    public void shouldBlockMfeScriptWhenIntegrityDoesNotMatch() {
        // Chromium downloads the script but refuses to run it, reporting it only on the console
        ConsoleMessage blocked = page.waitForConsoleMessage(
            new Page.WaitForConsoleMessageOptions().setPredicate(MfeScriptIntegrityIT::isIntegrityError),
            () -> page.navigate(server.url("/invalid")));

        assertThat(page.locator(SCRIPT_SELECTOR)).hasAttribute("integrity", INVALID_INTEGRITY);
        assertThat(page.locator(SCRIPT_SELECTOR)).hasAttribute("crossorigin", "anonymous");

        // Fragments still render: the engine itself is not affected
        Locator mfe1 = page.locator("micro-frontend[mfe-name='microfrontend_1']");
        Locator mfe2 = page.locator("micro-frontend[mfe-name='microfrontend_2']");
        assertThat(mfe1.locator("#send")).isVisible();
        assertThat(mfe2.locator("#items")).isAttached();

        mfe1.locator("#send").click();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(mfe2.locator("#items li")).hasCount(0);
        assertTrue(blocked.text(), blocked.text().contains("mfe-events.js"));
        assertEquals(List.of(), pageErrors);
    }

    private static boolean isIntegrityError(ConsoleMessage msg) {
        return "error".equals(msg.type()) && msg.text().contains("integrity");
    }

    private static byte[] readResource(String resource) {
        try (InputStream in = MfeScriptIntegrityIT.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) throw new IllegalStateException("Missing test resource: " + resource);
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String sha384(byte[] content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-384").digest(content);
            return "sha384-" + Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

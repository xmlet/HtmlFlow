package htmlflow.test.e2e;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

/**
 * Two MFEs sharing one scoped stylesheet (mfe-shared-styling-url) plus their own (mfe-styling-url).
 * The shared sheet is also adopted onto the host document, so the host shell can use the same theme.
 */
public class MfeSharedStylingIT extends MfeE2EBase {

    private static final String SHARED_CSS = TEST_JS_ROUTE + "shared.css";
    private static final String SHARED_BG = "rgb(255, 99, 71)";

    private static final HtmlView<?> HOST_PAGE = HtmlFlow.ViewFactory.builder()
        .mfeEnabled(true)
        .preEncoding(true)
        .build()
        .view(page -> page
            .html()
                .head()
                    .script().attrType(EnumTypeScriptType.MODULE).attrSrc(ENGINE_URL).__()
                .__()
                .body()
                    // Same class as in the MFEs, but outside any .mfe-shared scope
                    .button().attrClass("btn").attrId("host-btn").text("Host button").__()
                    // Host shell opting into the shared theme by using the scope class
                    .div().attrClass("mfe-shared")
                        .button().attrClass("btn").attrId("host-themed-btn").text("Themed host button").__()
                    .__()
                    .div()
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragments/microfrontend_1");
                            cfg.setMfeName("microfrontend_1");
                            cfg.setMfeSharedStylingUrl(SHARED_CSS);
                            cfg.setMfeStylingUrl(TEST_JS_ROUTE + "microfrontend_1.css");
                        })
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragments/microfrontend_2");
                            cfg.setMfeName("microfrontend_2");
                            cfg.setMfeSharedStylingUrl(SHARED_CSS);
                            cfg.setMfeStylingUrl(TEST_JS_ROUTE + "microfrontend_2.css");
                        })
                    .__()
                .__()
            .__());

    private static MfeTestServer server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new MfeTestServer()
            .html("/", HOST_PAGE::render)
            .html("/fragments/microfrontend_1", () -> fragment(1))
            .html("/fragments/microfrontend_2", () -> fragment(2))
            .start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    private static String fragment(int n) {
        return "<div class=\"mfe-shared\">"
            + "<p class=\"title\">MFE " + n + "</p>"
            + "<button class=\"btn\" id=\"btn\">Button " + n + "</button>"
            + "</div>";
    }

    @Test
    public void shouldApplySharedScopedStylingToBothMicrofrontends() {
        List<String> sharedCssRequests = new ArrayList<>();
        page.onRequest(req -> {
            if (req.url().endsWith(SHARED_CSS)) sharedCssRequests.add(req.url());
        });

        page.navigate(server.url("/"));

        Locator mfe1 = page.locator("micro-frontend[mfe-name='microfrontend_1']");
        Locator mfe2 = page.locator("micro-frontend[mfe-name='microfrontend_2']");

        // Shared reusable button class applied in both MFEs
        for (Locator mfe : List.of(mfe1, mfe2)) {
            Locator btn = mfe.locator("#btn");
            assertThat(btn).hasCSS("background-color", SHARED_BG);
            assertThat(btn).hasCSS("border-radius", "8px");
            assertThat(btn).hasCSS("padding", "8px 16px");
        }

        // Own stylesheets apply only to their own MFE
        assertThat(mfe1.locator(".title")).hasCSS("color", "rgb(0, 128, 0)");
        assertThat(mfe2.locator(".title")).hasCSS("color", "rgb(0, 0, 255)");

        // Shared sheet is also adopted onto document, but @scope keeps it off the host button
        page.waitForFunction("() => document.adoptedStyleSheets.length === 1");
        assertThat(page.locator("#host-btn")).not().hasCSS("background-color", SHARED_BG);
        assertThat(page.locator("#host-btn")).not().hasCSS("border-radius", Pattern.compile("^8px$"));

        page.waitForLoadState(LoadState.NETWORKIDLE); //wait until all network activity has stopped
        assertEquals("shared.css should be fetched once and cached: " + sharedCssRequests, 1, sharedCssRequests.size());
        assertEquals(List.of(), pageErrors);
    }

    @Test
    public void shouldApplySharedThemeToHostShell() {
        page.navigate(server.url("/"));

        // Wait until an MFE has loaded the shared sheet and adopted it onto document
        assertThat(page.locator("micro-frontend[mfe-name='microfrontend_1'] #btn")).hasCSS("background-color", SHARED_BG);
        page.waitForFunction("() => document.adoptedStyleSheets.length === 1");

        // Host element inside the .mfe-shared scope gets the shared theme
        Locator themed = page.locator("#host-themed-btn");
        assertThat(themed).hasCSS("background-color", SHARED_BG);
        assertThat(themed).hasCSS("border-radius", "8px");
        assertThat(themed).hasCSS("padding", "8px 16px");

        // Host element outside the scope is untouched
        assertThat(page.locator("#host-btn")).not().hasCSS("background-color", SHARED_BG);
        assertEquals(List.of(), pageErrors);
    }
}

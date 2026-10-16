package htmlflow.test.e2e;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.microsoft.playwright.Locator;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

/** End-to-end test of the compiled htmlflow-mfe-engine (dist/) rendering HtmlFlow MFE views. */
public class MfeBasicIT extends MfeE2EBase {

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
                    .div()
                        .mfe(cfg -> {
                            cfg.setMfeUrlResource("/fragment");
                            cfg.setMfeName("hello");
                        })
                    .__()
                .__()
            .__());

    private static MfeTestServer server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new MfeTestServer()
            .html("/", HOST_PAGE::render)
            .html("/fragment", () -> "<p id=\"greeting\">Hello World</p>")
            .start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    @Test
    public void shouldRenderHelloWorldInsideMicroFrontend() {
        page.navigate(server.url("/"));

        // Playwright CSS locators pierce open shadow roots
        Locator mfe = page.locator("micro-frontend[mfe-name='hello']");
        assertThat(mfe.locator("#greeting")).hasText("Hello World");
        assertEquals(List.of(), pageErrors);
    }
}

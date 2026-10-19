package htmlflow.test.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Shared Playwright setup for MFE e2e tests.
 * <ul>
 *   <li>{@link #ENGINE_URL} is fulfilled from the compiled engine in {@code htmlflow-mfe-engine/dist}.</li>
 *   <li>{@code /e2e/<name>} is fulfilled from the test classpath resource {@code e2e/<name>} (JS or CSS).</li>
 * </ul>
 * Subclasses own their {@link MfeTestServer} (host page and fragments).
 */
public abstract class MfeE2EBase {

    protected static final String ENGINE_URL = "/engine/htmlflow-mfe-engine.js";
    protected static final String TEST_JS_ROUTE = "/e2e/";

    private static final Path ENGINE_FILE = Path.of(
        System.getProperty("mfe.engine.dist", "../htmlflow-mfe-engine/dist"),
        "htmlflow-mfe-engine.js"
    );

    private static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;
    protected final List<String> pageErrors = new ArrayList<>();

    @BeforeClass
    public static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @AfterClass
    public static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Before
    public void openPage() {
        context = browser.newContext();
        context.route("**" + ENGINE_URL, route -> route.fulfill(new Route.FulfillOptions()
            .setContentType("text/javascript")
            .setPath(ENGINE_FILE)));
        context.route("**" + TEST_JS_ROUTE + "*", MfeE2EBase::fulfillTestScript);
        page = context.newPage();
        page.onPageError(pageErrors::add);
    }

    @After
    public void closePage() {
        if (context != null) context.close();
    }

    private static void fulfillTestScript(Route route) {
        String path = URI.create(route.request().url()).getPath();
        String resource = "e2e/" + path.substring(path.lastIndexOf('/') + 1);
        try (InputStream in = MfeE2EBase.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                route.fulfill(new Route.FulfillOptions().setStatus(404).setBody("Not found: " + resource));
                return;
            }
            route.fulfill(new Route.FulfillOptions()
                .setContentType(resource.endsWith(".css") ? "text/css" : "text/javascript")
                .setBodyBytes(in.readAllBytes()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package htmlflow.test.e2e;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.microsoft.playwright.Locator;
import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

/**
 * PSSR: the MFE fragment is streamed. A chunk containing {@code <slot>} elements is the wrapper, a chunk
 * with a {@code slot="x"} element goes into {@code slot[name="x"]} and any other chunk is appended as is.
 */
public class MfeStreamingIT extends MfeE2EBase {

    private static final Duration CHUNK_DELAY = Duration.ofMillis(100);
    private static final String MFE = "micro-frontend[mfe-name='streaming']";

    private static final String WRAPPER_CHUNK = "<div class=\"item-list\"><ul id=\"items\">"
        + "<slot name=\"item-1\"></slot>"
        + "<slot name=\"item-2\"></slot>"
        + "<slot name=\"item-3\"></slot>"
        + "</ul></div>";

    private static final String SINGLE_SLOT_WRAPPER_CHUNK =
        "<div class=\"item-list\"><ul id=\"items\"><slot name=\"item-1\"></slot></ul></div>";

    private static MfeTestServer server;

    private final List<String> consoleErrors = new ArrayList<>();

    /** Host page with a single streaming MFE fetching {@code fragmentUrl}. */
    private static HtmlView<?> hostPage(String fragmentUrl) {
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
                                cfg.setMfeUrlResource(fragmentUrl);
                                cfg.setMfeName("streaming");
                                cfg.setMfeStreamingData(true);
                            })
                        .__()
                    .__()
                .__());
    }

    @BeforeClass
    public static void startServer() throws Exception {
        HtmlView<?> inOrder = hostPage("/fragments/items");
        HtmlView<?> outOfOrder = hostPage("/fragments/out-of-order");
        HtmlView<?> plain = hostPage("/fragments/plain");
        HtmlView<?> missingSlot = hostPage("/fragments/missing-slot");
        server = new MfeTestServer()
            .html("/", inOrder::render)
            .stream("/fragments/items", CHUNK_DELAY,
                WRAPPER_CHUNK, itemChunk(1), itemChunk(2), itemChunk(3))
            .html("/out-of-order", outOfOrder::render)
            .stream("/fragments/out-of-order", CHUNK_DELAY,
                WRAPPER_CHUNK, itemChunk(3), itemChunk(1), itemChunk(2))
            .html("/plain", plain::render)
            .stream("/fragments/plain", CHUNK_DELAY,
                lineChunk(1), lineChunk(2), lineChunk(3))
            .html("/missing-slot", missingSlot::render)
            .stream("/fragments/missing-slot", CHUNK_DELAY,
                SINGLE_SLOT_WRAPPER_CHUNK, itemChunk(1), itemChunk(9))
            .start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) server.stop();
    }

    @Before
    public void collectConsoleErrors() {
        page.onConsoleMessage(msg -> {
            if ("error".equals(msg.type())) consoleErrors.add(msg.text());
        });
    }

    private static String itemChunk(int n) {
        return "<li slot=\"item-" + n + "\" data-id=\"" + n + "\">Item " + n + "</li>";
    }

    private static String lineChunk(int n) {
        return "<p class=\"line\">Line " + n + "</p>";
    }

    private List<String> slotErrors() {
        return consoleErrors.stream().filter(text -> text.contains("<slot")).toList();
    }

    private void assertItemsInTheirSlots(Locator list, int... ids) {
        for (int n : ids) {
            Locator item = list.locator("slot[name='item-" + n + "'] > li");
            assertThat(item).hasCount(1);
            assertThat(item).hasText("Item " + n);
            assertThat(item).hasAttribute("data-id", String.valueOf(n));
        }
    }

    @Test
    public void shouldRenderStreamedItemsIntoTheirSlots() {
        page.navigate(server.url("/"));

        Locator mfe = page.locator(MFE);

        // Wrapper chunk
        Locator list = mfe.locator(".item-list > ul#items");
        assertThat(list).isAttached();
        assertThat(list.locator("> slot")).hasCount(3);

        // Item chunks, in order
        Locator items = list.locator("li");
        assertThat(items).hasCount(3);
        assertEquals(List.of("Item 1", "Item 2", "Item 3"), items.allTextContents());

        assertItemsInTheirSlots(list, 1, 2, 3);
        assertEquals(List.of(), pageErrors);
        assertEquals(List.of(), slotErrors());
    }

    @Test
    public void shouldPlaceOutOfOrderChunksIntoTheirSlots() {
        page.navigate(server.url("/out-of-order"));

        Locator list = page.locator(MFE).locator(".item-list > ul#items");

        // Arrival order is 3, 1, 2 but the slots fix the rendered order
        Locator items = list.locator("li");
        assertThat(items).hasCount(3);
        assertEquals(List.of("Item 1", "Item 2", "Item 3"), items.allTextContents());

        assertItemsInTheirSlots(list, 1, 2, 3);
        assertEquals(List.of(), pageErrors);
        assertEquals(List.of(), slotErrors());
    }

    @Test
    public void shouldAppendChunksWithoutSlotsInArrivalOrder() {
        page.navigate(server.url("/plain"));

        Locator mfe = page.locator(MFE);

        Locator lines = mfe.locator("p.line");
        assertThat(lines).hasCount(3);
        assertEquals(List.of("Line 1", "Line 2", "Line 3"), lines.allTextContents());

        // No wrapper involved and the loading placeholder was cleared
        assertThat(mfe.locator("slot")).hasCount(0);
        assertThat(mfe).not().containsText("Loading");
        assertEquals(List.of(), pageErrors);
        assertEquals(List.of(), slotErrors());
    }

    @Test
    public void shouldReportChunkTargetingMissingSlot() {
        page.navigate(server.url("/missing-slot"));

        Locator mfe = page.locator(MFE);
        Locator list = mfe.locator(".item-list > ul#items");

        assertItemsInTheirSlots(list, 1);

        // item-9 has no matching slot: it is reported and not rendered
        page.waitForCondition(() -> !slotErrors().isEmpty());
        assertEquals(1, slotErrors().size());
        assertTrue(slotErrors().get(0), slotErrors().get(0).contains("No <slot name=\"item-9\">"));
        assertThat(mfe.locator("li")).hasCount(1);
        assertThat(mfe).not().containsText("Item 9");
        assertEquals(List.of(), pageErrors);
    }
}

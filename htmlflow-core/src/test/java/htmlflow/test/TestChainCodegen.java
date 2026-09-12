package htmlflow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.codegen.ChainCompiler.Renderer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * A compiled chain emits its page in two ways. With a StringBuilder it uses the class that the
 * ChainCompiler generated, and with another Appendable it uses the linked chain. Both have to
 * produce the same HTML. The template below uses all the kinds of slot, so that we exercise
 * every branch of the generator.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestChainCodegen {

    @Test
    void slots_render_as_generated_bytecode() throws Exception {
        assertTrue(
            isCompiled(),
            "no chain compiled, so the tests below would prove nothing"
        );
    }

    @Test
    void the_generated_class_and_the_linked_chain_agree() {
        assertEquals(renderInterpreted(), render());
    }

    /**
     * All the threads share the same Renderer. Its fields are final and it only ever appends to
     * the StringBuilder it's handed, so sharing it across threads is safe.
     */
    @Test
    void a_thread_safe_view_renders_the_same_page_from_every_thread()
        throws InterruptedException {
        String expected = render();
        HtmlView<List<Row>> view = view().threadSafe();
        List<String> outputs = Collections.synchronizedList(new ArrayList<>());
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            for (int i = 0; i < 64; i++) {
                pool.execute(() -> outputs.add(view.render(rows())));
            }
        } finally {
            pool.shutdown();
            assertTrue(
                pool.awaitTermination(30, TimeUnit.SECONDS),
                "renders did not finish"
            );
        }
        assertEquals(64, outputs.size());
        for (String out : outputs) assertEquals(expected, out);
    }
    /** A model with one property for each kind of slot, which we use inside a loop. */
    private static final class Row {

        final int i;
        final long l;
        final double d;
        final boolean b;
        final String text;
        final String raw;
        final String cls;
        final String note;

        Row(int i, String text, String note) {
            this.i = i;
            this.l = i * 1_000_000_000L;
            this.d = i + 0.5;
            this.b = i % 2 == 0;
            this.text = text;
            this.raw = "<i>" + i + "</i>";
            this.cls = i % 2 == 0 ? "even" : "odd";
            this.note = note;
        }
    }

    private static List<Row> rows() {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row(1, "a & b", "first"));
        rows.add(new Row(2, "<script>", null));
        rows.add(new Row(3, "plain", "third"));
        return rows;
    }

    private static void template(HtmlPage page) {
        page
            .html()
            .body()
            .table()
            .tbody()
            .forEachOf((List<Row> rows) -> rows, (tbody, row) ->
                tbody
                    .tr()
                    .attrOf("class", row.read(r -> r.cls))
                    .attrOfNullable("data-note", row.read(r -> r.note))
                    .td()
                    .intOf(row.readInt(r -> r.i))
                    .__()
                    .td()
                    .longOf(row.readLong(r -> r.l))
                    .__()
                    .td()
                    .doubleOf(row.readDouble(r -> r.d))
                    .__()
                    .td()
                    .boolOf(row.readBool(r -> r.b))
                    .__()
                    .td()
                    .textOf(row.read(r -> r.text))
                    .__()
                    .td()
                    .rawOf(row.read(r -> r.raw))
                    .__()
                    .__()
            )
            .__()
            .__()
            .__()
            .__();
    }

    private static HtmlView<List<Row>> view() {
        return HtmlFlow.<List<Row>>view(TestChainCodegen::template);
    }

    private static String render() {
        return view().render(rows());
    }

    /**
     * Renders the same view through an Appendable that isn't a StringBuilder, so it runs the
     * linked chain instead of the generated class.
     */
    private static String renderInterpreted() {
        return Utils.renderThroughLinkedChain(
            TestChainCodegen::template,
            rows()
        );
    }

    private static boolean isCompiled() throws Exception {
        HtmlView<List<Row>> view = view();
        view.render(rows());
        Object visitor = view.getVisitor();
        HtmlContinuation node = (HtmlContinuation) visitor
            .getClass()
            .getField("first")
            .get(visitor);
        for (; node != null; node = node.getNext()) {
            if (rendererUnder(node, 4)) return true;
        }
        return false;
    }

    /**
     * Looks for a Renderer field in the nodes below the target. We search instead of following a
     * fixed path because the place where a chain hangs may change.
     */
    private static boolean rendererUnder(Object target, int depth) throws Exception {
        if (target == null || depth == 0) return false;
        if (!target.getClass().getName().startsWith("htmlflow.")) return false;
        for (Field f : target.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) || f.getType().isPrimitive()) continue;
            f.setAccessible(true);
            Object value = f.get(target);
            if (value instanceof Renderer) return true;
            if (rendererUnder(value, depth - 1)) return true;
        }
        return false;
    }
}

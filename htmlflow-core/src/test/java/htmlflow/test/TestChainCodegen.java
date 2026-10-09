package htmlflow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.codegen.ChainCompiler.Renderer;
import htmlflow.exceptions.HtmlFlowAppendException;
import java.io.IOException;
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
 * The generated class has two methods, render for a StringBuilder and write for any other
 * Appendable. Each must produce the HTML of the equivalent dynamic() template. The template uses
 * every kind of slot, so every branch of the generator runs for both methods.
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
    void render_into_a_StringBuilder_matches_the_dynamic_block() {
        assertEquals(dynamicReference(), render());
    }

    @Test
    void write_into_another_Appendable_matches_the_dynamic_block() {
        assertEquals(
            dynamicReference(),
            Utils.renderToAppendable(TestChainCodegen::template, rows())
        );
    }

    /**
     * Only the loop body compiles, so the writer fails on the first row. A writer that failed on
     * the first append would throw from the linked chain and miss the generated write method.
     */
    @Test
    void an_IOException_from_write_surfaces_as_HtmlFlowAppendException() {
        HtmlView<List<Row>> view = view();
        view.setOut(new FailsOnRow());
        assertThrows(HtmlFlowAppendException.class, () -> view.write(rows()));
    }

    private static final class FailsOnRow implements Appendable {

        private final StringBuilder sb = new StringBuilder();

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            if (csq.toString().contains("<tr")) throw new IOException("closed");
            sb.append(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end)
            throws IOException {
            return append(csq.subSequence(start, end));
        }

        @Override
        public Appendable append(char c) {
            sb.append(c);
            return this;
        }
    }

    /**
     * Every thread shares one Renderer. Its fields are final and it only appends to the
     * StringBuilder it gets, so sharing is safe.
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
    /** One property per kind of slot, used inside a loop. */
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

    /** The same page written with a dynamic() block, which runs no generated code. */
    private static String dynamicReference() {
        return HtmlFlow
            .<List<Row>>view(TestChainCodegen::dynamicTemplate)
            .render(rows());
    }

    private static void dynamicTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .table()
            .tbody()
            .<List<Row>>dynamic((tbody, rows) -> {
                for (Row r : rows) {
                    var tr = tbody.tr().attrClass(r.cls);
                    if (r.note != null) tr.addAttr("data-note", r.note);
                    tr
                        .td()
                        .text(r.i)
                        .__()
                        .td()
                        .text(r.l)
                        .__()
                        .td()
                        .text(r.d)
                        .__()
                        .td()
                        .text(r.b)
                        .__()
                        .td()
                        .text(r.text)
                        .__()
                        .td()
                        .raw(r.raw)
                        .__()
                        .__();
                }
            })
            .__()
            .__()
            .__()
            .__();
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
     * Searches the nodes below target for a Renderer field. Searching survives changes to where
     * the compiled chain hangs.
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

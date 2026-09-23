package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.Student;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Value slots and forEachOf in an async view. Neither has async-specific code. Async
 * preprocessing extends the sync one, and slot and loop nodes are plain sync continuations that
 * the chain hands over at an await node. These tests fail if that stops being true.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestValueSlotsAsync {

    private static List<Student> students() {
        List<Student> l = new ArrayList<>();
        l.add(new Student(1, "Mary"));
        l.add(new Student(2, "Bob"));
        l.add(new Student(3, "Ana"));
        return l;
    }

    /** The rows with a dynamic block, the form that already worked. */
    private static void dynamicTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .table()
            .tbody()
            .<List<Student>>dynamic((tbody, students) -> {
                for (Student s : students) {
                    tbody
                        .tr()
                        .td()
                        .text(s.getNr())
                        .__()
                        .td()
                        .text(s.getName())
                        .__()
                        .__();
                }
            })
            .__()
            .__()
            .__()
            .__();
    }

    /** The same rows with value slots in a forEachOf. */
    private static void slotTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .table()
            .tbody()
            .forEachOf((List<Student> students) -> students, tbody ->
                tbody
                    .tr()
                    .td()
                    .longOf(Student::getNr)
                    .__()
                    .td()
                    .textOf(Student::getName)
                    .__()
                    .__()
            )
            .__()
            .__()
            .__()
            .__();
    }

    @Test
    void value_slots_render_the_same_bytes_on_a_synchronous_view() {
        List<Student> model = students();
        String expected = HtmlFlow
            .<List<Student>>view(TestValueSlotsAsync::dynamicTemplate)
            .render(model);
        String actual = HtmlFlow
            .<List<Student>>view(TestValueSlotsAsync::slotTemplate)
            .render(model);
        assertEquals(expected, actual);
    }

    @Test
    void value_slots_render_the_same_bytes_on_an_asynchronous_view()
        throws ExecutionException, InterruptedException {
        List<Student> model = students();

        String expected = HtmlFlow
            .<List<Student>>view(TestValueSlotsAsync::dynamicTemplate)
            .render(model);

        HtmlViewAsync<List<Student>> asyncView = HtmlFlow.viewAsync(
            TestValueSlotsAsync::slotTemplate
        );
        String actual = asyncView.renderAsync(model).get();

        assertEquals(expected, actual);
    }

    /** Slot loops before and after an await, so the chain hands over and comes back. */
    @Test
    void value_slots_survive_an_await_in_the_middle_of_the_chain()
        throws ExecutionException, InterruptedException {
        List<Student> model = students();

        HtmlViewAsync<List<Student>> view = HtmlFlow.viewAsync(page ->
            page
                .html()
                .body()
                .table()
                .tbody()
                .forEachOf((List<Student> s) -> s, tbody ->
                    tbody.tr().td().longOf(Student::getNr).__().__()
                )
                .__()
                .__()
                .<List<Student>>await((body, students, cb) ->
                    Flux
                        .fromIterable(students)
                        .doOnComplete(cb::finish)
                        .subscribe(s -> body.p().text(s.getName()).__())
                )
                .__()
                .__()
        );

        String out = view.renderAsync(model).get();
        // Both sides of the await, in order.
        int firstRow = out.indexOf("<td>");
        int firstPara = out.indexOf("<p>");
        assertTrue(firstRow > 0, "slot loop before the await emitted nothing");
        assertTrue(firstPara > firstRow, "await output must follow the slot loop");
        assertTrue(out.contains("Mary"), "await output missing");
    }
}

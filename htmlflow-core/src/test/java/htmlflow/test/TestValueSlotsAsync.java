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
 * Value slots and forEachOf in an asynchronous view. Neither of them has code for the async
 * path. The async preprocessing extends the synchronous one, and the slot and loop nodes are
 * common synchronous continuations, which the chain hands over when it reaches an await node.
 * If that is not true, then these tests fail.
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

    /** Writes the rows with a dynamic block, which is the form that already worked. */
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

    /** Writes the same rows with value slots inside a forEachOf. */
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

    /** A loop of slots before and after an await, so that the chain hands over and returns. */
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
        // The output of both sides of the await has to be present and in this order.
        int firstRow = out.indexOf("<td>");
        int firstPara = out.indexOf("<p>");
        assertTrue(firstRow > 0, "slot loop before the await emitted nothing");
        assertTrue(firstPara > firstRow, "await output must follow the slot loop");
        assertTrue(out.contains("Mary"), "await output missing");
    }
}

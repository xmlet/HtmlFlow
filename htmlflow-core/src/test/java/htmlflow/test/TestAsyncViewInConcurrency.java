package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.AsyncModel;
import htmlflow.test.model.Student;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Iterator;
import java.util.stream.IntStream;

import static htmlflow.test.TestAsyncView.randomNameGenerator;
import static java.lang.Math.toIntExact;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestAsyncViewInConcurrency {
    static final int NR_OF_TASKS = Runtime.getRuntime().availableProcessors();
    static final Publisher<Student> studentFlux = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(10))
            .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
    static final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});

    @Test
    void check_asyncview_processing_in_sequential_tasks_and_unsafe_view() {
        /**
         * Arrange View
         */
        final HtmlViewAsync<AsyncModel> view = HtmlFlow.<AsyncModel>viewAsync(TestAsyncView::testAsyncModel).threadUnsafe();
        /**
         * Act and Assert
         * Since Stream is Lazy then there is a vertical processing and a sequential execution between tasks.
         */
        IntStream
                .range(0, NR_OF_TASKS)
                .mapToObj(i -> view.renderAsync(new AsyncModel<>(titlesFlux, studentFlux)))
                .forEach(cf ->assertHtml(cf.join()));
    }
    @Test
    void check_asyncview_processing_in_concurrent_tasks_and_parallel_threads() {
        /**
         * Arrange View
         */
        final HtmlViewAsync<AsyncModel> view = HtmlFlow.<AsyncModel>viewAsync(TestAsyncView::testAsyncModel).threadSafe();
        /**
         * Act and Assert
         * Collects to dispatch resolution through writeAsync() concurrently.
         */
        IntStream
                .range(0, NR_OF_TASKS)
                .parallel()
                .mapToObj(i -> view.renderAsync(new AsyncModel<>(titlesFlux, studentFlux)))
                .collect(toList())
                .forEach(cf ->assertHtml(cf.join()));
    }

    private static void assertHtml(String html) {
        Iterator<String> actual = Utils
                .NEWLINE
                .splitAsStream(html)
                .iterator();
        Utils
                .loadLines("asyncTest.html")
                .forEach(expected -> {
                    final String next = actual.next();
                    System.out.println(next);
                    assertEquals(expected, next);
                });
        assertFalse(actual.hasNext());
    }
}

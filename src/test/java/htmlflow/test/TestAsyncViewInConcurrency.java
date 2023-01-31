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
    @Test
    void check_asyncview_processing_in_concurrent_tasks() {
        check_asyncview_processing_concurrently(false);
    }
    @Test
    void check_asyncview_processing_in_concurrent_tasks_and_parallel_threads() {
        check_asyncview_processing_concurrently(true);
    }

    void check_asyncview_processing_concurrently(boolean parallel) {
        /**
         * Arrange Tasks
         */
        final int NR_OF_TASKS = Runtime.getRuntime().availableProcessors();
        final IntStream tasks = parallel
                ? IntStream.range(0, NR_OF_TASKS).parallel()
                : IntStream.range(0, NR_OF_TASKS);
        /**
         * Arrange the Model and the View
         */
        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});

        final HtmlViewAsync view = HtmlFlow.viewAsync(TestAsyncView::testAsyncModel);
        /**
         * Act and Assert
         */
        tasks
                .mapToObj(i -> view.renderAsync(new AsyncModel<>(titlesFlux, studentFlux)))
                .parallel()
                .collect(toList()) // Collects to dispatch resolution through writeAsync(().
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

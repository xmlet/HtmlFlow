package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.AsyncModel;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.Thenable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestAsyncView {
    
    @Test
    void given_async_work_when_create_view_then_returns_thenable_and_prints_correct_html() throws ExecutionException, InterruptedException {
    
        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10), Schedulers.single())
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
    

        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});
    

        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titlesFlux, studentFlux);
    
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        
        HtmlViewAsync<AsyncModel<String, Student>> view = HtmlFlow.viewAsync(new PrintStream(mem),
                TestAsyncView::testAsyncModel);
        
        final CompletableFuture<Void> writeAsync = view.writeAsync(asyncModel);
        
        writeAsync.get();
    
        Iterator<String> actual = Utils
                .NEWLINE
                .splitAsStream(mem.toString())
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
    
    static void testAsyncModel(HtmlView<AsyncModel<String, Student>> view, AsyncModel<String, Student> model) {
        final Thenable<Element> thenable = view.html()
                .body()
                .div()
                .p()
                .text("Students from a school board")
                .__()
                .__()
                .div()
                .table()
                .thead()
                .tr()
                .async(model.titles,
                        (tr, titlesObs) -> Flux
                            .from(titlesObs)
                            .doOnNext(nr -> tr.th().text(nr).__())
                            .subscribe())
                .then(tr -> tr.__().__().tbody())
                .async(model.items,
                        (tbody, studentObs) -> Flux
                            .from(studentObs)
                            .doOnNext(student -> tbody.tr()
                                    .th()
                                    .text(student.nr)
                                    .__()
                                    .td()
                                    .text(student.name)
                                    .__()
                                    .__())
                            .subscribe())
                .then(tr -> tr.__()
                        .__()
                        .__()
                        .div()
                        .p()
                        .text("Best students in school")
                        .__()
                        .__()
                        .__()
                        .__());
        
        assertNotNull(thenable);
    }
    
    private String randomNameGenerator(int nr) {
        String[] names = new String[]{"Pedro", "Manuel", "Maria", "Clara", "Rafael"};
        return names[nr - 1];
    }
    
    private static class Student {
        private final long nr;
        private final String name;
        
        private Student(long nr, String name) {
            this.nr = nr;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return String.format("Student nr " + nr + " with name " + name);
        }
    }
}

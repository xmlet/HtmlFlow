package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.AsyncModel;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestAsyncView {
    
    @Test
    void given_async_work_when_create_view_and_render_it_twice_correctly() throws ExecutionException, InterruptedException {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
    
        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
    
        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});
    
        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titlesFlux, studentFlux);
        
        HtmlViewAsync<AsyncModel<String, Student>> view = HtmlFlow.viewAsync(new PrintStream(mem),
                this::testAsyncModel, Publisher.class, AsyncModel.class);
        write_and_assert_asyncview(mem, view, asyncModel);
        mem.reset();
        write_and_assert_asyncview(mem, view, asyncModel);
    }
    
    @Test
    void given_async_work_when_create_view_then_returns_thenable_and_prints_correct_html() throws ExecutionException, InterruptedException {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
    
        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
    
        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});
    
        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titlesFlux, studentFlux);
        
        HtmlViewAsync<AsyncModel<String, Student>> view = HtmlFlow.viewAsync(new PrintStream(mem),
                this::testAsyncModel, AsyncModel.class, String.class, Student.class);
        write_and_assert_asyncview(mem, view, asyncModel);
    }
    
    void write_and_assert_asyncview(ByteArrayOutputStream mem, HtmlViewAsync<AsyncModel<String, Student>> view, AsyncModel<String, Student> asyncModel)
            throws ExecutionException, InterruptedException {
        
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
    
    @Test
    void given_async_work_with_double_delay_when_create_view_then_returns_thenable_and_prints_correct_html() throws ExecutionException,
            InterruptedException {
        
        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
        
        
        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"}).delayElements(Duration.ofSeconds(1));
        
        
        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titlesFlux, studentFlux);
        
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        
        HtmlViewAsync<AsyncModel<String, Student>> view = HtmlFlow.viewAsync(new PrintStream(mem),
                this::testAsyncModel, Publisher.class, AsyncModel.class);
        
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
    
    void testAsyncModel(HtmlPage<AsyncModel<String, Student>> view) {
        final HtmlPage<AsyncModel<String, Student>> thenable = view.html()
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
                .<AsyncModel<String, Student>, String>await(String.class, model -> model.titles,
                        (tr, titlesObs) -> Flux
                                .from(titlesObs)
                                .doOnNext(nr -> tr.th().text(nr).__())
                                .subscribe())
                .__().__().tbody()
                .<AsyncModel<String,Student>, Student>await(Student.class, model -> model.items,
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
                .__()
                        .__()
                        .__()
                        .div()
                        .p()
                        .text("Best students in school")
                        .__()
                        .__()
                        .__()
                        .__();
        
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

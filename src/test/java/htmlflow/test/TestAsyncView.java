package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.AsyncModel;
import htmlflow.test.model.Student;
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
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestAsyncView {
    
    @Test
    void given_async_work_when_create_view_and_render_it_twice_on_same_model() throws ExecutionException, InterruptedException {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
    
        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
    
        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});
    
        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titlesFlux, studentFlux);
        
        HtmlViewAsync view = HtmlFlow.viewAsync(TestAsyncView::testAsyncModel);
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel);
        mem.reset();
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel);
    }

    @Test
    void given_async_work_when_create_view_and_render_it_twice_on_other_model() throws ExecutionException, InterruptedException {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();

        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));

        final Publisher<String> titlesFlux = Flux.fromArray(new String[]{"Nr", "Name"});

        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titlesFlux, studentFlux);

        HtmlViewAsync view = HtmlFlow.viewAsync(TestAsyncView::testAsyncModel);
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel);
        //
        // 2nd render
        //
        final AsyncModel<String, Student> asyncModel2nd = new AsyncModel<>(titlesFlux, Flux.range(6, 4)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr)))));
        mem.reset();
        write_and_assert_asyncview("asyncTestSecond.html", mem, view, asyncModel2nd);
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
        
        HtmlViewAsync view = HtmlFlow.viewAsync(TestAsyncView::testAsyncModel);
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel);
    }

    void write_and_assert_asyncview(String expectedHtml, ByteArrayOutputStream mem,
                                    HtmlViewAsync view, AsyncModel<String, Student> asyncModel)
            throws ExecutionException, InterruptedException {

        final CompletableFuture<Void> writeAsync = view.writeAsync(new PrintStream(mem), asyncModel);

        writeAsync.get();

        Iterator<String> actual = Utils
                .NEWLINE
                .splitAsStream(mem.toString())
                .iterator();
        Utils
                .loadLines(expectedHtml)
                .forEach(expected -> {
                    final String next = actual.next();
                    // System.out.println(next);
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
        
        HtmlViewAsync view = HtmlFlow.viewAsync(TestAsyncView::testAsyncModel);
        
        final CompletableFuture<Void> writeAsync = view.writeAsync(new PrintStream(mem), asyncModel);
        
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
    
    public static void testAsyncModel(HtmlPage view) {
        final HtmlPage thenable = view.html()
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
                .<AsyncModel<String, Student>>await((tr, model, onCompletion) -> Flux
                                .from(model.titles)
                                .doOnComplete(onCompletion::finish)
                                .doOnNext(nr -> tr.th().text(nr).__())
                                .subscribe())
                .__().__().tbody()
                .<AsyncModel<String, Student>>await((tbody, model, onCompletion) -> Flux
                                .from(model.items)
                                .doOnComplete(onCompletion::finish)
                                .doOnNext(student -> tbody.tr()
                                        .th()
                                        .text(student.getNr())
                                        .__()
                                        .td()
                                        .text(student.getName())
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
    
    public static String randomNameGenerator(int nr) {
        String[] names = new String[]{
                "Pedro", "Manuel", "Maria", "Clara", "Rafael",
                "Ze", "Joan", "Gui", "Valery"
        };
        return names[nr - 1];
    }
}

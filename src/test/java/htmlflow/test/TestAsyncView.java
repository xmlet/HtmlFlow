package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import htmlflow.test.model.AsyncModel;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.Thenable;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestAsyncView {
    
    @Test
    void given_async_work_when_create_view_then_returns_thenable_and_prints_correct_html() throws ExecutionException, InterruptedException {
        
        Observable<String> titles = Observable
                .fromArray("Nr", "Name");
        
        Observable<Student> studentObservable = Observable
                .intervalRange(1, 5, 0, 10, TimeUnit.MILLISECONDS)
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));
        
        final AsyncModel<String, Student> asyncModel = new AsyncModel<>(titles, studentObservable);
    
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        
        HtmlView<AsyncModel<String, Student>> view = HtmlFlow.viewAsync(mem, TestAsyncView::testAsyncModel);
        
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
                        (tr, titlesObs) -> titlesObs.subscribe(nr -> tr.th().text(nr).__()))
                .then(tr -> tr.__().__().tbody())
                .async(model.items,
                        (tbody, studentObs) -> studentObs.subscribe(student -> tbody.tr()
                                .th()
                                .text(student.nr)
                                .__()
                                .td()
                                .text(student.name)
                                .__()
                                .__()))
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

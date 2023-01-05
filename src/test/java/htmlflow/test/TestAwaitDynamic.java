package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.AsyncDynamicModel;
import htmlflow.test.model.Student;
import htmlflow.test.model.StudentWithGrade;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Pedro Fialho
 **/

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TestAwaitDynamic {

    private final LinkedHashMap<String, Double> namesToGrade = new LinkedHashMap<>();

    @Before
    public void initMap() {
        namesToGrade.put("Pedro", 10.0);
        namesToGrade.put("Manuel", 12.2);
        namesToGrade.put("Maria", 13.0);
        namesToGrade.put("Clara", 14.0);
        namesToGrade.put("Rafael", 9.3);
    }

    @Test
    public void given_template_with_async_and_await_should_write_template_correctly() throws ExecutionException, InterruptedException {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();

        String[] titles = new String[] {"Nr", "Name"};
        String[] studentAndGradesTitles = new String[] {"Name", "Grade"};

        final Publisher<Student> studentFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(10))
                .doOnNext(nr -> System.out.println(" ########################## Emit " + nr))
                .map(nr -> new Student(nr, randomNameGenerator(toIntExact(nr))));

        Stream<StudentWithGrade> studentWithGradeStream = namesToGrade.entrySet()
                .stream()
                .map(entry -> new StudentWithGrade(entry.getKey(), entry.getValue()));

        AsyncDynamicModel model = new AsyncDynamicModel(titles, studentAndGradesTitles, studentFlux, studentWithGradeStream);

        HtmlViewAsync view = HtmlFlow.viewAsync(this::awaitAndDynamicTemplate);

        final CompletableFuture<Void> writeAsync = view.writeAsync(new PrintStream(mem), model);

        writeAsync.get();

        Iterator<String> actual = Utils
                .NEWLINE
                .splitAsStream(mem.toString())
                .iterator();
        Utils
                .loadLines("asyncTestWithDynamic.html")
                .forEach(expected -> {
                    final String next = actual.next();
//                     System.out.println(next);
                    assertEquals(expected, next);
                });
        assertFalse(actual.hasNext());
    }

    void awaitAndDynamicTemplate(HtmlPage view) {
        final HtmlPage thenable = view.html()
                .body()
                .div()
                .p()
                .text("Students from a school and their grades")
                .__()
                .__()
                .div()
                .table()
                .thead()
                .tr()
                .<AsyncDynamicModel>dynamic((tr, model) -> {
                    for (String schoolsTitle : model.getTitles()) {
                        tr.th().text(schoolsTitle).__();
                    }
                })
                .__().__().tbody()
                .<AsyncDynamicModel>await((tbody, model, onCompletion) -> Flux
                        .from(model.getStudents())
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
                .table()
                .thead()
                .tr()
                .<AsyncDynamicModel>dynamic((tr, model) -> {
                    for (String schoolsTitle : model.getStudentAndGradesTitles()) {
                        tr.th().text(schoolsTitle).__();
                    }
                })
                .__().__().tbody()
                .<AsyncDynamicModel>dynamic((tbody, model) -> model
                        .getWithGradeStream()
                        .forEach(studentWithGrade -> tbody.tr()
                        .th()
                        .text(studentWithGrade.getName())
                        .__()
                        .td()
                        .text(studentWithGrade.getGrade())
                        .__()
                        .__()))
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
        return namesToGrade.keySet()
                .toArray(new String[0])[nr - 1];
    }
}

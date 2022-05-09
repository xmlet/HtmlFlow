package htmlflow.test;

import htmlflow.DynamicHtml;
import htmlflow.test.model.AsyncModel;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.xmlet.htmlapifaster.Thead;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestAsyncView {
    
    @Test
    void given_async_work_when_create_view_then_returns_thenable_and_prints_correct_html() throws IOException {
        
        Observable<String> titles = Observable
                .fromArray("542", "22", "3333", "42", "541241");
        
        Observable<Long> items = Observable
                .intervalRange(1, 5, 0, 10, TimeUnit.MILLISECONDS);
        
        final AsyncModel<String, Long> asyncModel = new AsyncModel<>(titles, items);
        DynamicHtml<AsyncModel<String, Long>> view = DynamicHtml.view(TestAsyncView::testAsyncModel);
        
        Path path = Paths.get("asyncTest.html");
        
        // waits for completion
        titles.blockingSubscribe();
        items.blockingSubscribe();
        
        final String render = view.render(asyncModel);
        
        Files.write(path, render.getBytes(StandardCharsets.UTF_8));;
        Desktop.getDesktop().browse(path.toUri());
    }
    
    @Test
    void given_async_work_when_create_view_and_render_async_then_can_iterate_observable() {
    
        Observable<String> titles = Observable
                .fromArray("542", "22", "3333", "42", "541241");
    
        Observable<Long> items = Observable
                .intervalRange(1, 5, 0, 10, TimeUnit.MILLISECONDS);
        
        final AsyncModel<String, Long> asyncModel = new AsyncModel<>(titles, items);
        DynamicHtml<AsyncModel<String, Long>> view = DynamicHtml.view(TestAsyncView::testAsyncModel);
    
        Path path = Paths.get("asyncTest.html");
    
        List<byte[]> htmlBytes = new ArrayList<>();
        
        view.renderAsync(asyncModel)
                .subscribe(s -> htmlBytes.add(s.getBytes(StandardCharsets.UTF_8)), (th) -> {}, () -> {
            try {
                htmlBytes.forEach(bytes -> {
                    try {
                        Files.write(path, bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Desktop.getDesktop().browse(path.toUri());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        titles.blockingSubscribe();
        items.blockingSubscribe();
    }
    
    static void testAsyncModel(DynamicHtml<AsyncModel<String, Long>> view, AsyncModel<String, Long> model) {
        view.html()
                .head()
                .title()
                .text("This is test for the async")
                .__()
                .__()
                .body()
                .div()
                .p()
                .text("Creating table from reactive models")
                .__()
                .__()
                .div()
                .table()
                .thead()
                .async(model.items,
                        (thead) -> model.items.subscribe(nr -> thead.tr().text(nr).__()),
                        Thead::__)
                .then(table -> table.__().table().thead())
                .async(model.titles,
                        (thead) -> model.titles.subscribe(s -> thead.tr().text(s).__()),
                        Thead::__)
                .then(table -> table.__()
                        .__()
                        .div().p().text("This is after the tables")
                        .__()
                        .__()
                        .__()
                        .__());
    }
}

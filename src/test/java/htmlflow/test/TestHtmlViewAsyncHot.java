package htmlflow.test;

import htmlflow.*;
import htmlflow.visitor.HtmlViewVisitorAsync;
import htmlflow.visitor.HtmlViewVisitorAsyncHot;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestHtmlViewAsyncHot {

    public <M> HtmlViewAsync<M> viewAsyncHot(HtmlTemplate template) {
        return HtmlFlow.viewAsync(false, template);
    }

    @Test
    public void should_use_hot_visitor() {
        HtmlViewAsync<Object> view = viewAsyncHot(v -> {
            v.html()
                    .body()
                    .h1().text("Hello, Hot Reload!").__()
                    .__()
                    .__();
        });

        assertInstanceOf(HtmlViewAsync.class, view, "Should be HtmlViewAsync instance");
        assertEquals("HtmlViewAsyncHot", view.getName());

        Object visitor = view.getVisitor();
        assertInstanceOf(HtmlViewVisitorAsyncHot.class, visitor, "Should use HtmlViewVisitorAsyncHot");
    }

    @Test
    public void should_not_cache_state_between_renders() {
        AtomicInteger counter = new AtomicInteger(0);

        HtmlViewAsync<Object> view = viewAsyncHot(v -> {
            int count = counter.incrementAndGet();
            v.html()
                    .body()
                    .h1().text("Render count: " + count).__()
                    .__()
                    .__();
        });

        String result1 = view.renderAsync("test").join();
        String result2 = view.renderAsync("test").join();
        String result3 = view.renderAsync("test").join();

        assertTrue(result1.contains("Render count: 1"));
        assertTrue(result2.contains("Render count: 2"));
        assertTrue(result3.contains("Render count: 3"));
        assertNotEquals(result1, result2);
        assertNotEquals(result2, result3);
    }

    @Test
    public void should_use_cached_visitor_when_caching_enabled() {
        HtmlViewAsync<Object> view = HtmlFlow.viewAsync(v -> {
            v.html()
                    .body()
                    .h1().text("Hello, Cached!").__()
                    .__()
                    .__();
        });

        assertInstanceOf(HtmlViewAsync.class, view, "Should be HtmlViewAsync instance");
        assertEquals("HtmlViewAsync", view.getName());

        Object visitor = view.getVisitor();
        assertInstanceOf(HtmlViewVisitorAsync.class, visitor, "Should use HtmlViewVisitorAsync");
    }

    @Test
    public void should_show_difference_between_hot_and_cached() {
        AtomicInteger counter = new AtomicInteger(0);

        HtmlTemplate template = v -> {
            int count = counter.incrementAndGet();
            v.html()
                    .body()
                    .h1().text("Count: " + count).__()
                    .__()
                    .__();
        };

        HtmlViewAsync<Object> hotView = viewAsyncHot(template).setCaching(false);
        String hotResult1 = hotView.renderAsync("test").join();
        String hotResult2 = hotView.renderAsync("test").join();

        counter.set(0);

        HtmlViewAsync<Object> cachedView = hotView.setCaching(true);
        String cachedResult1 = cachedView.renderAsync("test").join();
        String cachedResult2 = cachedView.renderAsync("test").join();

        assertTrue(hotResult1.contains("Count: 1"));
        assertTrue(hotResult2.contains("Count: 2"));
        assertNotEquals(hotResult1, hotResult2);

        assertTrue(cachedResult1.contains("Count: 1"));
        assertTrue(cachedResult2.contains("Count: 1"));
        assertEquals(cachedResult1, cachedResult2);
    }

    @Test
    public void should_handle_dynamic_content_with_state_changes() {
        AtomicInteger renderCount = new AtomicInteger(0);

        HtmlViewAsync<Object> view = viewAsyncHot(v -> {
            int count = renderCount.incrementAndGet();
            v.html()
                    .body()
                    .h1().text("Dynamic Test").__()
                    .dynamic((h1, model) -> h1.p().text("Model: " + model + ", Render: " + count).__())
                    .__()
                    .__();
        });

        String result1 = view.renderAsync("First").join();
        String result2 = view.renderAsync("Second").join();

        assertTrue(result1.contains("Model: First, Render: 1"));
        assertTrue(result2.contains("Model: Second, Render: 2"));
        assertNotEquals(result1, result2);
    }

    @Test
    public void should_change_visitors_when_caching_changes() {
        HtmlViewAsync<Object> view = viewAsyncHot(v -> {
            v.html()
                    .body()
                    .h1().text("Visitor Change Test").__()
                    .__()
                    .__();
        });

        Object initialVisitor = view.getVisitor();
        assertInstanceOf(HtmlViewVisitorAsyncHot.class, initialVisitor, "Initial visitor should be HtmlViewVisitorAsyncHot");

        HtmlViewAsync<Object> view2 = view.setCaching(true);
        Object newVisitor = view2.getVisitor();

        assertInstanceOf(HtmlViewAsync.class, view2, "New view should be HtmlViewAsync");
        assertInstanceOf(HtmlViewVisitorAsync.class, newVisitor, "New visitor should be HtmlViewVisitorAsync");
    }

    @Test
    public void should_support_thread_safety() {
        HtmlViewAsync<Object> view = viewAsyncHot(v -> {
            v.html()
                    .body()
                    .h1().text("Thread Safety Test").__()
                    .__()
                    .__();
        });

        HtmlViewAsync<Object> threadSafeView = view.threadSafe();
        String result = threadSafeView.renderAsync("Thread Safe").join();
        assertTrue(result.contains("Thread Safety Test"), "Thread-safe view should render correctly");

        HtmlViewAsync<Object> threadUnsafeView = view.threadUnsafe();
        String unsafeResult = threadUnsafeView.renderAsync("Thread Unsafe").join();
        assertTrue(unsafeResult.contains("Thread Safety Test"), "Thread-unsafe view should render correctly");
    }
}
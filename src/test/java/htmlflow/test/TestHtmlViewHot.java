package htmlflow.test;

import htmlflow.*;
import htmlflow.visitor.HtmlViewVisitor;
import htmlflow.visitor.HtmlViewVisitorHot;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestHtmlViewHot {

    public <M> HtmlView<M> viewHot(HtmlTemplate template) {
        return HtmlFlow.view(false, template);
    }

    @Test
    public void should_use_hot_visitor() {
        HtmlView<Object> view = viewHot(v -> {
            v.html()
                    .body()
                    .h1().text("Hello, Hot Reload!").__()
                    .__()
                    .__();
        });

        assertInstanceOf(HtmlViewHot.class, view, "Should be HtmlViewHot instance");
        assertEquals("HtmlViewHot", view.getName());

        Object visitor = view.getVisitor();
        assertInstanceOf(HtmlViewVisitorHot.class, visitor, "Should use HtmlViewVisitorHot");
    }

    @Test
    public void should_not_cache_state_between_renders() {
        AtomicInteger counter = new AtomicInteger(0);

        HtmlView<Object> view = viewHot(v -> {
            int count = counter.incrementAndGet();
            v.html()
                    .body()
                    .h1().text("Render count: " + count).__()
                    .__()
                    .__();
        });

        String result1 = view.render("test");
        String result2 = view.render("test");
        String result3 = view.render("test");

        assertTrue(result1.contains("Render count: 1"));
        assertTrue(result2.contains("Render count: 2"));
        assertTrue(result3.contains("Render count: 3"));
        assertNotEquals(result1, result2);
        assertNotEquals(result2, result3);
    }

    @Test
    public void should_use_cached_visitor_when_caching_enabled() {
        HtmlView<Object> view = HtmlFlow.view(v -> {
            v.html()
                    .body()
                    .h1().text("Hello, Cached!").__()
                    .__()
                    .__();
        });

        assertFalse(view instanceof HtmlViewHot, "Should be HtmlView instance");
        assertEquals("HtmlView", view.getName());

        Object visitor = view.getVisitor();
        assertFalse(visitor instanceof HtmlViewVisitorHot, "Should use HtmlViewVisitor");
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

        HtmlView<Object> hotView = viewHot(template).setCaching(false);
        String hotResult1 = hotView.render("test");
        String hotResult2 = hotView.render("test");

        counter.set(0);

        HtmlView<Object> cachedView = hotView.setCaching(true);
        String cachedResult1 = cachedView.render("test");
        String cachedResult2 = cachedView.render("test");

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

        HtmlView<Object> view = viewHot(v -> {
            int count = renderCount.incrementAndGet();
            v.html()
                    .body()
                    .h1().text("Dynamic Test").__()
                    .dynamic((h1, model) -> h1.p().text("Model: " + model + ", Render: " + count).__())
                    .__()
                    .__();
        });

        String result1 = view.render("First");
        String result2 = view.render("Second");

        assertTrue(result1.contains("Model: First, Render: 1"));
        assertTrue(result2.contains("Model: Second, Render: 2"));
        assertNotEquals(result1, result2);
    }

    @Test
    public void should_change_visitors_when_caching_changes() {
        HtmlView<Object> view = viewHot(v -> {
            v.html()
                    .body()
                    .h1().text("Visitor Change Test").__()
                    .__()
                    .__();
        }).setCaching(false);

        Object initialVisitor = view.getVisitor();
        assertInstanceOf(HtmlViewVisitorHot.class, initialVisitor, "Initial visitor should be HtmlViewVisitorHot");

        HtmlView<Object> view2 = view.setCaching(true);
        Object newVisitor = view2.getVisitor();

        assertFalse(view2 instanceof HtmlViewHot, "New view should be HtmlView");
        assertInstanceOf(HtmlViewVisitor.class, newVisitor, "New visitor should be HtmlViewVisitor");
    }
}
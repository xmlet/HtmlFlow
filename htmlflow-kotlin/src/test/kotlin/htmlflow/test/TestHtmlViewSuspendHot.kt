package htmlflow.test

import htmlflow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.p
import java.util.concurrent.atomic.AtomicInteger

/**
 * Test for HtmlViewSuspendHot implementation state and behavior.
 *
 * @see TestSuspendableView
 */
class TestHtmlViewSuspendHot {

    fun <M> viewSuspendHot(
        template: HtmlPage.() -> Unit
    ): HtmlViewSuspend<M> {
        return viewSuspend(template, preEncoding = false, threadSafe = true, isIndented = true)
    }

    /**
     * Test that HtmlViewSuspendHot uses the correct visitor type.
     */
    @Test
    fun should_use_suspend_hot_visitor() {
        val view = viewSuspendHot<String> {
            html {
                body {
                    h1 { text("Hello, Hot Reload!") }
                }
            }
        }
        // Verify visitor type
        val visitor = view.visitor
        assertTrue(visitor is HtmlViewVisitorSuspendHot, "Should use HtmlViewVisitorSuspendHot")
    }

    /**
     * Test state isolation between renders - hot reload should not cache state.
     */
    @Test
    fun should_not_cache_state_between_renders() = runBlocking {
        val counter = AtomicInteger(0)
        
        val view = viewSuspendHot<String> {
            val count = counter.incrementAndGet()
            html {
                body {
                    h1 { text("Render count: $count") }
                }
            }
        }
        
        val result1 = view.render("test")
        val result2 = view.render("test") 
        val result3 = view.render("test")
        
        assertTrue(result1.contains("Render count: 1"))
        assertTrue(result2.contains("Render count: 2"))
        assertTrue(result3.contains("Render count: 3"))
        
        // Verify each render gets a fresh execution
        assertNotEquals(result1, result2)
        assertNotEquals(result2, result3)
    }

    /**
     * Test that regular viewSuspend with caching=true uses cached visitor.
     */
    @Test
    fun should_use_cached_visitor_when_caching_enabled() {
        val view = viewSuspend<String> {
            html {
                body {
                    h1 { text("Hello, Cached!") }
                }
            }
        }
        
        // Verify it's the cached version
        assertTrue(view !is HtmlViewSuspendHot, "Should be HtmlViewSuspend instance")
        assertEquals("HtmlViewSuspend", view.name)
        
        // Verify visitor type
        val visitor = view.visitor
        assertTrue(visitor is HtmlViewVisitorSuspend, "Should use HtmlViewVisitorSuspend")
    }

    /**
     * Test comparison between hot and cached views with state.
     */
    @Test
    fun should_show_difference_between_hot_and_cached() = runBlocking {
        val counter = AtomicInteger(0)
        
        val template: HtmlPage.() -> Unit = {
            val count = counter.incrementAndGet()
            html {
                body {
                    h1 { text("Count: $count") }
                }
            }
        }
        
        // Hot reload view - should increment counter on each render
        val hotView = viewSuspendHot<String>(template)
        val hotResult1 = hotView.render("test")
        val hotResult2 = hotView.render("test")
        
        // Reset counter for cached test
        counter.set(0)
        
        // Cached view - should increment counter only once during preprocessing
        val cachedView = viewSuspend<String>(template)
        val cachedResult1 = cachedView.render("test")
        val cachedResult2 = cachedView.render("test")
        
        // Hot reload results should be different (fresh execution each time)
        assertTrue(hotResult1.contains("Count: 1"))
        assertTrue(hotResult2.contains("Count: 2"))
        assertNotEquals(hotResult1, hotResult2)
        
        // Cached results should be the same (executed once during preprocessing)
        assertTrue(cachedResult1.contains("Count: 1"))
        assertTrue(cachedResult2.contains("Count: 1")) // Same count, cached
        assertEquals(cachedResult1, cachedResult2)
    }

    /**
     * Test simple dynamic content with hot reload.
     */
    @Test
    fun should_handle_dynamic_content_with_state_changes() = runBlocking {
        val renderCount = AtomicInteger(0)
        
        val view = viewSuspendHot<String> {
            val count = renderCount.incrementAndGet()
            html {
                body {
                    h1 { text("Dynamic Test") }
                    dyn { model: String ->
                        p { text("Model: $model, Render: $count") }
                    }
                }
            }
        }
        
        val result1 = view.render("First")
        val result2 = view.render("Second")
        
        assertTrue(result1.contains("Model: First, Render: 1"))
        assertTrue(result2.contains("Model: Second, Render: 2"))
        assertNotEquals(result1, result2)
    }

    @Test
    fun should_change_visitors_when_caching_changes() = runBlocking {
        val view = viewSuspendHot<String> {
            html {
                body {
                    h1 { text("Visitor Change Test") }
                }
            }
        }

        val initialVisitor = view.visitor
        assertTrue(initialVisitor is HtmlViewVisitorSuspendHot, "Initial visitor should be HtmlViewVisitorSuspendHot")

        val view2 = view.setPreEncoding(true)
        val newVisitor = view2.visitor

        assertTrue(view2 !is HtmlViewSuspendHot, "New view should be HtmlViewSuspend")
        assertTrue(newVisitor is HtmlViewVisitorSuspend, "New visitor should be HtmlViewVisitorSuspend")
    }
}

/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlTemplate;
import htmlflow.HtmlView;
import htmlflow.HtmlViewHot;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * Unit tests for Builder/Engine pattern functionality, including configuration chaining,
 * view creation with different settings, compile-time safety, and hot reload behavior.
 */
public class TestViewEngine {

    private static final HtmlTemplate SIMPLE_TEMPLATE = view -> view
            .html()
                .head()
                    .title().text("Test Page").__()
                .__()
                .body()
                    .h1().text("Hello World").__()
                    .p().text("Test content").__()
                .__()
            .__();

    private static final HtmlTemplate DYNAMIC_TEMPLATE = view -> view
            .html()
                .body()
                    .h1().text("Dynamic Content").__()
                    .div().dynamic((div, model) -> div.text("Model: " + model)).__()
                .__()
            .__();

    @Test
    public void testBuilderDefaultConfiguration() {
        HtmlFlow.Builder builder = HtmlFlow.builder();
        HtmlFlow.Engine engine = builder.build();
        HtmlView<String> view = engine.view(SIMPLE_TEMPLATE);
        
        assertNotNull("Builder should create an engine", engine);
        assertNotNull("Engine should create a view", view);
        
        String html = view.render();
        assertTrue("Default should be indented", html.contains("\n"));
        assertTrue("Should contain expected content", html.contains("Hello World"));
    }

    @Test
    public void testBuilderFluentChaining() {
        HtmlFlow.Engine engine = HtmlFlow.builder()
                .indented(false)
                .threadSafe(true)
                .caching(false)
                .build();
        
        HtmlView<String> view = engine.view(SIMPLE_TEMPLATE);
        assertNotNull("Fluent chaining should create a view", view);
        
        String html = view.render();
        assertFalse("Should not be indented", html.contains("\n  "));
    }

    @Test
    public void testBuilderToEnginePattern() {
        HtmlFlow.Builder builder = HtmlFlow.builder()
                .indented(false)
                .caching(true)
                .threadSafe(false);
        
        HtmlFlow.Engine engine = builder.build();
        
        HtmlView<String> view1 = engine.view(SIMPLE_TEMPLATE);
        HtmlView<String> view2 = engine.view(DYNAMIC_TEMPLATE);
        
        assertNotNull("Engine should create first view", view1);
        assertNotNull("Engine should create second view", view2);
        
        String html1 = view1.render();
        String html2 = view2.render("test");
        
        assertFalse("Both views should not be indented",
                html1.contains("\n  ") || html2.contains("\n  "));
    }

    @Test
    public void testBuilderIndentedConfiguration() {
        HtmlView<String> indentedView = HtmlFlow.builder()
                .indented(true)
                .build()
                .view(SIMPLE_TEMPLATE);
        
        String indentedHtml = indentedView.render();
        assertTrue("Indented view should contain newlines", indentedHtml.contains("\n"));
        
        HtmlView<String> compactView = HtmlFlow.builder()
                .indented(false)
                .build()
                .view(SIMPLE_TEMPLATE);
        
        String compactHtml = compactView.render();
        assertFalse("Compact view should not have indentation", compactHtml.contains("\n  "));
    }

    @Test
    public void testBuilderThreadSafeConfiguration() {
        HtmlView<String> threadSafeView = HtmlFlow.builder()
                .threadSafe(true)
                .build()
                .view(SIMPLE_TEMPLATE);
        
        assertNotNull("Thread-safe view should be created", threadSafeView);
        
        HtmlView<String> singleThreadView = HtmlFlow.builder()
                .threadSafe(false)
                .build()
                .view(SIMPLE_TEMPLATE);
        
        assertNotNull("Single-thread view should be created", singleThreadView);
    }

    @Test
    public void testBuilderCachingVsHotReload() {
        HtmlView<String> cachedView = HtmlFlow.builder()
                .caching(true)
                .build()
                .view(DYNAMIC_TEMPLATE);
        
        HtmlView<String> hotView = HtmlFlow.builder()
                .caching(false)
                .build()
                .view(DYNAMIC_TEMPLATE);
        
        assertNotNull("Cached view should be created", cachedView);
        assertNotNull("Hot reload view should be created", hotView);
        
        // Verify type of view created
        assertFalse("Cached view should NOT be HtmlViewHot", cachedView instanceof HtmlViewHot);
        assertTrue("Hot reload view should be HtmlViewHot", hotView instanceof HtmlViewHot);
        
        // Both should render the same content
        String cachedHtml = cachedView.render("cached");
        String hotHtml = hotView.render("hot");
        
        assertTrue("Cached view should contain model", cachedHtml.contains("Model: cached"));
        assertTrue("Hot view should contain model", hotHtml.contains("Model: hot"));
    }

    @Test
    public void testEngineWithAppendableOutput() {
        StringWriter writer = new StringWriter();
        
        HtmlView<String> view = HtmlFlow.builder()
                .indented(false)
                .build()
                .view(writer, SIMPLE_TEMPLATE);
        
        assertNotNull("View with appendable should be created", view);
        
        view.write("test");
        String output = writer.toString();
        
        assertTrue("Output should contain expected content", output.contains("Hello World"));
    }

    @Test
    public void testEngineReuseability() {
        HtmlFlow.Engine engine = HtmlFlow.builder()
                .indented(true)
                .threadSafe(false)
                .caching(true)
                .build();
        
        HtmlView<String> view1 = engine.view(SIMPLE_TEMPLATE);
        HtmlView<String> view2 = engine.view(DYNAMIC_TEMPLATE);
        
        assertNotNull("First view should be created", view1);
        assertNotNull("Second view should be created", view2);
        
        String html1 = view1.render();
        String html2 = view2.render("test");
        
        assertTrue("Both views should be indented", 
                html1.contains("\n") && html2.contains("\n"));
    }

    @Test
    public void testBuilderConfigurationCombinations() {
        boolean[] boolValues = {true, false};
        
        for (boolean indented : boolValues) {
            for (boolean threadSafe : boolValues) {
                for (boolean caching : boolValues) {
                    HtmlView<String> view = HtmlFlow.builder()
                            .indented(indented)
                            .threadSafe(threadSafe)
                            .caching(caching)
                            .build()
                            .view(SIMPLE_TEMPLATE);

                    assertNotNull(
                            String.format("View should be created with config: indented=%s, threadSafe=%s, caching=%s",
                                    indented, threadSafe, caching),
                            view);

                    String html = view.render();
                    assertFalse("HTML should not be empty", html.isEmpty());
                    assertTrue("HTML should contain expected content", html.contains("Hello World"));
                }
            }
        }
    }

    @Test
    public void testBuilderHotReloadBehavior() {
        HtmlView<String> hotView = HtmlFlow.builder()
                .caching(false)
                .build()
                .view(DYNAMIC_TEMPLATE);
        
        String result1 = hotView.render("first");
        String result2 = hotView.render("second");
        
        assertTrue("First render should contain first model", result1.contains("Model: first"));
        assertTrue("Second render should contain second model", result2.contains("Model: second"));
        assertNotEquals("Results should be different", result1, result2);
    }

    @Test
    public void testBuilderWithDifferentTemplates() {
        HtmlFlow.Engine engine = HtmlFlow.builder().indented(false).build();
        
        HtmlTemplate template1 = view -> view.html().body().h1().text("Template 1").__().__();
        HtmlTemplate template2 = view -> view.html().body().h2().text("Template 2").__().__();
        
        HtmlView<String> view1 = engine.view(template1);
        HtmlView<String> view2 = engine.view(template2);
        
        String html1 = view1.render();
        String html2 = view2.render();
        
        assertTrue("First view should contain h1", html1.contains("<h1>Template 1</h1>"));
        assertTrue("Second view should contain h2", html2.contains("<h2>Template 2</h2>"));
    }

    @Test
    public void testBuilderReuseAfterBuild() {
        HtmlFlow.Builder builder = HtmlFlow.builder().indented(true);
        
        HtmlFlow.Engine engine1 = builder.build();
        
        HtmlFlow.Engine engine2 = builder.indented(false).build();
        
        HtmlView<String> view1 = engine1.view(SIMPLE_TEMPLATE);
        HtmlView<String> view2 = engine2.view(SIMPLE_TEMPLATE);
        
        String html1 = view1.render();
        String html2 = view2.render();
        
        assertTrue("First engine should create indented view", html1.contains("\n"));
        assertFalse("Second engine should create compact view", html2.contains("\n  "));
    }

    @Test
    public void testBuilderErrorHandling() {
        HtmlFlow.Engine engine = HtmlFlow.builder().build();
        
        try {
            engine.view(null);
            fail("Should throw exception for null template");
        } catch (Exception e) {
        }
        
        try {
            engine.view(null, SIMPLE_TEMPLATE).render();
            fail("Should throw exception for null appendable");
        } catch (Exception e) {
        }
    }

    @Test
    public void testEngineImmutability() {
        HtmlFlow.Builder builder = HtmlFlow.builder()
                .indented(true)
                .threadSafe(false)
                .caching(true);
        
        HtmlFlow.Engine engine = builder.build();
        
        builder.indented(false).threadSafe(true).caching(false);
        
        HtmlView<String> view = engine.view(SIMPLE_TEMPLATE);
        String html = view.render();
        
        assertTrue("Engine should be immutable and retain original indented=true", html.contains("\n"));
        assertFalse("Engine should be immutable and NOT be affected by builder changes", 
                view instanceof HtmlViewHot);
    }

    @Test
    public void testMultipleEnginesFromSameBuilder() {
        HtmlFlow.Builder builder = HtmlFlow.builder().indented(false);
        
        HtmlFlow.Engine engine1 = builder.caching(true).build();
        HtmlFlow.Engine engine2 = builder.caching(false).build();
        HtmlFlow.Engine engine3 = builder.threadSafe(true).caching(true).build();
        
        HtmlView<String> view1 = engine1.view(SIMPLE_TEMPLATE);
        HtmlView<String> view2 = engine2.view(SIMPLE_TEMPLATE);
        HtmlView<String> view3 = engine3.view(SIMPLE_TEMPLATE);
        
        assertNotNull("First engine should create view", view1);
        assertNotNull("Second engine should create view", view2);
        assertNotNull("Third engine should create view", view3);
        
        assertFalse("First view should be cached (not HtmlViewHot)", view1 instanceof HtmlViewHot);
        assertTrue("Second view should be hot reload", view2 instanceof HtmlViewHot);
        assertFalse("Third view should be cached", view3 instanceof HtmlViewHot);
    }
}

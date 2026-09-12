/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * Value slots that we bind to the page and not inside a loop, compared with the dynamic() block
 * that they replace. A slot leaves the HTML around it in the neighbour static blocks, which
 * risks losing the whitespace that separates them.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestValueSlots {

    static final class Product {

        final String name;
        final String badge;
        final String cls;
        final int stock;
        final long sku;
        final double price;
        final boolean listed;

        Product(String name, String badge, int stock) {
            this.name = name;
            this.badge = badge;
            this.cls = stock > 0 ? "in-stock" : "out";
            this.stock = stock;
            this.sku = stock * 5_000_000_000L;
            this.price = stock + 0.25;
            this.listed = stock > 0;
        }
    }

    private static final Product IN_STOCK = new Product("Tea & Co", "new", 7);
    private static final Product SOLD_OUT = new Product("<b>Bold</b>", null, 0);

    private static void slotTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .div()
            .attrOf("class", (Product p) -> p.cls)
            .attrOfNullable("data-badge", (Product p) -> p.badge)
            .h1()
            .textOf((Product p) -> p.name)
            .__()
            .p()
            .rawOf((Product p) -> "<em>" + p.stock + "</em>")
            .__()
            .span()
            .intOf((Product p) -> p.stock)
            .__()
            .span()
            .longOf((Product p) -> p.sku)
            .__()
            .span()
            .doubleOf((Product p) -> p.price)
            .__()
            .span()
            .boolOf((Product p) -> p.listed)
            .__()
            .__()
            .__()
            .__();
    }

    private static void dynamicTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .<Product>dynamic((body, p) ->
                body
                    .div()
                    .attrClass(p.cls)
                    .h1()
                    .text(p.name)
                    .__()
                    .p()
                    .raw("<em>" + p.stock + "</em>")
                    .__()
                    .span()
                    .text(p.stock)
                    .__()
                    .span()
                    .text(p.sku)
                    .__()
                    .span()
                    .text(p.price)
                    .__()
                    .span()
                    .text(p.listed)
                    .__()
                    .__()
            )
            .__()
            .__();
    }

    private static String render(Product model) {
        HtmlView<Product> view = HtmlFlow.view(TestValueSlots::slotTemplate);
        return view.render(model);
    }

    /**
     * We use SOLD_OUT because it has no badge, so the nullable attribute is absent. The
     * dynamicTemplate has no way to reproduce that.
     */
    @Test
    void slots_match_the_dynamic_block_they_replace() {
        HtmlView<Product> dyn = HtmlFlow.view(TestValueSlots::dynamicTemplate);
        assertEquals(dyn.render(SOLD_OUT), render(SOLD_OUT));
    }

    @Test
    void a_nullable_attribute_is_written_only_when_present() {
        assertTrue(render(IN_STOCK).contains("data-badge=\"new\""));
        assertFalse(render(SOLD_OUT).contains("data-badge"));
    }

    @Test
    void text_is_escaped_and_raw_is_not() {
        String out = render(SOLD_OUT);
        assertTrue(out.contains("&lt;b&gt;Bold&lt;/b&gt;"));
        assertFalse(out.contains("<b>Bold</b>"));
        assertTrue(out.contains("<em>0</em>"));
    }
}

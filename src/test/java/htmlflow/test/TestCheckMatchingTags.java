package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import org.junit.Test;
import org.xmlet.htmlapifaster.Div;

public class TestCheckMatchingTags {
    @Test
    public void testCheckMatchingTagsOnContainer() {
        HtmlView<?> view = HtmlFlow.view(v -> v
                .html()
                    .body()
                        .div().of(div -> templateDiv(div))
                        .__() // div
                    .__() // body
                .__() // html
        );
        view.render();
    }

    @Test
    public void testCheckMatchingTagsOnPage() {
        HtmlView<?> view = HtmlFlow.view(TestCheckMatchingTags::templatePage);
        view.render();
    }

    public static void templateDiv(Div<?> container) {
        container
            .div()
                .h2()

                .__() //h2
                .div().attrClass("row")
                    .div().attrClass("col-md-12")
                        .__() //img MISSING opening image tag.
                    .__() //div
                .__() //div
            .__(); // div
    }
    public static void templatePage (HtmlPage page) {
        page
                .div()
                    .h2()

                    .__() //h2
                    .div().attrClass("row")
                        .div().attrClass("col-md-12")
                            .__() //img MISSING opening image tag.
                        .__() //div
                    .__() //div
                .__(); // div
    }
}

package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlDoc;

public class HtmlCustomElements {
    public static HtmlPage customElements = HtmlFlow
            .doc()
                .html()
                    .head()
                        .script()
                            .attrSrc("alert.js") // Link to alert-info definition
                            .attrDefer(true)
                        .__()
                    .__()
                .body()
                    .div()
                        .attrClass("container")
                        .p().text("Testing custom elements!").__()
                        .custom("alert-info") // alert-info should be stored in the new Element and accessible to the Visitor.
                            .addAttr("title", "Information")
                            .addAttr("message", "This is a message for a custom element")
                            .addAttr("kind", "success")
                            .ul()
                                .li().text("For any reason we could even include other elements.").__()
                            .__() // ul
                            .__() // alert-info
                        .__() // div
                    .__() // body
                .__(); //html
}

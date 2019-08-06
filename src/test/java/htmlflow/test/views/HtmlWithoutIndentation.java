package htmlflow.test.views;

import htmlflow.HtmlView;
import htmlflow.StaticHtml;

public class HtmlWithoutIndentation {

    public static HtmlView bodyDivP = StaticHtml
        .view()
            .setIndented(false)
            .html()
                .body()
                    .div()
                        .p()
                            .text("Some dummy text!")
                        .__() // p
                    .__() // div
                .__() // body
            .__(); // html

}

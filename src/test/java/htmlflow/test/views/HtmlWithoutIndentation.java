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

    public static HtmlView bodyPre = StaticHtml
        .view()
            .setIndented(false)
            .html()
                .body()
                    .pre()
                        .text("Some text")
                    .__() // pre
                .__() // body
            .__(); // html

}

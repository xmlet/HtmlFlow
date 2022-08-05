package htmlflow.test.views;

import htmlflow.AbstractHtmlWriter;
import htmlflow.StaticHtml;

import java.io.PrintStream;

public class HtmlWithoutIndentation {

    public static AbstractHtmlWriter bodyDivP = StaticHtml
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

    public static AbstractHtmlWriter bodyPre = StaticHtml
        .view()
            .setIndented(false)
            .html()
                .body()
                    .pre()
                        .text("Some text")
                    .__() // pre
                .__() // body
            .__(); // html

    public static AbstractHtmlWriter hotBodyDivP(PrintStream out) {
        return StaticHtml.view(out)
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
}

package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlDoc;

import java.io.PrintStream;

public class HtmlWithoutIndentation {

    public static HtmlPage bodyDivP = HtmlFlow
        .doc()
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

    public static HtmlPage bodyPre = HtmlFlow
        .doc()
            .setIndented(false)
            .html()
                .body()
                    .pre()
                        .text("Some text")
                    .__() // pre
                .__() // body
            .__(); // html

    public static HtmlPage hotBodyDivP(PrintStream out) {
        return HtmlFlow.doc(out)
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

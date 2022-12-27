package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;

import java.io.PrintStream;

public class HtmlWithoutIndentation {

    public static void bodyDivP(StringBuilder out) {
        HtmlFlow
            .doc(out)
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

    public static void bodyDivPtemplate(HtmlPage view) {
        view
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

    public static void bodyPre(StringBuilder out) {
        HtmlFlow
            .doc(out)
            .setIndented(false)
            .html()
                .body()
                    .pre()
                        .text("Some text")
                    .__() // pre
                .__() // body
            .__(); // html
    }

    public static void hotBodyDivP(PrintStream out) {
        HtmlFlow
            .doc(out)
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

package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlDoc;
import htmlflow.HtmlView;

import java.io.PrintStream;
import java.util.function.Consumer;

public class HtmlWithoutIndentation {

    public static Consumer<StringBuilder> bodyDivP = sb -> HtmlFlow.doc(sb)
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

    public static Consumer<StringBuilder> bodyPre = sb -> HtmlFlow.doc(sb)
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

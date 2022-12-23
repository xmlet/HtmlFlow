package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlDoc;
import htmlflow.HtmlView;

import java.io.PrintStream;

public class HtmlWithoutIndentation {

    public static HtmlView bodyDivP = HtmlFlow.view(view -> view
            .html()
                .body()
                    .div()
                        .p()
                            .text("Some dummy text!")
                        .__() // p
                    .__() // div
                .__() // body
            .__() // html
        )
        .setIndented(false);

    public static HtmlView bodyPre = HtmlFlow.view(view -> view
            .html()
                .body()
                    .pre()
                        .text("Some text")
                    .__() // pre
                .__() // body
            .__() // html
        )
        .setIndented(false);

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

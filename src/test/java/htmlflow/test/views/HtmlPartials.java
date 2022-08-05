package htmlflow.test.views;

import htmlflow.DynamicHtml;
import htmlflow.HtmlTemplate;
import htmlflow.AbstractHtmlWriter;
import htmlflow.StaticHtml;
import htmlflow.test.model.Track;
import org.junit.Test;

import java.util.stream.Stream;

/**
 * These tests do not contain any assertion because they are only a samplefor README.md.
 */
@SuppressWarnings("squid:S3577")
public class HtmlPartials {

    static AbstractHtmlWriter bbView = StaticHtml.view().div().text("Dummy bbView").__(); // div

    static AbstractHtmlWriter footerView(AbstractHtmlWriter banner) {
        StaticHtml view = StaticHtml.view();
        return view
                .div()
                    .of(__ -> view.addPartial(banner))
                    .p()
                        .text("Created with HtmFlow")
                    .__() // p
                .__(); //
    }


    /**
     * This unit test was only a sample for a paper.
     */
    @SuppressWarnings("squid:S2699")
    @Test
    public void testPartials() {
        HtmlTemplate<Stream<Track>> tracksTemplate = (view , tracks, partials) -> view
        .div()
            .ul()
                .of(ul -> tracks.forEach (item -> ul
                    .li()
                        .text(item. getName ())
                    .__() // li
                ))
            .__ () // ul
            .of(__ -> view.addPartial(partials[0]))
        .__(); // div


        Stream<Track> tracks = Stream.of(new Track("Space Odyssey"), new Track("Bad"), new Track("Under Pressure"));
        DynamicHtml<Stream<Track>> tracksView = DynamicHtml.view(tracksTemplate);
        String html = tracksView.render(tracks, footerView(bbView));
        System.out.println(html);
    }
}

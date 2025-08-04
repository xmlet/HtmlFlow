package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.test.model.Track;
import org.xmlet.htmlapifaster.EnumRelType;

import java.util.stream.Stream;

public class HtmlDynamicChainTwiceOnTopgenius {

    public static final HtmlView toptracksOkOfWithDynamic = HtmlFlow
        .view(HtmlDynamicChainTwiceOnTopgenius::toptracksTemplateOfAndDynamic);

    public static void toptracksTemplateDynamicTwice(HtmlPage view) {
        view
            .html()
                .head()
                    .link()
                        .attrRel(EnumRelType.STYLESHEET)
                        .attrHref("/stylesheets/bootstrap.min.css")
                    .__()
                    .title().text("TopGenius.eu").__()
                .__()
                .body()
                    .div()
                        .attrClass("container")
                        .table()
                            .attrClass("table")
                            .tr()
                                .th().text("Rank").__()
                                .th().text("Track").__()
                                .th().text("Listeners").__()
                            .__()
                            .<Stream<Track>>dynamic((table, tracks) -> {
                                int[] count = {1};
                                tracks.forEach(track ->
                                    table
                                        .tr()
                                            .td().dynamic((td, obj) -> td.text(count[0]++)).__()
                                            .td()
                                                .<Track>dynamic((td, trk) -> td
                                                    .a()
                                                        .attrHref(track.getUrl())
                                                        .attrTarget("_blank")
                                                        .text(track.getName())
                                                    .__())
                                            .__()
                                            .td().<Track>dynamic((td, trk) -> td.text(track.getListeners())).__()
                                        .__()
                                );
                            })
                        .__()
                    .__()
                .__()
            .__();
    }
    static void toptracksTemplateOfAndDynamic(HtmlPage view) {
        view
            .html()
                .head()
                    .link()
                        .attrRel(EnumRelType.STYLESHEET)
                        .attrHref("/stylesheets/bootstrap.min.css")
                    .__()
                    .title().text("TopGenius.eu").__()
                .__()
                .body()
                    .div()
                        .attrClass("container")
                        .table()
                            .attrClass("table")
                            .tr()
                                .th().text("Rank").__()
                                .th().text("Track").__()
                                .th().text("Listeners").__()
                            .__()
                            .<Stream<Track>>dynamic((table, tracks) -> {
                                int[] count = {1};
                                tracks.forEach(track ->
                                    table
                                        .tr()
                                            .td().text(count[0]++).__()
                                            .td()
                                                .a()
                                                    .attrHref(track.getUrl())
                                                    .attrTarget("_blank")
                                                    .text(track.getName())
                                                .__()
                                            .__()
                                            .td().text(track.getListeners()).__()
                                        .__()
                                );
                            })
                        .__()
                    .__()
                .__()
            .__();
    }
}

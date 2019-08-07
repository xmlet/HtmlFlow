package htmlflow.test.views;

import htmlflow.DynamicHtml;
import htmlflow.test.model.Track;
import org.xmlet.htmlapifaster.EnumRelType;

import java.util.stream.Stream;

public class HtmlDynamicChainTwiceOnTopgenius {

    public static final DynamicHtml<Stream<Track>> toptracksWrongDynamicTwice = DynamicHtml
        .view(HtmlDynamicChainTwiceOnTopgenius::toptracksTemplateDynamicTwice);

    public static final DynamicHtml<Stream<Track>> toptracksOkOfWithDynamic = DynamicHtml
        .view(HtmlDynamicChainTwiceOnTopgenius::toptracksTemplateOfAndDynamic);

    static void toptracksTemplateDynamicTwice(DynamicHtml<Stream<Track>> view, Stream<Track> tracks) {
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
                            .dynamic(table -> {
                                int[] count = {1};
                                tracks.forEach(track ->
                                    table
                                        .tr()
                                            .td().dynamic(td -> td.text(count[0]++)).__()
                                            .td()
                                                .dynamic(td -> td
                                                    .a()
                                                        .attrHref(track.getUrl())
                                                        .attrTarget("_blank")
                                                        .text(track.getName())
                                                    .__())
                                            .__()
                                            .td().dynamic(td -> td.text(track.getListeners())).__()
                                        .__()
                                );
                            })
                        .__()
                    .__()
                .__()
            .__();
    }
    static void toptracksTemplateOfAndDynamic(DynamicHtml<Stream<Track>> view, Stream<Track> tracks) {
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
                            // IF all bound tables have the same number of rows then
                            // we may postpone dynamic for each individual field.
                            .of(table -> {
                                int[] count = {1};
                                tracks.forEach(track ->
                                    table
                                        .tr()
                                            .td().dynamic(td -> td.text(count[0]++)).__()
                                            .td()
                                                .dynamic(td -> td
                                                    .a()
                                                        .attrHref(track.getUrl())
                                                        .attrTarget("_blank")
                                                        .text(track.getName())
                                                    .__())
                                            .__()
                                            .td().dynamic(td -> td.text(track.getListeners())).__()
                                        .__()
                                );
                            })
                        .__()
                    .__()
                .__()
            .__();
    }
}

package htmlflow.test;

import htmlflow.test.model.Track;
import org.junit.Test;

import java.util.Iterator;

import static htmlflow.test.Utils.NEWLINE;
import static htmlflow.test.views.HtmlDynamicChainTwiceOnTopgenius.toptracksOkOfWithDynamic;
import static htmlflow.test.views.HtmlDynamicChainTwiceOnTopgenius.toptracksWrongDynamicTwice;
import static java.util.stream.Stream.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestDynamicCases {

    @Test(expected = IllegalStateException.class)
    public void testDynamicChainedTwice() {
        Track [] tracks = {
            new Track("Space Odyssey"),
            new Track("Bad"),
            new Track("Under Pressure")};
        toptracksWrongDynamicTwice.render(of(tracks));
        String html = toptracksWrongDynamicTwice.render(of(tracks));
    }

    @Test
    public void testOfAndDynamicChained() {
        Track [] tracks1 = {
            new Track("Space Odyssey"),
            new Track("Under Pressure")};
        Track [] tracks2 = {
            new Track("Bad"),
            new Track("Sit down")};
        String html1 = toptracksOkOfWithDynamic.render(of(tracks1));
        Iterator<String> iter1 = NEWLINE.splitAsStream(html1).iterator();
        Utils
                .loadLines("topgeniusSpaceAndPressure.html")
                .forEach(expected -> assertEquals(expected, iter1.next()));
        assertFalse(iter1.hasNext());

        String html2 = toptracksOkOfWithDynamic.render(of(tracks2));
        Iterator<String> iter2 = NEWLINE.splitAsStream(html2).iterator();
        Utils
                .loadLines("topgeniusBadAndSit.html")
                .forEach(expected -> assertEquals(expected, iter2.next()));
        assertFalse(iter2.hasNext());
    }
}

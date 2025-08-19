package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.HtmlViewAsync;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

class TestAsyncViewInConcurInMultpleThreads {

    @Test
    void testMultipleThreadsInViewAsync() throws InterruptedException {
        HtmlViewAsync<Object> view = HtmlFlow.viewAsync(TestAsyncViewInConcurInMultpleThreads::template).threadSafe();
        checkRender(() -> view.renderAsync().join());
    }

    @Test
    void testMultipleThreadsInView() throws InterruptedException {
        HtmlView<Object> view = HtmlFlow.view(TestAsyncViewInConcurInMultpleThreads::template).threadSafe();
        checkRender(view::render);
    }

    @Test
    void testMultipleThreadsInViewAsyncHot() throws InterruptedException {
        HtmlViewAsync<Object> view = HtmlFlow.viewAsync(TestAsyncViewInConcurInMultpleThreads::template).setPreEncoding(false).threadSafe();
        checkRender(() -> view.renderAsync().join());
    }

    @Test
    void testMultipleThreadsInViewHot() throws InterruptedException {
        HtmlView<Object> view = HtmlFlow.view(TestAsyncViewInConcurInMultpleThreads::template).setPreEncoding(false).threadSafe();
        checkRender(view::render);
    }

    private void checkRender(Supplier<String> render) throws InterruptedException {
        final int threadCount = 50;
        Thread[] thread = new Thread[threadCount];
        String[] html = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadNumber = i;
            thread[i] = new Thread(() -> {
                try {
                    html[threadNumber] = render.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        for (int i = 0; i < threadCount; i++) {
            thread[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            thread[i].join();
            assertEquals(EXPECTED_HTML, html[i]);
        }
    }

    @SuppressWarnings("squid:S2925")
    static void template(HtmlPage view) {
        view.div().span().of(span -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    span.a().attrHref("link").text("text").__().of(s -> {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        s.a().attrHref("link2").text("text2").__();
                    });
                }).__()
                .__();
    }

    static final String EXPECTED_HTML = """
            <div>
            \t<span>
            \t\t<a href="link">
            \t\t\ttext
            \t\t</a>
            \t\t<a href="link2">
            \t\t\ttext2
            \t\t</a>
            \t</span>
            </div>""";
}

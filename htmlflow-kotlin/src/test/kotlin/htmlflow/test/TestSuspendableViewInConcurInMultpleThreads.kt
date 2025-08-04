package htmlflow.test

import htmlflow.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.Span

class TestSuspendableViewInConcurInMultpleThreads {
    @Test
    @Throws(InterruptedException::class)
    fun testMultipleThreadsInViewAsync() {
        val view = viewSuspend<Any> { template() }.threadSafe()
        checkRender { view.render() }
    }
}

private fun checkRender(render: suspend () -> String) {
    // println("start");
    val threadCount = 50
    // AtomicInteger left = new AtomicInteger(threadCount);
    val thread = arrayOfNulls<Job>(threadCount)
    val html = arrayOfNulls<String>(threadCount)
    runBlocking {
        for (i in 0 until threadCount) {
            thread[i] = launch {
                try {
                    html[i] = render()
                    // println("Thread " + threadNumber + " exited, remaining: " + left.decrementAndGet());
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        for (i in 0 until threadCount) {
            thread[i]?.join()
            Assert.assertEquals(TestAsyncViewInConcurInMultpleThreads.EXPECTED_HTML, html[i])
        }
    }
    // println("end");
}

private fun HtmlPage.template() {
    div().span().of { span: Span<Div<HtmlPage>> ->
        try {
            Thread.sleep(1)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        span.a().attrHref("link").text("text").l
            .of { s: Span<Div<HtmlPage>> ->
                try {
                    Thread.sleep(1)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                s.a().attrHref("link2").text("text2").l
            }
    }.l.l
}


private const val expectedHtml = """<div>
	<span>
		<a href="link">
			text
		</a>
		<a href="link2">
			text2
		</a>
	</span>
</div>"""
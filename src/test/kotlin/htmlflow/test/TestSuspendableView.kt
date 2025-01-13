package htmlflow.test

import htmlflow.HtmlPage
import htmlflow.HtmlViewSuspend
import htmlflow.await
import htmlflow.l
import htmlflow.suspending
import htmlflow.test.model.AsyncModel
import htmlflow.test.model.Student
import htmlflow.viewSuspend
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.Duration
import java.util.concurrent.ExecutionException

class TestSuspendableView {

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun given_async_work_when_create_view_and_render_it_twice_on_same_model() {
        val mem = ByteArrayOutputStream()
        val studentFlux: Publisher<Student> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(10))
            .doOnNext { nr: Int -> println(" ########################## Emit $nr") }
            .map { nr: Int ->
                Student(
                    nr.toLong(),
                    randomNameGenerator(Math.toIntExact(nr.toLong()))
                )
            }
        val titlesFlux: Publisher<String> = Flux.fromArray(arrayOf("Nr", "Name"))
        val asyncModel = AsyncModel(titlesFlux, studentFlux)
        val view: HtmlViewSuspend<AsyncModel<String, Student>> = viewSuspend { testSuspendingModel() }
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel)
        mem.reset()
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel)
    }

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun given_async_work_when_create_view_and_render_it_twice_on_other_model() {
        val mem = ByteArrayOutputStream()
        val studentFlux: Publisher<Student> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(10))
            .doOnNext { nr: Int -> println(" ########################## Emit $nr") }
            .map { nr: Int ->
                Student(
                    nr.toLong(),
                    randomNameGenerator(Math.toIntExact(nr.toLong()))
                )
            }
        val titlesFlux: Publisher<String> = Flux.fromArray(arrayOf("Nr", "Name"))
        val asyncModel = AsyncModel(titlesFlux, studentFlux)
        val view: HtmlViewSuspend<AsyncModel<String, Student>> = viewSuspend { testSuspendingModel() }
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel)
        //
        // 2nd render
        //
        val asyncModel2nd = AsyncModel(titlesFlux, Flux.range(6, 4)
            .delayElements(Duration.ofMillis(10))
            .doOnNext { nr: Int -> println(" ########################## Emit $nr") }
            .map { nr: Int ->
                Student(
                    nr.toLong(),
                    randomNameGenerator(Math.toIntExact(nr.toLong()))
                )
            })
        mem.reset()
        write_and_assert_asyncview("asyncTestSecond.html", mem, view, asyncModel2nd)
    }

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun given_async_work_when_create_view_then_returns_thenable_and_prints_correct_html() {
        val mem = ByteArrayOutputStream()
        val studentFlux: Publisher<Student> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(10))
            .doOnNext { nr: Int -> println(" ########################## Emit $nr") }
            .map { nr: Int ->
                Student(
                    nr.toLong(),
                    randomNameGenerator(Math.toIntExact(nr.toLong()))
                )
            }
        val titlesFlux: Publisher<String> = Flux.fromArray(arrayOf("Nr", "Name"))
        val asyncModel = AsyncModel(titlesFlux, studentFlux)
        val view: HtmlViewSuspend<AsyncModel<String, Student>> = viewSuspend { testSuspendingModel() }
        write_and_assert_asyncview("asyncTest.html", mem, view, asyncModel)
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun write_and_assert_asyncview(
        expectedHtml: String?, mem: ByteArrayOutputStream,
        view: HtmlViewSuspend<AsyncModel<String, Student>>, asyncModel: AsyncModel<String, Student>
    ) = runBlocking {
        view.write(PrintStream(mem), asyncModel)
        val actual = Utils.NEWLINE
            .splitAsStream(mem.toString())
            .iterator()
        Utils
            .loadLines(expectedHtml)
            .forEach { expected: String? ->
                val next = actual.next()
                Assertions.assertEquals(expected, next)
            }
        Assertions.assertFalse(actual.hasNext())
    }

    @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun given_async_work_with_double_delay_when_create_view_then_returns_thenable_and_prints_correct_html() {
        val studentFlux: Publisher<Student> = Flux.range(1, 5)
            .delayElements(Duration.ofMillis(10))
            .doOnNext { nr: Int -> println(" ########################## Emit $nr") }
            .map { nr: Int ->
                Student(
                    nr.toLong(),
                    randomNameGenerator(Math.toIntExact(nr.toLong()))
                )
            }
        val titlesFlux: Publisher<String> = Flux.fromArray(arrayOf("Nr", "Name")).delayElements(Duration.ofSeconds(1))
        val asyncModel = AsyncModel(titlesFlux, studentFlux)
        val mem = ByteArrayOutputStream()
        val view: HtmlViewSuspend<AsyncModel<String, Student>> = viewSuspend { testSuspendingModel() }
        runBlocking { view.write(PrintStream(mem), asyncModel) }
        val actual = Utils.NEWLINE
            .splitAsStream(mem.toString())
            .iterator()
        Utils
            .loadLines("asyncTest.html")
            .forEach { expected: String? ->
                val next = actual.next()
                println(next)
                Assertions.assertEquals(expected, next)
            }
        Assertions.assertFalse(actual.hasNext())
    }
}

fun HtmlPage.testSuspendingModel() {
    val thenable = html()
        .body()
        .div()
        .p()
        .text("Students from a school board")
        .l
        .l
        .div()
        .table()
        .thead()
        .tr()
        .await { model: AsyncModel<String, Student>, cb ->
            Flux.from(model.titles)
                .doOnComplete(cb)
                .doOnNext { th().text(it).l }
                .subscribe()
        }
        .l.l
        .tbody()
        .suspending { model: AsyncModel<String, Student> ->
            model.items.asFlow()
                .collect { student ->
                    tr()
                        .th()
                        .text(student.nr)
                        .l
                        .td()
                        .text(student.name)
                        .l
                        .l
                }
        }
        .l.l.l
        .div()
        .p()
        .text("Best students in school")
        .l.l.l.l
    Assertions.assertNotNull(thenable)
}

fun randomNameGenerator(nr: Int): String? {
    val names = arrayOf(
        "Pedro", "Manuel", "Maria", "Clara", "Rafael",
        "Ze", "Joan", "Gui", "Valery"
    )
    return names[nr - 1]
}
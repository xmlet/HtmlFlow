package htmlflow.test

import htmlflow.test.model.AsyncModel
import htmlflow.test.model.Student
import htmlflow.viewSuspend
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.stream.Collectors
import java.util.stream.IntStream

class TestSuspendableViewInConcurrency {
    val NR_OF_TASKS = Runtime.getRuntime().availableProcessors()
    val studentFlux: Publisher<Student> = Flux.range(1, 5)
        .delayElements(Duration.ofMillis(10))
        .map { nr: Int ->
            Student(
                nr.toLong(),
                TestAsyncView.randomNameGenerator(Math.toIntExact(nr.toLong()))
            )
        }
    val titlesFlux: Publisher<String> = Flux.fromArray(arrayOf("Nr", "Name"))

    @Test
    fun check_asyncview_processing_in_sequential_tasks_and_unsafe_view() = runBlocking {
        /**
         * Arrange View
         */
        /**
         * Arrange View
         */
        val view = viewSuspend<AsyncModel<String, Student>> { testSuspendingModel() }
            .threadUnsafe()
        /**
         * Act and Assert
         * Since Stream is Lazy then there is a vertical processing and a sequential execution between tasks.
         */
        /**
         * Act and Assert
         * Since Stream is Lazy then there is a vertical processing and a sequential execution between tasks.
         */
        (0..NR_OF_TASKS)
            .map {
                view.render(AsyncModel(titlesFlux, studentFlux))
            }
            .forEach { assertHtml(it)}
    }

    @Test
    fun check_asyncview_processing_in_concurrent_tasks_and_parallel_threads() {
        /**
         * Arrange View
         */
        val view = viewSuspend<AsyncModel<String, Student>> { testSuspendingModel() }
            .threadSafe()
        /**
         * Act and Assert
         * Collects to dispatch resolution through writeAsync() concurrently.
         */
        IntStream
            .range(0, NR_OF_TASKS)
            .parallel()
            .mapToObj { GlobalScope.async {
                view.render(
                    AsyncModel(titlesFlux, studentFlux)
                )
            } }
            .collect(Collectors.toList())
            .forEach{ runBlocking {
                assertHtml(
                    it.await()
                )
            }}
    }

    private fun assertHtml(html: String) {
        val actual = Utils.NEWLINE
            .splitAsStream(html)
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
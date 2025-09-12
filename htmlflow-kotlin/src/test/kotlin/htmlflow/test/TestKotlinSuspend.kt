package htmlflow.test

import htmlflow.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import kotlin.test.assertEquals

class Task(
    val title: String?,
    val description: String,
    val priority: String
)

class TestKotlinSuspend {

    @Test(expected = IllegalStateException::class)
    fun doc_with_illegal_use_of_suspending() {
        HtmlFlow.doc(System.out)
            .html().body().div()
            .suspending { model: String -> p().text(model).l.l}
            .l.l.l
    }
    @Test(expected = IllegalStateException::class)
    fun view_with_illegal_use_of_suspending() {
        HtmlFlow.view<String>{ page -> page
            .html().body().div()
            .of { div ->
                runBlocking {
                    div.suspending { -> div.p().text("test").l.l }
                }
            }
            .l.l.l
        }
    }
    @Test
    fun doc_with_suspend_function_awaiting() {
        val out = StringBuilder()
        runBlocking {
            val swim = completedFuture(Task("Swim", "Sea open water", "High"))
            async {
                taskDocTableSuspendAwait(out, swim) // This Doc includes a suspension of 1000 ms
            }
            delay(100) // Gives chance to former doc render to the suspension point.
            assertEquals(expectedHtmlTablePart1, out.toString())
        }
        // Check the final document after completion of all tasks
        assertEquals(expectedHtmlTable, out.toString())
    }
    @Test
    fun view_with_suspend_function_awaiting() = runBlocking {
        val out = StringBuilder()
        val swim = completedFuture(Task("Swim", "Sea open water", "High"))
        taskViewTableSuspendAwait.write(out, swim)
        assertEquals(expectedHtmlTable, out.toString())
    }
}

private val taskViewTableSuspendAwait: HtmlViewSuspend<CompletableFuture<Task>> = viewSuspend {
    html()
    .head().title().text("Dummy Table").l
    .l
    .body()
    .h1().text("Dummy Table").l
    .hr().l
    .div()
    .table()
    .tr()
    .th().text("Title").l
    .th().text("Description").l
    .th().text("Priority").l
    .l // tr
    .suspending { cf: CompletableFuture<Task> ->
        val task = cf.await()
        delay(1000)
        tr()
          .td().text(task.title).l
          .td().text(task.description).l
          .td().text(task.priority).l
        .l // tr

    }
    .l
    .l
    .l
    .l
}

private suspend fun taskDocTableSuspendAwait(out: Appendable, cf: CompletableFuture<Task>) {
    HtmlFlow
        .doc(out)
        .html()
        .head().title().text("Dummy Table").l
        .l
        .body()
        .h1().text("Dummy Table").l
        .hr().l
        .div()
        .table()
        .tr()
        .th().text("Title").l
        .th().text("Description").l
        .th().text("Priority").l
        .l // tr
        .suspending { ->
            val task = cf.await()
            delay(1000)
            tr()
                .td().text(task.title).l
                .td().text(task.description).l
                .td().text(task.priority).l
            .l // tr

        }
        .l
        .l
        .l
        .l
}

private val expectedHtmlTable = """
<!DOCTYPE html>
<html>
	<head>
		<title>
			Dummy Table
		</title>
	</head>
	<body>
		<h1>
			Dummy Table
		</h1>
		<hr>
		<div>
			<table>
				<tr>
					<th>
						Title
					</th>
					<th>
						Description
					</th>
					<th>
						Priority
					</th>
				</tr>
				<tr>
					<td>
						Swim
					</td>
					<td>
						Sea open water
					</td>
					<td>
						High
					</td>
				</tr>
			</table>
		</div>
	</body>
</html>
""".trimIndent()

private val expectedHtmlTablePart1 = """
<!DOCTYPE html>
<html>
	<head>
		<title>
			Dummy Table
		</title>
	</head>
	<body>
		<h1>
			Dummy Table
		</h1>
		<hr>
		<div>
			<table>
				<tr>
					<th>
						Title
					</th>
					<th>
						Description
					</th>
					<th>
						Priority
					</th>
				</tr>
""".trimIndent()

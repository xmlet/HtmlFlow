package app;

import static java.lang.System.out;
import htmlflow.HtmlView;
import htmlflow.ModelBinder;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import model.Priority;
import model.Status;
import model.Task;

public class TestTaskToHtml {
	private static ModelBinder<Task> binderGetId(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getId());
		}};
	}
	private static ModelBinder<Task> binderGetTitle(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getTitle());
		}};
	}
	private static ModelBinder<Task> binderGetDescription(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getDescription());
		}};
	}
	private static ModelBinder<Task> binderGetPriority(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getPriority());
		}};
	}

	private static HtmlView<Task> taskDetailsView(){
		HtmlView<Task> taskView = new HtmlView<Task>();

		taskView.head().title("Task Details");
		taskView
		.body()
		.heading(1, "Task Details")
		.hr()
		.div()
		.text("Id: ").text(binderGetId())
		.br()
		.text("Title: ").text(binderGetTitle())
		.br()
		.text("Description: ").text(binderGetDescription())
		.br()
		.text("Priority: ").text(binderGetPriority());

		return taskView;
	}
	
	private static HtmlView<Iterable<Task>> taskListView(){
		HtmlView<Iterable<Task>> taskView = new HtmlView<Iterable<Task>>();
		taskView.head().title("Task Details");

		HtmlTable<Iterable<Task>> t = taskView.body()
		.heading(1, "Task Details")
		.hr()
		.div()
		.table();
		HtmlTr<Iterable<Task>> headerRow = t.tr();
		headerRow.th().text("Id");
		headerRow.th().text("Title");
		headerRow.th().text("Description");
		t.trFromIterable(binderGetId(), binderGetTitle(), binderGetDescription());
		return taskView;
	}
	static void testTasks() throws IOException{
		Task t1 = new Task("Preparar Aula","Desenvolver um problema para contextualização do padrão de desenho template method.",Priority.High,Status.Progress);
		Task t2 = new Task("Concluir AOM compiler v2","Processar todas as classes como transaccionais com raíz em DoubleLayout",Priority.Normal,Status.Unstarted);
		Task t3 = new Task("Jantar do dia do pai","Marcar mesa num restaurante interessante!",Priority.High,Status.Progress);
		try{
			PrintStream out = new PrintStream(new FileOutputStream("Task.html"));
			taskDetailsView().setPrintStream(out).write(1, t1);
			Runtime.getRuntime().exec("explorer Task.html");
		}finally{
			out.close();
		}
		try{
			PrintStream out = new PrintStream(new FileOutputStream("TaskList.html"));
			taskListView().setPrintStream(out).write(1, Arrays.asList(t1, t2, t3));
			Runtime.getRuntime().exec("explorer TaskList.html");
		}finally{
			out.close();
		}

	}
}

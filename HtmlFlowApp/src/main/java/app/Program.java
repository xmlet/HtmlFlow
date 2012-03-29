package app;

import htmlflow.HtmlTemplate;
import htmlflow.ModelBinder;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;
import static java.lang.System.in;

import model.Priority;
import model.Status;
import model.Task;

public class Program {
	private static ModelBinder<Task> binderGetId(){
		return new ModelBinder<Task>() {public String bind(Task model) {
				return model.getId() + "";
			}
		};
	}
	private static ModelBinder<Task> binderGetTitle(){
		return new ModelBinder<Task>() {public String bind(Task model) {
				return model.getTitle();
			}
		};
	}
	private static ModelBinder<Task> binderGetDescription(){
		return new ModelBinder<Task>() {public String bind(Task model) {
				return model.getDescription();
			}
		};
	}
	
	
	private static HtmlTemplate<Task> taskDetailsView(){
		HtmlTemplate<Task> taskView = new HtmlTemplate<Task>();
		
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
					.text("Description: ").text(binderGetDescription());
		
		return taskView;
	}
	private static HtmlTemplate<Iterable<Task>> taskListView(){
		HtmlTemplate<Iterable<Task>> taskView = new HtmlTemplate<Iterable<Task>>();
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
	private static void printToFile(String fileName, String content) throws IOException{
		FileWriter out = new FileWriter(fileName);
		out.write(content);
		out.flush();
		out.close();
	}
	private static void testTasks() throws IOException{
		Task t1 = new Task("Preparar Aula","Desenvolver um problema para contextualização do padrão de desenho template method.",Priority.High,Status.Progress);
		Task t2 = new Task("Concluir AOM compiler v2","Processar todas as classes como transaccionais com raíz em DoubleLayout",Priority.Normal,Status.Unstarted);
		Task t3 = new Task("Jantar do dia do pai","Marcar mesa num restaurante interessante!",Priority.High,Status.Progress);
		printToFile("Task.html", taskDetailsView().write(1, t1));
		Runtime.getRuntime().exec("explorer Task.html");
		printToFile("TaskList.html", taskListView().write(1, Arrays.asList(t1, t2, t3)));
		Runtime.getRuntime().exec("explorer TaskList.html");
	}
	public static void main(String[] args) throws IOException {
		
		testTasks();
		//
		// run command shell
		//
		Scanner cin = new Scanner(in);
		out.println("****** Commmand shell application *****");
		while(true){
			out.print("> ");
			out.flush();
			String inLine = cin.nextLine();
			if(inLine.startsWith("new ")){
				
			}
		}

	}
}

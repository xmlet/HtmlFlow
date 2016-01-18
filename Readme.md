The *HTmlFlow* is a simple Java library for writing HTML documents in a fluent 
style into a `java.io.PrintStream`. 
For instance, considering a Java class `Task`with three properties: `Title`, 
`Description` and a `Priority`, then we can produce an HTML document 
in the following way:

``` java
import htmlflow.HtmlView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class App {
    public static void main(String [] args) throws IOException {
        //
        // Creates and setup an HtmlView object for task details
        //
        HtmlView<?> taskView = new HtmlView<>();
        taskView
                .head()
                .title("Task Details")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
                .body().classAttr("container")
                .heading(1, "Task Details")
                .hr()
                .div()
                .text("Title: ").text("ISEL MPD project")
                .br()
                .text("Description: ").text("A Java library for serializing objects in HTML.")
                .br()
                .text("Priority: ").text("HIGH");
        //
        // Produces an HTML file document
        //
        try(PrintStream out = new PrintStream(new FileOutputStream("Task.html"))){
            taskView.setPrintStream(out).write();
            Runtime.getRuntime().exec("explorer Task.html");
        }
    }
}
```

The *HtmlFlow* also supports *binders* that enable the same HTML view to be used (or **bind**) with different object models. 

``` java
	public static void main(String[] args) throws IOException {
		HtmlView<Task> taskView = taskDetailsView();
		try{
			Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High);
			PrintStream out = new PrintStream(new FileOutputStream("Task1.html"));
			taskView.setPrintStream(out).write(1, t1);
			Runtime.getRuntime().exec("explorer Task1.html");
		}finally{
			out.close();
		}
		try{
			Task t2 = new Task("Special dinner", "Have dinner with someone!", Priority.Normal);
			PrintStream out = new PrintStream(new FileOutputStream("Task2.html"));
			taskView.setPrintStream(out).write(1, t2); // Now we can use the same HtmlView with a different task
			Runtime.getRuntime().exec("explorer Task2.html");
		}finally{
			out.close();
		}
	}	
	private static HtmlView<Task> taskDetailsView(){
		HtmlView<Task> taskView = new HtmlView<Task>();
		taskView.head().title("Task Details");
		taskView
		.body()
		.heading(1, "Task Details")
		.hr()
		.div()
		.text("Title: ").text(binderGetTitle())
		.br()
		.text("Description: ").text(binderGetDescription())
		.br()
		.text("Priority: ").text(binderGetPriority());
		return taskView;
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
```

Finally, an example of producing an HTML table bind to a list of tasks:

``` java
	public static void main(String[] args) throws IOException {
		HtmlView<Task> taskView = taskDetailsView();
		try{
			Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High);
			Task t2 = new Task("Special dinner", "Have dinner with someone!", Priority.Normal);
			Task t3 = new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High);
			PrintStream out = new PrintStream(new FileOutputStream("TaskList.html"));
			taskListView().setPrintStream(out).write(1, Arrays.asList(t1, t2, t3));
			Runtime.getRuntime().exec("explorer TaskList.html");
		}finally{
			out.close();
		}
	}
	private static HtmlView<Iterable<Task>> taskListView(){
		HtmlView<Iterable<Task>> taskView = new HtmlView<Iterable<Task>>();
		taskView.head().title("Task List");

		HtmlTable<Iterable<Task>> t = taskView.body()
		.heading(1, "Task Details")
		.hr()
		.div()
		.table();
		HtmlTr<Iterable<Task>> headerRow = t.tr();
		headerRow.th().text("Title");
		headerRow.th().text("Description");
		headerRow.th().text("Priority");
		t.trFromIterable(binderGetTitle(), binderGetDescription(), binderGetPriority());
		return taskView;
	}
```
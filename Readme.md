The *HTmlFlow* is a simple Java library for writing HTML documents in a fluent style into a `java.io.PrintStream`. For instance, considering a Java class `Task`with three properties: `Title`, `Description` and a `Priority`, then we can produce an HTML document for a `Task` object in the following way:

``` java
	//
	// Creates a new Task object
	//
	Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High);
	//
	// Creates and setup an HtmlView object for task details
	//
	HtmlView<Task> taskView = new HtmlView<Task>();
	taskView.head().title("Task Details");
	taskView
	.body()
	.heading(1, "Task Details")
	.hr()
	.div()
	.text("Title: ").text(t1.getTitle())
	.br()
	.text("Description: ").text(t1.getDescription());
	.br()
	.text("Priority: ").text(t1.getPriority().toString());
	//
	// Produces an HTML file document
	//
	try{
		PrintStream out = new PrintStream(new FileOutputStream("Task.html"));
		taskView.setPrintStream(out).write(1, t1);
		Runtime.getRuntime().exec("explorer Task.html");
	}finally{
		out.close();
	}

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

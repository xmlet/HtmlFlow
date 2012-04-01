The *HTmlFlow* is a simple Java library for writing HTML documents in a fluent style into a `java.io.PrintStream`. For instance, considering a Java class `Task`with three properties: `Title`, `Description` and a `Priority`, then we can produce an HTML document for a `Task` object in the following way:

<!--code-->
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
		taskDetailsView().setPrintStream(out).write(1, t1);
		Runtime.getRuntime().exec("explorer Task.html");
	}finally{
		out.close();
	}

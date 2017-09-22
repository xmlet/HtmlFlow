
[![Build](https://sonarcloud.io/api/badges/gate?key=com.github.fmcarvalho%3Ahtmlflow)](https://sonarcloud.io/dashboard?id=com.github.fmcarvalho%3Ahtmlflow)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.fmcarvalho%3Ahtmlflow&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.fmcarvalho%3Ahtmlflow)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.fmcarvalho/htmlflow/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.fmcarvalho/htmlflow)

HtmlFlow library purpose is to allow Java applications to easily write HTML
documents in a fluent style into a `java.io.PrintStream`.
The library also supports *data binders* that enable the same HTML view to be 
bound with different object models.

## Installation

First, in order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.fmcarvalho</groupId>
    <artifactId>htmlflow</artifactId>
    <version>1.1</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/fmcarvalho/htmlflow/)

## Usage

Next we present an example withOUT model binding (move forward to check further 
examples with data binding):

``` java
import htmlflow.HtmlView;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

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
            Desktop.getDesktop().browse(URI.create("Task.html"));
        }
    }
}
```

In the following we present an example binding the same view to different domain 
objects of the same type.
To that end, consider a Java class `Task` with three properties: `Title`, 
`Description` and a `Priority`, then we can produce several HTML documents
in the following way:


``` java
import htmlflow.HtmlView;
import htmlflow.HtmlWriter;

import model.Priority;
import model.Task;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;

public class App {

    public static void main(String [] args) {
        HtmlView<Task> taskView = taskDetailsView();
        Task [] dataSource = {
            new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
            new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        };
        Arrays
            .stream(dataSource)
            .forEach(task -> printHtml(taskView, task, "task" + task.getId() + ".html"));
    }
    private static <T> void printHtml(HtmlWriter<T> html, T model, String path){
        try(PrintStream out = new PrintStream(new FileOutputStream(path))){
            html.setPrintStream(out).write(model);
            Desktop.getDesktop().browse(URI.create(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static HtmlView<Task> taskDetailsView(){
        HtmlView<Task> taskView = new HtmlView<>();
        taskView
                .head()
                .title("Task Details")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
                .body().classAttr("container")
                .heading(1, "Task Details")
                .hr()
                .div()
                .text("Title: ").text(Task::getTitle)
                .br()
                .text("Description: ").text(Task::getDescription)
                .br()
                .text("Priority: ").text(Task::getPriority);
        return taskView;
    }
}
```

Finally, an example of producing an HTML table binding to a list of tasks:

``` java
import htmlflow.HtmlView;
import htmlflow.HtmlWriter;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;

import model.Priority;
import model.Task;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String [] args)  throws IOException {
        HtmlView<Iterable<Task>>  taskView = taskListView();
        List<Task> dataSource = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        try(PrintStream out = new PrintStream(new FileOutputStream("TaskList.html"))){
            taskView.setPrintStream(out).write(dataSource);
            Desktop.getDesktop().browse(URI.create("TaskList.html"));
        }
    }
    private static HtmlView<Iterable<Task>> taskListView(){
        HtmlView<Iterable<Task>> taskView = new HtmlView<Iterable<Task>>();
        taskView
                .head()
                .title("Task List")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        HtmlTable<Iterable<Task>> table = taskView
                .body().classAttr("container")
                .heading(1, "Task List")
                .div()
                .table().classAttr("table");
        HtmlTr<Iterable<Task>> headerRow = table.tr();
        headerRow.th().text("Title");
        headerRow.th().text("Description");
        headerRow.th().text("Priority");
        table.trFromIterable(Task::getTitle, Task::getDescription, Task::getPriority);
        return taskView;
    }
}
```

## Changelog

### 1.1 (March 23, 2017)

* Read HTML header from a template resource located in `templates/HtmlView-Header.txt`.
This template is bundled and loaded form the HtmlFlow JAR by default.
However, you are free to use your own header template file `templates/HtmlView-Header.txt`
and include its location in the classpath to replace the existing one.
This also solves the #16 Issue.


## License

[MIT](https://github.com/fmcarvalho/HtmlFlow/blob/master/LICENSE)

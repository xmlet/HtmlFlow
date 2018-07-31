# HtmlFlow

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3Ahtmlflow)
[![Maven Central Version](http://img.shields.io/maven-central/v/com.github.xmlet/htmlflow.svg)](http://search.maven.org/#search%7Cga%7C1%7Cxmlet%20htmlflow)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.xmlet%3Ahtmlflow&metric=Coverage)

HtmlFlow library purpose is to allow Java applications to easily write typesafe
HTML documents in a fluent style respecting all HTML 5 rules (for example,
`h1().div()` gives a compilation error because that would place a `div` inside
an `h1` which is not allowed in HTML).
So, whenever we write `.` after an element the intelissense will just suggest
the set of allowed elements and attributes.

The HtmlFlow API is according to HTML5 and is generated with the support
of an automated framework ([xmlet](https://github.com/xmlet/)) based on an [XSD
definition of the HTML5](https://github.com/xmlet/HtmlApi/blob/master/src/main/resources/html_5.xsd)
syntax.
Thus, all attributes are strongly typed with enumerated types which restrict
the set of accepted values.
Finally, HtmlFlow also supports *data binders* that enable the same HTML view to
be bound with different object models.

## Installation

First, in order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>htmlflow</artifactId>
    <version>2.0</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/xmlet/htmlflow/)

## Usage

All methods return the created element, except `text()` which returns
the element containing the text node (the `this`).
There is also a method `º()` which returns the parent element.   
Next we present an example withOUT model binding (move forward to check
further examples with data binding):

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
            .title().text("Task Details").º()
            .link()
            .attrRel(Enumrel.STYLESHEET)
            .attrType(Enumtype.TEXT_CSS)
            .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
            .body().attrClass("container")
            .h1().text("Task Details").º()
            .hr().º()
            .div().text("Title: ISEL MPD project")
            .br().º()
            .text("Description: A Java library for serializing objects in HTML.")
            .br().º()
            .text("Priority: HIGH");
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

import model.Priority;
import model.Task;

import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;

public class App {

    public static void main(String [] args) throws IOException {
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
    private static <T> void printHtml(HtmlView<T> html, T model, String path) throws IOException {{
        try(PrintStream out = new PrintStream(new FileOutputStream(path))){
            html.setPrintStream(out).write(model);
            Desktop.getDesktop().browse(URI.create(path));
        }
    }
    
    private static HtmlView<Task> taskDetailsView(){
        HtmlView<Task> taskView = new HtmlView<>();
        taskView
            .head()
            .title().text("Task Details").º()
            .link()
            .attrRel(Enumrel.STYLESHEET)
            .attrType(Enumtype.TEXT_CSS)
            .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
            .body().attrClass("container")
            .h1()
            .text("Task Details").º()
            .hr().º()
            .div()
            .text("Title:").text(Task::getTitle)
            .br().º()
            .text("Description:").text(Task::getDescription)
            .br().º()
            .text("Priority:").text(Task::getPriority);
        return taskView;
    }
}
```

Finally, an example of producing an HTML table binding to a list of tasks:

``` java
import htmlflow.HtmlView;
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
           .head().title().text("Task List").º()
           .link()
           .attrRel(Enumrel.STYLESHEET)
           .attrType(Enumtype.TEXT_CSS)
           .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        Table<Div<Body<Html>>> table = taskView
            .body().attrClass("container")
            .h1().text("Task List").º()
            .hr().º()
            .div()
            .table()
            .attrClass("table")
            .tr()
              .th().text("Title").º()
              .th().text("Description").º()
              .th().text("Priority").º()
            .º();
       table.<List<Task>>binder((tbl, list) -> {
           list.forEach(task -> {
               tbl.tr()
                       .td().text(task.getTitle()).º()
                       .td().text(task.getDescription()).º()
                       .td().text(task.getPriority().toString());
           });
       });
        return taskView;
    }
}
```

## Changelog

### 2.0 (March, 2018)

HtmlFlow version 2.0 has full support for all existing HTML5 elements and
attributes. 
Moreover all attributes are strongly typed with enumerated types which
restrict accepted values. 
Now HtmlFlow API is constructed with the support of an automated
framework [xmlet](https://github.com/xmlet/) based on an 
[XSD definition of the HTML5](https://github.com/xmlet/HtmlApi/blob/master/src/main/resources/html_5.xsd)
syntax and rules. 
Thus we remove the package `htmlflow.attributes` and `htmlflow.elements`,
which have been replaced by the types defined in `org.xmlet.htmlapi` library.
This new approach forces HtmlFlow API to keep consistency along all methods use.
So **version 2.0** introduces some changes, namely:

* All fluent methods have no parameters. For example, formerly when we
were specifying the text node of a paragraph or heading (such as,
`.p("my text")` or `.h2("my title")`), now we have to chain an invocation
to the `text()` method (such as, `.p().text("my text")` or
`.h2().text("my title")`).

* All fluent methods now return the created element.
Whenever we need to proceed with the parent element we may chain an
invocation to `.º()`. For example, formerly when we wrote `.div().br().p()`
now we have to write `.div().br().º().p()`. 
Moreover the statement `.div().br().p()` not even compiles, because HTML
does not allow a paragraph inside a break line element, so we will get a
compilation error. 

* Except for `.text()` which does not create an element but a text node
instead, the rest of fluent methods return the created element. 
For `.text()` it returns the element containing the text node (the `this`).

* The new method `º()` returns the parent of an element. This method is
strongly typed so it returns exactly an instance of `T` where `T` is the
concrete class which extends `Element`. 
This is an important issue to respect the HTML structure and rules.

* Indentation. Now every element or text node is printed in a new line.
Formerly there were some exceptions, namely for text nodes which were
printed in the same line of the beginning tag.
These exceptions were removed.

* If you do not like the HtmlFlow print approach you are free to
implement your own `org.xmlet.htmlapi.ElementVisitor`.
See the `htmlflow.HtmlVisitor` implementation as a guideline.

* Removed default implementation of method `write()` in interface `HtmlWriter<T> `.


### 1.2 (September 22, 2017)

* Refactor unit tests to increase code coverage and to load the expected HTML output from the resources. 
* Include HtmlFlow in [SonarCloud.io](https://sonarcloud.io/dashboard?id=com.github.fmcarvalho%3Ahtmlflow) to analyze code quality. 
* Fix of [Issue 14](https://github.com/xmlet/HtmlFlow/issues/24) -- _Header.txt can't be loaded from resources_.

### 1.1 (March 23, 2017)

* Read HTML header from a template resource located in `templates/HtmlView-Header.txt`.
This template is bundled and loaded form the HtmlFlow JAR by default.
However, you are free to use your own header template file `templates/HtmlView-Header.txt`
and include its location in the classpath to replace the existing one.
This also solves the #16 Issue.

## License

[MIT](https://github.com/xmlet/HtmlFlow/blob/master/LICENSE)

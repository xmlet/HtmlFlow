# HtmlFlow

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3Ahtmlflow)
[![Maven Central Version](https://maven-badges.herokuapp.com/maven-central/com.github.xmlet/htmlflow/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cxmlet%20htmlflow)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.xmlet%3Ahtmlflow&metric=Coverage)

HtmlFlow library purpose is to allow Java applications to easily write typesafe
HTML documents in a fluent style respecting all HTML 5 rules (e.g. `h1().div()`
gives a compilation error because it goes against the content
allowed by `h1` according to HTML5). So, whenever you type `.` after an element
the intelissense will just suggest the set of allowed elements and attributes.

The HtmlFlow API is according to HTML5 and is generated with the support
of an automated framework ([xmlet](https://github.com/xmlet/)) based on an [XSD
definition of the HTML5](https://github.com/xmlet/HtmlApi/blob/master/src/main/resources/html_5.xsd)
syntax.
Thus, all attributes are strongly typed with enumerated types which restrict
the set of accepted values.
Finally, HtmlFlow also supports *data binders* that enable the same HTML view to
be bound with different object models.

[Get started](#getting-started) or check our examples in [HtmlTables](src/test/java/htmlflow/test/HtmlTables.java)
and [HtmlLists](src/test/java/htmlflow/test/HtmlLists.java).

## Installation

First, in order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>htmlflow</artifactId>
    <version>2.1</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/xmlet/htmlflow/)

## Getting started

All methods return the created element, except `text()` which returns
the element containing the text node (the `this`).
There is also a method `º()` which returns the parent element.   
Next we present an example building a static view (move forward to check
further examples with data binding):

```java
import htmlflow.HtmlView;

final static HtmlView viewDetails = HtmlView
    .html()
        .head()
            .title().text("Task Details").º()
            .link()
                .attrRel(EnumRelLinkType.STYLESHEET)
                .attrType(EnumTypeContentType.TEXT_CSS)
                .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
            .º() //link
        .º() //head
        .body()
            .attrClass("container")
            .h1().text("Task Details").º()
            .hr().º()
            .div().text("Title: ISEL MPD project")
                .br().º()
                .text("Description: A Java library for serializing objects in HTML.")
                .br().º()
                .text("Priority: HIGH")
            .º() //div
        .º() //body
    .º(); //html
```

From the previous `viewDetails` view you can get the resulting HTML in different 
ways: 1) get the resulting `String` through its `render()` method or 2) directly
write to any `Printstream` such as `System.out` or 3) or any other `PrintStream` chain
such as `new PrintStream(new FileOutputStream(path))`. **NOTE**: `PrintStream` is not
buffered, so you may need to interleave a `BufferedOutputStream` object to improve
performance. On the other hand `render()` internally uses a `StringBuilder` which
shows better speedup.

```java
String html = viewDetails.render(); // 1) Get a string with the HTML
viewDetails
    .setPrintStream(System.out)
    .write();                       // 2) print to the standard output
viewDetails
    .setPrintStream(new PrintStream(new FileOutputStream("details.html")))
    .write();                       // 3) write to details.html file

Desktop.getDesktop().browse(URI.create("details.html"));
```

Regardless the output approach you will get the same formatted HTML document:

```html
<!DOCTYPE html>
<html>
	<head>
		<title>
			Task Details
		</title>
		<link rel="Stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
	</head>
	<body class="container">
		<h1>
			Task Details
		</h1>
		<hr>
		<div>
			Title: ISEL MPD project
			<br>
			Description: A Java library for serializing objects in HTML.
			<br>
			Priority: HIGH
		</div>
	</body>
</html>
``` 

## Dynamic Views

Next we present an example binding the same view to different domain 
objects of the same type i.e. `Task`, which has three properties: `Title`,
`Description` and a `Priority`.
Given the dynamic view `taskDetailsView` we can bind this same view with different 
object models producing several HTML documents.
In the following example we bind three different `Task` instances with `taskDetailsView`
to produce three HTML documents: `task3.html`,`task4.html` and `task5.html`.     


``` java
final static HtmlView<Task> taskDetailsView = HtmlView
    .<Task>html()
        .head()
            .title().text("Task Details").º()
            .link()
                .attrRel(EnumRelLinkType.STYLESHEET)
                .attrType(EnumTypeContentType.TEXT_CSS)
                .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
            .º() //link
        .º() //head
        .body()
            .attrClass("container")
            .h1().text("Task Details").º()
            .hr().º()
            .div()
                .text("Title:").text(Task::getTitle)
                .br().º()
                .text("Description:").text(Task::getDescription)
                .br().º()
                .text("Priority:").text(Task::getPriority)
            .º() // div
        .º() //body
    .º(); // html
        
public class App {

    public static void main(String [] args) {
        Stream.of(
            new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new Task(4, "Special dinner", "Have dinner with someone!", Priority.Normal),
            new Task(5, "Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        )
        .forEach(task -> {
            String path = "task" + task.getId() + ".html";
            printHtml(path);
        });
    }
    
    private static <T> void printHtml(HtmlView<T> html, T model, String path) {
        try(PrintStream out = new PrintStream(new FileOutputStream(path))){
            html.setPrintStream(out).write(model);
            Desktop.getDesktop().browse(URI.create(path));
        }
    }
}
```

Finally, an example of a dynamic HTML table binding to a list of tasks:

``` java
final static HtmlView<Iterable<Task>> taskListView = HtmlView
    .<Iterable<Task>>html()
        .head()
            .title()
                .text("Task List")
                .º()
            .link()
                .attrRel(EnumRelLinkType.STYLESHEET)
                .attrType(EnumTypeContentType.TEXT_CSS)
                .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
            .º()
        .º()
        .body()
            .attrClass("container")
            .a().attrHref("https://github.com/fmcarvalho/HtmlFlow").text("HtmlFlow").º()
            .p().text("Html page built with HtmlFlow.").º()
            .h1().text("Task List").º()
            .hr().º()
            .div()
                .table()
                    .attrClass("table")
                    .tr()
                        .th().text("Title").º()
                        .th().text("Description").º()
                        .th().text("Priority").º()
                    .º()
                    // prior version 2.0: table.trFromIterable(Task::getTitle, Task::getDescription, Task::getPriority);
                    .<Iterable<Task>>binder((tbl, list) -> {
                        list.forEach(task -> {
                            tbl.tr()
                                .td().text(task.getTitle()).º()
                                .td().text(task.getDescription()).º()
                                .td().text(task.getPriority().toString()).td().text(task.getPriority().toString()).º()
                            .º(); // tr
                        });
                    })
                .º() // table
            .º() // div
        .º() // body
    .º(); // html

public class App {
    public static void main(String [] args)  {
        List<Task> model = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        String htmlTable = taskListView.render(model);
    }
}
```

## Changelog

### 2.1 (August, 2018)

HtmlFlow version 2.1 was updated to release 1.0.10 of `com.github.xmlet.HtmlApi` and
introduces a couple of new features:

* New `render()` which produces a `String` rather then writing to a `PrintStream`.
This `render()` uses internally a `StringBuilder` and shows better performance
than the `write()` approach.

* To allow `HtmlView` being a parent element of `Html` we made `HtmlView<T>` extend
`AbstractElement<HtmlView<T>, Element>` and turn it compatible with `Element`. 
Yet, it does not support `accept(ElementVisitor visitor)` nor `cloneElem()`.

* Fix `Html root` field definition in `HtmlView` to include the generic parent as `Html<HtmlView>`
and add it as child of that `HtmlView`.

* New static factory method `html()` used to start building a `HtmlView`.

* All these features together with the existing `º()` make possible to build a view in a single
pass, reducing the number of auxiliary variables capturing intermediate elements.
Now all the views of the examples of this `README.md` are built in static fields assignment.
More usage examples in [HtmlTables](src/test/java/htmlflow/test/HtmlTables.java) and
[HtmlLists](src/test/java/htmlflow/test/HtmlLists.java).

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

# HtmlFlow

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3Ahtmlflow)
[![Maven Central Version](https://maven-badges.herokuapp.com/maven-central/com.github.xmlet/htmlflow/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cxmlet%20htmlflow)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.xmlet%3Ahtmlflow&metric=Coverage)

**HtmlFlow** is a Java DSL to write typesafe HTML documents in a fluent style.
Use one of its `view()` factory methods to get started with HtmlFlow: 

```java
String html= StaticHtml
                .view()
                    .html()
                        .head()
                            .title().text("HtmlFlow").__()
                        .__() //head
                        .body()
                            .div().attrClass("container")
                                .h1().text("My first page with HtmlFlow").__()
                                .img().attrSrc("https://avatars1.githubusercontent.com/u/35267172").__()
                                .p().text("Typesafe is awesome! :-)").__()
                            .__()
                        .__() //body
                    .__() //html
                .render();
```

[Get started](#getting-started) or check out some more examples regarding [_dynamic views_](#dynamic-views)
built with the `htmlflow.DynamicHtml` support.
Under the unit tests package [htmlflow.test.views](src/test/java/htmlflow/test/views) you can also find several kinds of complex views with dynamic partial blocks.

## Installation

First, in order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>htmlflow</artifactId>
    <version>3.0</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/xmlet/htmlflow/)

## Getting started

All methods (such as `body()`, `div()`, `p()`, etc) return the created element,
except `text()` which returns its parent element (e.g. `.h1().text("...")` returns
the `H1` parent object).
The same applies to attribute methods - `attr<attribute name>()` - that also
return their parent (e.g. `.img().attrSrc("...")` returns the `Img` parent object).

There is also a special method `__()` which returns the parent element.
This method is responsible for emitting the end tag of an element.

The HTML resulting from HtmlFlow respects all HTML 5 rules (e.g. `h1().div()`
gives a compilation error because it goes against the content
allowed by `h1` according to HTML5). So, whenever you type `.` after an element
the intelissense will just suggest the set of allowed elements and attributes.

The HtmlFlow API is according to HTML5 and is generated with the support
of an automated framework ([xmlet](https://github.com/xmlet/)) based on an [XSD
definition of the HTML5](https://github.com/xmlet/HtmlApi/blob/master/src/main/resources/html_5.xsd)
syntax.

Thus, all attributes are strongly typed with enumerated types which restrict
the set of accepted values.
Finally, HtmlFlow also supports [_dynamic views_](#dynamic-views) with *data binders* that enable
the same HTML view to be bound with different object models.

## Output approaches

Consider the definition of the following view that is late rendered by the function 
passed to the `view()` method:

```java
static HtmlView view = StaticHtml.view(v -> v
            .html()
                .body()
                    .p().text("Typesafe is awesome! :-)").__()
                .__() //body
            .__()); // html
```

Thus you can get the resulting HTML in three different ways:
1) get the resulting `String` through its `render()` method or 
2) directly write to any `Printstream` such as `System.out` or 
3) any other `PrintStream` chain such as `new PrintStream(new FileOutputStream(path))`. 

**NOTE**: `PrintStream` is not buffered, so you may need to interleave a `BufferedOutputStream`
object to improve performance.
On the other hand `render()` internally uses a `StringBuilder` which shows better speedup.

```java
String html = view.render();        // 1) get a string with the HTML
view
    .setPrintStream(System.out)
    .write();                       // 2) print to the standard output
view
    .setPrintStream(new PrintStream(new FileOutputStream("details.html")))
    .write();                       // 3) write to details.html file

Desktop.getDesktop().browse(URI.create("details.html"));
```

Regardless the output approach you will get the same formatted HTML document:

```html
<!DOCTYPE html>
<html>
    <body>
        <p>
            Typesafe is awesome! :-)
        </p>
    </body>
</html>
``` 

## Dynamic Views

A _dynamic view_ is based on a template function `BiConsumer<DynamicHtml<U>, U>`, i.e.
a `void` function that receives a dynamic view (`DynamicHtml<U>`) and a domain object of type `U` -- 
`(DynamicHtml<U>, U) => void`.
Given the template function we can build a dynamic view through `DynamicHtml.view(templateFunction)`.

Next we present an example of a view with a template (e.g. `taskDetailsTemplate`) that will be later 
bound to a domain object `Task`.
Note the use of the method `dynamic()` inside the `taskDetailsTemplate` whenever we are
binding properties from the domain object `Task`.
This is a **mandatory issue** to enable dynamic bind of properties, otherwise those values are
cached and the domain object `Task` will be ignored on further renders. 

``` java
HtmlView<Task> view = DynamicHtml.view(HtmlLists::taskDetailsTemplate);

static void taskDetailsTemplate(DynamicHtml<Task> view, Task task) {
    view
        .html()
            .head()
                .title().text("Task Details").__()
            .__() //head
            .body()
                .dynamic(body -> body.text("Title:").text(task.getTitle()))
                .br().__()
                .dynamic(body -> body.text("Description:").text(task.getDescription()))
                .br().__()
                .dynamic(body -> body.text("Priority:").text(task.getPriority()))
            .__() //body
        .__(); // html
}
```

Next we present an example binding this same view with different domain objects,
producing different HTML documents.

``` java
List<Task> tasks = Arrays.asList(
    new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
    new Task(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
    new Task(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High)
);
for (Task task: tasks) {
    Path path = Paths.get("task" + task.getId() + ".html");
    Files.write(path, view.render(task).getBytes());
    Desktop.getDesktop().browse(path.toUri());
}
```

Finally, an example of a dynamic HTML table binding to a list of tasks:

``` java
static void tasksTableTemplate(DynamicHtml<Stream<Task>> view, Stream<Task> tasks) {
    view
        .html()
            .head()
                .title()
                    .text("Tasks Table")
                .__()
            .__()
            .body()
                .table()
                    .attrClass("table")
                    .tr()
                        .th().text("Title").__()
                        .th().text("Description").__()
                        .th().text("Priority").__()
                    .__()
                    .tbody()
                        .dynamic(tbody ->
                            tasks.forEach(task -> tbody
                                .tr()
                                    .td().dynamic(td -> td.text(task.getTitle())).__()
                                    .td().dynamic(td -> td.text(task.getDescription())).__()
                                    .td().dynamic(td -> td.text(task.getPriority().toString())).__()
                                .__() // tr
                            ) // forEach
                        ) // dynamic
                    .__() // tbody
                .__() // table
            .__() // body
        .__(); // html
}

static HtmlView<Stream<Task>> tasksTableView = DynamicHtml.view(HtmlForReadme::tasksTableTemplate);

public class App {
    public static void main(String [] args)  throws IOException {
        Stream<Task> tasks = Stream.of(
            new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new Task(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
            new Task(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High)
        );

        Path path = Paths.get("tasksTable.html");
        Files.write(path, tasksTableView.render(tasks).getBytes());
        Desktop.getDesktop().browse(path.toUri());
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

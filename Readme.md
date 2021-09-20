# HtmlFlow

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3Ahtmlflow)
[![Maven Central Version](https://maven-badges.herokuapp.com/maven-central/com.github.xmlet/htmlflow/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cxmlet%20htmlflow)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.xmlet%3Ahtmlflow&metric=Coverage)
[![Petclinic Sample](https://img.shields.io/badge/petclinic-Spring%20boot%20sample%20with%20HtmlFlow-blue)](https://github.com/xmlet/spring-petclinic)

[**HtmlFlow**](https://htmlflow.org/) is a Java DSL to write typesafe HTML
documents in a fluent style.
Use one of its `view()` factory methods to get started with HtmlFlow, such as the following sample,
which produces the HTML on the right side.
Use the utility `Flowifier.toFlow(url)` to get the HtmlFlow definition from the
corresponding HTML source:

<table class="table">
    <tr>
        <td colspan="2" align="center">
            <code>view.render()</code>&nbsp &#8628
        </td>
    </tr>
    <tr class="row">
        <td>

```java
HtmlView view = StaticHtml
  .view()
    .html()
      .head()
        .title().text("HtmlFlow").__()
      .__() //head
      .body()
        .div().attrClass("container")
          .h1().text("My first page with HtmlFlow").__()
          .img().attrSrc("http://bit.ly/2MoHwrU").__()
          .p().text("Typesafe is awesome! :-)").__()
        .__() //div
      .__() //body
    .__(); //html
```

</td>
<td>

```html



<html>
    <head>
        <title>HtmlFlow</title>
    </head>
    <body>
        <div class="container">
            <h1>My first page with HtmlFlow</h1>
            <img src="http://bit.ly/2MoHwrU">
            <p>Typesafe is awesome! :-)</p>
        </div>
    </body>
</html>

```

</td>
</tr>
    <tr>
        <td colspan="2" align="center">
            &#8632 &nbsp<code>Flowifier.toFlow(url)</code>
        </td>
    </tr>
</table>

Finally HtmlFlow is the **most performant** engine among state of the art template
engines like Velocity, Thymleaf, Mustache, etc and other DSL libraries for HTML such
as j2Html and KotlinX Html.
Check out the performance results in the most popular benchmarks at
[spring-comparing-template-engines](https://github.com/jreijn/spring-comparing-template-engines)
and our fork of
[xmlet/template-benchmark](https://github.com/xmlet/template-benchmark).

Check the implementation of the sample Spring-based petclinic with HtmlFlow views [xmlet/spring-petclinic](https://github.com/xmlet/spring-petclinic).
You can find different kinds of dynamic views [there](https://github.com/xmlet/spring-petclinic/tree/master/src/main/java/org/springframework/samples/petclinic/views).

## Why another templating engine ?

Every general purpose language has its own [_template engine_](https://en.wikipedia.org/wiki/Template_processor). 
Java has [several](https://en.wikipedia.org/wiki/Comparison_of_web_template_engines).
Most of the time, template engines have templates that are defined in new
_external DSL_.
To allow them to produce a _view_ based on the templates files, they generally use
the concept of [_model_](https://en.wikipedia.org/wiki/Data_model).

One of the problems of this technic is that you will end up with a template that
won't be type checked.
So if you have a typo inside your template, the compiler won't be able to help
you before the template is rendered.

HtmlFlow took a different approach. Templates are expressed in an _internal DSL_.
You will write normal Java code to produce your template. 
So the full Java tool chain is at your disposal for templating. 
Put it simply, HtmlFlow templates are essentially plain Java functions.

HtmlFlow is not the only one using this approach. But it's the fastest one.
Bonus points it also produces only valid HTML document according to HTML 5.2.

## Table of Contents

* [Installation](#installation)
* [Get started](#getting-started)
* [Output approaches](#output-approaches)
* [Dynamic Views](#dynamic-views)
* <a href="https://htmlflow.org/features/#partial-and-layout" target="_blank">Partial and Layouts</a>
* <a href="https://htmlflow.org/news/" target="_blank">Changelog</a>
* [References](#references)
* [License](#license)
* [About](#about)

## Installation

First, in order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>htmlflow</artifactId>
    <version>3.7</version>
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

The HTML resulting from HtmlFlow respects all HTML 5.2 rules (e.g. `h1().div()`
gives a compilation error because it goes against the content
allowed by `h1` according to HTML5.2). So, whenever you type `.` after an element
the intelissense will just suggest the set of allowed elements and attributes.

The HtmlFlow API is according to HTML5.2 and is generated with the support
of an automated framework ([xmlet](https://github.com/xmlet/)) based on an [XSD
definition of the HTML5.2](https://github.com/xmlet/HtmlApiFaster/blob/master/src/main/resources/html_5_2.xsd)
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

## References

* 2020, [Text Web Templates Considered Harmful](https://link.springer.com/chapter/10.1007/978-3-030-61750-9_4),
  Part of the Lecture Notes in Business Information Processing book series (LNBIP, volume 399).
  This paper shows how a DSL for HTML (such as HtmlFlow or Kotlinx.Html)
  provides unopinionated web templates with boundless resolving features only
  ruled by the host programming language (such as Java, Kotlin or JavaScript).
* 2019, [HoT: Unleash Web Views with Higher-order
  Templates](https://www.scitepress.org/Link.aspx?doi=10.5220/0008167701180129),
  [15th WebIst](http://www.webist.org/?y=2019) conference, 2019, Viena - This
  paper highlights the compositional nature of HtmlFlow to compose templates
  through higher-order functions. 
* 2018, [Domain Specific Language generation based on a XML
  Schema](https://www.slideshare.net/LuisDuarte105/domain-specific-language-generation-based-on-a-xml-schema-208756986).
  Slides of the MsC thesis presentation of Luís Duarte.
* 2018, [Modern Type-Safe Template
  Engines](https://dzone.com/articles/modern-type-safe-template-engines) - You
  can find more details in this DZone article about performance comparison.

## License

[MIT](https://github.com/xmlet/HtmlFlow/blob/master/LICENSE)

## About

HtmlFlow was created by [Miguel Gamboa](http://gamboa.pt/) (aka
[fmcarvalho](https://github.com/fmcarvalho/)), an assistant professor of
[Computer Science and
Engineering](https://www.isel.pt/en/courses/bsc-degree/computer-science-and-engineering)
of [ISEL](https://www.isel.pt/en/), [Polytechnic Institute of
Lisbon](https://www.ipl.pt/en).

---
title: "Version 3.x"
keywords: features version3
sidebar: htmlflow_sidebar
permalink: features_version3
---

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

<pre><code class="language-java">
static HtmlView view = StaticHtml.view(v -> v
            .html()
                .body()
                    .p().text("Typesafe is awesome! :-)").__()
                .__() //body
            .__()); // html
</code></pre>

Thus you can get the resulting HTML in three different ways:
1) get the resulting `String` through its `render()` method or
2) directly write to any `Printstream` such as `System.out` or
3) any other `PrintStream` chain such as `new PrintStream(new FileOutputStream(path))`.

**NOTE**: `PrintStream` is not buffered, so you may need to interleave a `BufferedOutputStream`
object to improve performance.
On the other hand `render()` internally uses a `StringBuilder` which shows better speedup.

<pre><code class="language-java">
String html = view.render();        // 1) get a string with the HTML
view
    .setPrintStream(System.out)
    .write();                       // 2) print to the standard output
view
    .setPrintStream(new PrintStream(new FileOutputStream("details.html")))
    .write();                       // 3) write to details.html file

Desktop.getDesktop().browse(URI.create("details.html"));
</code></pre>

Regardless the output approach you will get the same formatted HTML document:

<pre><code class="language-markup">
<!DOCTYPE html>
&lt;html>
    &lt;body>
        &lt;p>
            Typesafe is awesome! :-)
        &lt;/p>
    &lt;/body>
&lt;/html>
</code></pre>

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

<pre><code class="language-java">
HtmlView&lt;Task> view = DynamicHtml.view(HtmlLists::taskDetailsTemplate);

static void taskDetailsTemplate(DynamicHtml&lt;Task> view, Task task) {
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
</code></pre>

Next we present an example binding this same view with different domain objects,
producing different HTML documents.

<pre><code class="language-java">
List&lt;Task> tasks = Arrays.asList(
    new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
    new Task(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
    new Task(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High)
);
for (Task task: tasks) {
    Path path = Paths.get("task" + task.getId() + ".html");
    Files.write(path, view.render(task).getBytes());
    Desktop.getDesktop().browse(path.toUri());
}
</code></pre>

Finally, an example of a dynamic HTML table binding to a list of tasks:

<pre><code class="language-java">
static void tasksTableTemplate(DynamicHtml&lt;Stream&lt;Task>> view, Stream&lt;Task> tasks) {
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
                                    .td().of(td -> td.text(task.getTitle())).__()
                                    .td().of(td -> td.text(task.getDescription())).__()
                                    .td().of(td -> td.text(task.getPriority().toString())).__()
                                .__() // tr
                            ) // forEach
                        ) // dynamic
                    .__() // tbody
                .__() // table
            .__() // body
        .__(); // html
}

static HtmlView&lt;Stream&lt;Task>> tasksTableView = DynamicHtml.view(HtmlForReadme::tasksTableTemplate);

public class App {
    public static void main(String [] args)  throws IOException {
        Stream&lt;Task> tasks = Stream.of(
            new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new Task(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
            new Task(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High)
        );

        Path path = Paths.get("tasksTable.html");
        Files.write(path, tasksTableView.render(tasks).getBytes());
        Desktop.getDesktop().browse(path.toUri());
    }
}
</code></pre>

## Partial and Layout

HtmlFlow also enables the use of partial HTML blocks inside a template function.
This is useful whenever you want to reuse the same template with different HTML fragments.

The partial is constructed just like any template. Consider the following, which create a template, for a div containing a label and an input.

```java
public class InputField {
  public static class LabelValueModel {
        final String label;
        final String id;
        final Object value;

        private LabelValueModel(String label, String id, Object value) {
            this.label = label;
            this.id = id;
            this.value = value;
        }

        public static LabelValueModel of(String label, String id,  Object value) {
            return new LabelValueModel(label, id, value);
        }
    }

  public static HtmlView<LabelValueModel> view = DynamicHtml.view(InputField::template);

  static void template(DynamicHtml<LabelValueModel> view, LabelValueModel model) {
      view
          .div()
              .label()
              .dynamic(label -> label.text(model.label)).__() //label

              .input()
                  .dynamic(input -> input
                      .attrType(EnumTypeInputType.TEXT)
                      .attrId(model.id)
                      .attrName(model.id)
                      .attrValue(model.value.toString())
                  )
              .__()
          .__();
  }
}
```
Notice that we also introduce a model that is dedicated to this partial.
This will help us get data in the exact form needed to produce a complete, working and type-safe template.

This partial could be used inside another template.

```java
static void template(DynamicHtml<Pet> view, Pet pet) {
    view
      .div()
        .form().attrMethod(EnumMethodType.POST)
          .div().attrClass("form-group has-feedback")
            .dynamic(div -> view.addPartial(InputField.view, InputField.LabelValueModel.of("Date", "date", LocalDate.now())))
          .__() // div
        .__() // form
      .__() // div
}
```

This way of invoking partial is particularly useful when you need to use a smaller part (component) gathered together to produce a bigger template.
This is the most common usage of partials.

There is another way of using partials, it's to construct a layout. The layout is a normal template, but with a hole to be filed with partials.
Like we saw earlier, a partial has nothing special by itself. What is interesting is the layout, consider the following template.


```java
public class Layout {

    public static DynamicHtml<Object> view = (DynamicHtml<Object>) DynamicHtml.view(Layout::template).threadSafe();

    private static <T> void template(DynamicHtml<T> view, T model, HtmlView[] partials) {
        view
            .html()
                .head()
                    .meta().addAttr("http-equiv","Content-Type").attrContent("text/html; charset=UTF-8")
                    .__() //meta
                    .meta().attrCharset("utf-8")
                    .__() //meta
                    .meta().addAttr("http-equiv","X-UA-Compatible").attrContent("IE=edge")
                    .__() //meta
                    .meta().attrName("viewport").attrContent("width=device-width, initial-scale=1")
                    .__() //meta
                    .link().addAttr("rel","shortcut icon").addAttr("type","image/x-icon").attrHref("/resources/images/favicon.png")
                    .__() //link
                    .title()
                        .text("My awesome templating system")
                    .__() //title
                  .__() //head
                .body()
                    .nav().attrClass("navbar navbar-default").addAttr("role","navigation")
                      ..dynamic(__ -> view.addPartial(partials[0]) )
                    .__() //nav
                    .div().attrClass("container-fluid")
                        .div().attrClass("container xd-container")
                          .dynamic(__ ->  view.addPartial(partials[1], model) )
                        .__() //div
                    .__() //div
                .__() //body
            .__(); //html
    }
}
```

Notice the third argument to the function `template`, this array of partials is the place where we receive the partials to fill the holes of our layout.
To use them we called two distinct signatures of `view.addPartial`, one with only the partial, and one with a partial and a model. Depending on the type of
templated hidden behind `partials[0]`` we would use one signature or the other.

So we have defined our layout, let's use it to create a template with much less clutter.

```java
public class APage {

    public static DynamicHtml<Object> view = (DynamicHtml<Object>) DynamicHtml.view(Layout::template, Object model, new DynamicHtml<Object>[]{Menu::template, APage::template]).threadSafe();

    private static <T> void template(DynamicHtml<T> view, T model, HtmlView[] partials) {
        view
            .div().text("Type safety feel so cozy !!")
    }
}
```

Notice the member `view` which call the layout's template, and passing the menu and the page component as an array.


<p>&nbsp;</p>

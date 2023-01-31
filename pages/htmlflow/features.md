---
title: "Features"
keywords: features
sidebar: htmlflow_sidebar
permalink: features
---

<div class="row">
<div class="col-md-7">

{% highlight java %}
HtmlFlow
  .doc(System.out)
    .html() // HtmlPage
      .head()
        .title().text("HtmlFlow").__()
      .__() // head
      .body()
        .div().attrClass("container")
          .h1().text("My first HtmlFlow page").__()
          .img().attrSrc("http://bit.ly/2MoHwrU").__()
          .p().text("Typesafe is awesome! :-)").__()
        .__() // div
      .__() // body
    .__(); // html
{% endhighlight %}

</div>
<div class="col-md-5">

{% highlight html %}
<html>
  <head>
    <title>HtmlFlow</title>
  </head>
  <body>
    <div class="container">
      <h1>My first HtmlFlow page</h1>
      <img src="http://bit.ly/2MoHwrU">
      <p>Typesafe is awesome! :-)</p>
    </div>
  </body>
</html>
{% endhighlight %}

</div>
</div>

Beyond `doc()`, the `HtmlFlow` API also provides `view()` and `viewAsync()`, which build
an `HtmlPage` with a `render(model)` or `renderAsync(model)` methods depending of a model
(asynchronous for the latter).

## Getting started

All builders (such as `body()`, `div()`, `p()`, etc) return the created element,
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

When you build an `HtmlPage` with `HtmlFlow.doc(Appendable out)` you may use any kind of
output compatible with `Appendable`, such as `Writer`, `PrintStream`, `StringBuilder`, or other
(notice some streams, such as `PrintStream`, are not buffered and may degrade performance).

HTML is emitted as builder methods are invoked (e.g. `.body()`, `.div()`, `.p()`, etc).

However, if you build an `HtmlView` with `HtmlFlow.view(view -> view.html().head()...)`
the HTML is only emitted when you call `render(model)` or `write(model)` on the resulting `HtmlView`.
Then, you can get the resulting HTML in two different ways:

```java
HtmlView view = HtmlFlow.view(view -> view
    .html()
        .head()
            ....
);
String html = view.render();        // 1) get a string with the HTML
view
    .setOut(System.out)
    .write();                       // 2) print to the standard output
```

Regardless the output approach you will get the same formatted HTML document.

`HtmlView` does a preprocessing of the provided function (e.g. `view -> ...`) computing
and storing all static HTML blocks for future render calls, avoiding useless concatenation 
of text and HTML tags and improving performance.


## Dynamic Views

`HtmlView` is a subclass of `HtmlPage`, built from a template function specified by the functional interface:

```java
interface HtmlTemplate { void resolve(HtmlPage page); }
```

Next we present an example of a view with a template (e.g. `taskDetailsTemplate`) that will be later
bound to a domain object `Task`.
Notice the use of the method `dynamic()` inside the `taskDetailsTemplate` whenever we need to 
access the domain object `Task` (i.e. the _model_).
This model will be passed later to the view through its method `render(model)` or `write(model)`.

``` java
HtmlView view = HtmlFlow.view(HtmlLists::taskDetailsTemplate);

public static void taskDetailsTemplate(HtmlPage view) {
    view
        .html()
            .head()
                .title().text("Task Details").__()
            .__() //head
            .body()
                .<Task>dynamic((body, task) -> body.text("Title:").text(task.getTitle()))
                .br().__()
                .<Task>dynamic((body, task) -> body.text("Description:").text(task.getDescription()))
                .br().__()
                .<Task>dynamic((body, task) -> body.text("Priority:").text(task.getPriority()))
            .__() //body
        .__(); // html
}
```

Next we present an example binding this same view with 3 different domain objects,
producing 3 different HTML documents.

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

Finally, an example of a dynamic HTML table binding to a stream of tasks.
Notice, we  do not need any special templating feature to traverse the `Stream<Task>` and
we simply take advantage of Java Stream API.

``` java
static HtmlView tasksTableView = HtmlFlow.view(HtmlForReadme::tasksTableTemplate);

static void tasksTableTemplate(HtmlPage page) {
    page
        .html()
            .head()
                .title().text("Tasks Table").__()
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
                        .<Stream<Task>>dynamic((tbody, tasks) ->
                            tasks.forEach(task -> tbody
                                .tr()
                                    .td().text(task.getTitle()).__()
                                    .td().text(task.getDescription()).__()
                                    .td().text(task.getPriority().toString()).__()
                                .__() // tr
                            ) // forEach
                        ) // dynamic
                    .__() // tbody
                .__() // table
            .__() // body
        .__(); // html
}
```

## Asynchronous HTML Views

`HtmlViewAsync` is another subclass of `HtmPage` also depending of an `HtmlTemplate` function, 
which can be bind with both synchronous, or asynchronous models.

Notice that calling `renderAsync()` returns immediately, without blocking, while the `HtmlTemplate`
function is still processing, maybe awaiting for the asynchronous model completion.
Thus, `renderAsync()` and `writeAsync()` return `CompletableFuture<String>` and 
`CompletableFuture<Void>` allowing to follow up processing and completion.

To ensure well-formed HTML, the HtmlFlow needs to observe the asynchronous models completion. 
Otherwise, the text or HTML elements following an asynchronous model binding maybe emitted before 
the HTML resulting from the asynchronous model.

Thus, to bind an asynchronous model we should use the builder
`.await(parent, model, onCompletion) -> ...)` where the `onCompletion` callback is used to signal 
HtmFlow that can proceed to the next continuation, as presented in next sample:

```java
static HtmlViewAsync tasksTableViewAsync = HtmlFlow.viewAsync(HtmlForReadme::tasksTableTemplateAsync);

static void tasksTableTemplateAsync(HtmlPage page) {
    page
        .html()
            .head()
                .title() .text("Tasks Table") .__()
            .__()
            .body()
                .table().attrClass("table")
                    .tr()
                        .th().text("Title").__()
                        .th().text("Description").__()
                        .th().text("Priority").__()
                    .__()
                    .tbody()
                    .<Flux<Task>>await((tbody, tasks, onCompletion) -> tasks
                        .doOnNext(task -> tbody
                            .tr()
                                .td().text(task.getTitle()).__()
                                .td().text(task.getDescription()).__()
                                .td().text(task.getPriority().toString()).__()
                            .__() // tr
                        )
                        .doOnComplete(onCompletion::finish)
                        .subscribe()
                    )
                    .__() // tbody
                .__() // table
            .__() // body
        .__(); // html
}
```

In previous example, the model is a
[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html),
which is a Reactive Streams
[`Publisher`](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true) with rx operators that emits 0 to N elements.

HtmlFlow _await_ feature works regardless the type of asynchronous model and can be used with
any kind of asynchronous API.


## Layout and partial views (aka _fragments_)

HtmlFlow also enables the use of partial HTML blocks inside a template function, also know as _fragments_.
This is useful whenever you want to reuse the same template with different HTML fragments.

A fragment is constructed just like any template, that is a consumer of an HTML element.
Whereas a view template receives an `HtmlPage` corresponding the root element, a fragment template 
may receive any kind of HTML element.
But, instead of a specific functional interface `HtmlTemlate` used for views, a fragment 
is defined with the standard `Consumer<T>` where `T` is the type of the parent element.
For example, a fragment for a footer may be defined by a `Consumer<Footer>`.

The fragment template may receive additional arguments for auxiliary model objects (i.e. context objects).

Consider the following example from [Spring Petclinic](https://github.com/xmlet/spring-petclinic),
which creates a fragment for a `div` containing a `label` and an `input`.

```java
static void partialInputField(Div<?> container, String label, String id, Object value) {
    container
        .div().attrClass("form-group")
            .label()
                .text(label)
            .__() //label
            .input()
                .attrClass("form-control")
                .attrType(EnumTypeInputType.TEXT)
                .attrId(id)
                .attrName(id)
                .attrValue(value.toString())
            .__() // input
        .__(); // div
}
```

This partial could be used inside another template.

```java
static void ownerTemplate(Div<?> container) {
  container
    .h2()
      .text("Owner")
    .__() //h2
    .form()
      .attrMethod(EnumMethodType.POST)
      .div().attrClass("form-group has-feedback")
        .<Owner>dynamic((div, owner) -> partialInputField(div, "Name", "name", owner.getName()))
        .<Owner>dynamic((div, owner) -> partialInputField(div, "Address", "address", owner.getAddress()))
    ...
```

Notice, the function `ownerTemplate` is in turn another fragment that receives a `Div` element.

**Remember** when calling a fragment to use a `dynamic()` block to avoid storing it internally as a static HTML block and make it render whenever you call it with a model. Do not use the builder `of()` to include a dynamic fragment.

This way of invoking a fragment is particularly useful when you need to use a smaller part (component) gathered together to produce a bigger template.
This is the most common usage of partials or fragments.

There is another way of using fragments, it's to construct a _layout_. 
The layout is a normal template, but with "_holes_" or placeholders, to be filled with fragments.
These fragments are consumers (i.e. `Consumer<Element>`) received as arguments of the layout.

The layout function may instantiate an `HtmlView` based on a template with closures over
those fragments arguments.

For example, the following layout uses an auxiliary `navbar` and `content` fragments to build the end view. 

```java
public class Layout {
    public static HtmlView view(Consumer<Nav> navbar, Consumer<Div> content) {
        return HtmlFlow.view(page -> page
            .html()
                .head()
                    .title()
                        .text("PetClinic :: a Spring Framework demonstration")
                    .__() //title
                    .link().attrRel(EnumRelType.STYLESHEET).attrHref("/resources/css/petclinic.css")
                    .__() //link
                  .__() //head
                .body()
                    .nav()
                        .of(navbar::accept)
                    .__() //nav
                    .div().attrClass("container xd-container")
                        .of(content::accept)
                    .__() //div
                .__() //body
            .__() //html
        ); // return view
    }
}
```

To chain the call of fragments fluently we take advantage of the auxiliary HtmlFlow builder `of()` that let us chain a consumer of the last created element.
Notice `.of(navbar::accept)` is equivalent to write in Java `.of(nav -> navbar.accept(nav))`.

Once defined the layout and considering the previous example of the `ownerTemplate` we may build
a owner view with:

```java
public class OwnerView {

    static final HtmlView view = Layout
        .view(Navbar::navbarFragment, OwnerView::ownerTemplate);
...
}
```

Given this view we may render it with a `Owner` object.
For example, the `OwnerController` edit handler may be defined as:

```java
@GetMapping("/owners/{ownerId}/edit")
@ResponseBody
public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId) {
    Owner owner = ownersRepository.findById(ownerId);
    return OwnerView.view.render(owner);
}
```

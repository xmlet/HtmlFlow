---
title: "Features"
keywords: features
sidebar: htmlflow_sidebar
permalink: features
---


HtmlFlow is **unopinionated** and eliminates the need for a special templating
dialect. All control flow is executed through the host language (i.e., Java),
fluently chained with HtmlFlow blocks using the `of()` or `dynamic()` builders.

After introducing the core concepts of HtmlFlow, we present a couple of examples
demonstrating some of the most common usages with HtmlFlow. However, it's
important to **note** that there are **no limitations on the use of Java within HtmlFlow**.


## Core Concepts

HtmlFlow builders:
* element builders (such as `body()`, `div()`, `p()`, etc) return the **created element**
* `text()` returns its **parent element** (e.g. `.h1().text("...")` returns the `H1` parent).
* attribute builders - `attr<attribute name>()` - return their parent (e.g. `.img().attrSrc("...")` returns the `Img`).
* `__()` returns the **parent element** and **emits the end tag** of an element.

HtmlFlow provides both an **_eager_** and a **_lazy_** approach for building HTML.
This allows the `Appendable` to be provided either _beforehand_ or _later_ when
the view is rendered.
The `doc()` and `view()` factory methods follow each of these approaches:
<ul>
    <li>
{% highlight java %}/* eager */ HtmlFlow.doc(System.out).html().body().div().table()...{% endhighlight %}
    </li>
    <li>
{% highlight java %}/* lazy */ var view = HtmlFlow.view(page -> page.html().body().div().table()...){% endhighlight %}
    </li>
</ul>

An `HtmlView` is more **performant** than an `HtmlDoc` when we need to bind
the same template with different data **models**.
In this scenario, **static HTML blocks are resolved only once**, on `HtmlView` instantiation.

Given an `HtmlView` instance, e.g. `view`, we can render the HTML using one of the
following approaches:
<ul>
    <li>
{% highlight java %}String html = view.render(tracks){% endhighlight %}
    </li>
    <li>
{% highlight java %}view.setOut(System.out).write(tracks){% endhighlight %}
    </li>
</ul>

The `setOut()` method accepts any kind of `Appendable` object.

## Data Binding

_Web templates_ in HtmlFlow are defined using functions (or methods in Java). The
**model** (or _context object_) may be passed as arguments to such functions.
Next, we have an example of a dynamic web page binding to a `Track` object.

<div>	
<ul  class="nav nav-tabs">
    <li class="active">
        <a  href="#exBind01" data-toggle="tab">HtmlDoc</a>
    </li>
    <li>
        <a href="#exBind02" data-toggle="tab">HtmlView</a>
    </li>
</ul>
<div class="tab-content">
    <div class="tab-pane active" id="exBind01">

{% highlight java %}
void trackDoc(Appendable out, Track track) {
  HtmlFlow.doc(out)
    .html()
      .body()
        .ul()
          .li()
            .of((li) -> li
              .text(format("Artist: %s", track.getArtist())))
          .__() // li
          .li()
            .of((li) -> li
              .text(format("Track: %s", track.getName())))
          .__() // li
        .__() // ul
      .__() // body
    .__(); // html
}
...
trackDoc(System.out, new Track("David Bowie", "Space Oddity"));
{% endhighlight %}

    </div>
    <div class="tab-pane" id="exBind02">

{% highlight java %}
HtmlView<Track> trackView = HtmlFlow.view(view -> view
  .html()
    .body()
      .ul()
        .li()
          .<Track>dynamic((li, track) -> li
            .text(format("Artist: %s", track.getArtist())))
        .__() // li
        .li()
          .<Track>dynamic((li, track) -> li
            .text(format("Track: %s", track.getName())))
        .__() // li
      .__() // ul
    .__() // body
  .__() // html
);
...
trackView.setOut(System.out).write(new Track("David Bowie", "Space Oddity"));
{% endhighlight %}

    </div>
</div>
</div>

The `of()` and `dynamic()` builders in `HtmlDoc` and `HtmlView`, respectively,
are utilized to chain Java code in the definition of web templates:

* `of(Consumer<E> cons)` returns the same element `E`, where `E` is the parent HTML element. 
* `dynamic(BiConsumer<E, M> cons)` - similar to `.of()` but the consumer receives an additional argument `M` (model). 

## If/else

Regarding the previous template of `tracksDoc` or `tracksView`, consider, for
example, that you would like to display the **year of the artist's death** for cases
where the artist has already passed away.
Considering that `Track` has a property `diedDate` of type `LocalDate`, we can interleave
the following HtmlFlow snippet within the `ul` to achieve this purpose:


<div>	
<ul  class="nav nav-tabs">
    <li class="active">
        <a  href="#exIf01" data-toggle="tab">HtmlDoc</a>
    </li>
    <li>
        <a href="#exIf02" data-toggle="tab">HtmlView</a>
    </li>
</ul>
<div class="tab-content">
    <div class="tab-pane active" id="exIf01">

{% highlight java %}
void trackDoc(Appendable out, Track track) {
    ...
      .ul()
        ...
        .of(ul -> {
          if(track.getDiedDate() != null)
            ul.li().text(format("Died in %d", track.getDiedDate().getYear())).__();
        })

}
{% endhighlight %}

    </div>
    <div class="tab-pane" id="exIf02">

{% highlight java %}
HtmlView<Track> trackView = HtmlFlow.view(view -> view
    ...
      .ul()
        ...
        .<Track>dynamic((ul, track) -> {
          if(track.getDiedDate() != null)
            ul.li().text(format("Died in %d", track.getDiedDate().getYear())).__();
        })
        ...
{% endhighlight %}

    </div>
</div>
</div>


## Loops

You can utilize any Java loop statement in your web template definition. Next,
we present an example that takes advantage of the `forEach` loop method of
`Iterable`:

<div>	
<ul  class="nav nav-tabs">
    <li class="active">
        <a  href="#exLoop01" data-toggle="tab">HtmlDoc</a>
    </li>
    <li>
        <a href="#exLoop02" data-toggle="tab">HtmlView</a>
    </li>
</ul>
<div class="tab-content">
    <div class="tab-pane active" id="exLoop01">

{% highlight java %}
void playlistDoc(Appendable out, List<Track> tracks) {
  HtmlFlow.doc(out)
    .html()
      .body()
        .table()
          .tr()
            .th().text("Artist").__()
            .th().text("Track").__()
          .__() // tr
          .of(table -> tracks.forEach( trk ->
            table
              .tr()
                .td().text(trk.getArtist()).__()
                .td().text(trk.getName()).__()
              .__() // tr
          ))
        .__() // table
      .__() // body
    .__(); // html
}
{% endhighlight %}

    </div>
    <div class="tab-pane" id="exLoop02">

{% highlight java %}
HtmlView<List<Track>> playlistView = HtmlFlow.view(view -> view
  .html()
    .body()
      .table()
        .tr()
          .th().text("Artist").__()
          .th().text("Track").__()
        .__() // tr
        .<List<Track>>dynamic((table, tracks) -> tracks.forEach( trk ->
          table
            .tr()
              .td().text(trk.getArtist()).__()
              .td().text(trk.getName()).__()
            .__() // tr
          ))
      .__() // table
    .__() // body
  .__() // html
);
{% endhighlight %}

    </div>
</div>
</div>

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

## Fragments (aka Partials)

A **fragment** is a piece of HTML intended to be added to a web page.
In HtmFLow is simply a function receiving 

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

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
* `text()` and `raw()` return the **parent element** (e.g. `.h1().text("...")` returns the `H1` parent). `text()`escapes HTML, while `raw()` doesn't.
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
The resulting `HtmDoc` or `HtmlView` from `HtmlFlow.doc()` or `HtmlFlow.view()` is configurable with the following options:
* `setIndent(boolean)`: Enables or disables indentation. It is on by default.
* `threadSafe()`: Enables the use of the view in multi-threaded scenarios. It is off by default.

**Note** that `HtmDoc` and `HtmlView` are **immutable**, and the aforementioned methods return new instances.

## Data Binding

_Web templates_ in HtmlFlow are defined using functions (or methods in Java). The
**model** (or _context object_) may be passed as arguments to such functions.
Next, we have an example of a dynamic web page binding to a `Track` object.

{% include example01_bind.html %}

The `of()` and `dynamic()` builders in `HtmlDoc` and `HtmlView`, respectively,
are utilized to chain Java code in the definition of web templates:

* `of(Consumer<E> cons)` returns the same element `E`, where `E` is the parent HTML element. 
* `dynamic(BiConsumer<E, M> cons)` - similar to `.of()` but the consumer receives an additional argument `M` (model). 

## If/else

Regarding the previous template of `trackDoc` or `trackView`, consider, for
example, that you would like to display the **year of the artist's death** for cases
where the artist has already passed away.
Considering that `Track` has a property `diedDate` of type `LocalDate`, we can interleave
the following HtmlFlow snippet within the `ul` to achieve this purpose:

{% include example02_if_else.html %}

## Loops

You can utilize any Java loop statement in your web template definition. Next,
we present an example that takes advantage of the `forEach` loop method of
`Iterable`:

{% include example03_loops.html %}

## Binding to Asynchronous data models

To ensure well-formed HTML, HtmlFlow needs to observe the completion of
asynchronous models. Otherwise, text or HTML elements following an asynchronous
model binding may be emitted before the HTML resulting from the asynchronous
model.

To bind an asynchronous model, one should use the builder
`.await(parent, model, onCompletion) -> ...)`
where the `onCompletion` callback signals to HtmlFlow that it can proceed to the
next continuation.

Next we present the asynchronous version of the playlist web template.
Instead of a `List<Track>` we are binding to a [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html),
which is a Reactive Streams [`Publisher`](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true) with reactive operators that emits 0 to N elements.

{% include example04_async.html %}

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

{% include example05_partials01_partial.html %}

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

{% include example05_partials02_layout.html %}

To chain the call of fragments fluently we take advantage of the auxiliary HtmlFlow builder `of()` that let us chain a consumer of the last created element.
Notice `.of(navbar::accept)` is equivalent to write in Java `.of(nav -> navbar.accept(nav))`.

Once defined the layout and considering the previous example of the `ownerTemplate` we may build
a owner view with:

{% include example05_partials03_view.html %}
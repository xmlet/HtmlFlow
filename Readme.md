# HtmlFlow

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3Ahtmlflow)
[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.xmlet/htmlflow)](https://central.sonatype.com/artifact/com.github.xmlet/htmlflow)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3Ahtmlflow&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.xmlet%3Ahtmlflow&metric=Coverage)
[![javadoc HtmlApiFaster](https://img.shields.io/badge/javadocs-HtmlApiFaster-blue)](https://javadoc.io/doc/com.github.xmlet/htmlApiFaster/latest/org/xmlet/htmlapifaster/package-summary.html)
[![javadoc HtmlFlow](https://img.shields.io/badge/javadocs-HtmlFlow-blue)](https://javadoc.io/doc/com.github.xmlet/htmlflow)

[![Petclinic Sample](https://img.shields.io/badge/petclinic-Spring%20boot%20sample%20with%20HtmlFlow-blue)](https://github.com/xmlet/spring-petclinic)

[**HtmlFlow**](https://htmlflow.org/) is a Java DSL to write **typesafe HTML**
in a fluent style, in both **Java** or **Kotlin** (for Kotlin check the [examples](https://htmlflow.org/features#data-binding))
You may use the utility `Flowifier.fromHtml(String html)` if you need the HtmlFlow definition for an
existing HTML source:

<table class="table">
    <tr>
        <td colspan="2" align="center">
            <em>output</em>&nbsp &#8628
        </td>
    </tr>
    <tr class="row">
        <td>

```java
HtmlFlow
  .doc(System.out)
    .html() // HtmlPage
      .head()
        .title().text("HtmlFlow").__()
      .__() // head
      .body()
        .div().attrClass("container")
          .h1().text("My first page with HtmlFlow").__()
          .img().attrSrc("http://bit.ly/2MoHwrU").__()
          .p().text("Typesafe is awesome! :-)").__()
        .__() // div
      .__() // body
    .__(); // html
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
            &#8632 &nbsp<code>Flowifier.fromHtml("...")</code>
        </td>
    </tr>
</table>

**NOTICE** for those migrating from legacy API <= 3.x read next about
[Migrating from HtmlFlow 3.x to 4.x](https://htmlflow.org/2023-01-31-Release-4.0.html).

HtmlFlow is the **most performant** engine among state of the art template
engines like Velocity, Thymleaf, Mustache, etc and other DSL libraries for HTML such
as j2Html and KotlinX Html.
Check out the performance results in the most popular benchmarks at
[spring-comparing-template-engines](https://github.com/jreijn/spring-comparing-template-engines#benchmarks-102019)
and our fork of
[xmlet/template-benchmark](https://github.com/xmlet/template-benchmark).

[xmlet/spring-petclinic](https://github.com/xmlet/spring-petclinic) provides an implementation of the Spring-based
petclinic with HtmlFlow [views](https://github.com/xmlet/spring-petclinic/tree/master/src/main/java/org/springframework/samples/petclinic/views).

## Why another templating engine ?

Every general purpose language has its own [_template engine_](https://en.wikipedia.org/wiki/Template_processor). 
Java has [several](https://en.wikipedia.org/wiki/Comparison_of_web_template_engines).
Most of the time, templates are defined in a new templating language
(i.e. [_external DSL_](https://en.wikipedia.org/wiki/Domain-specific_language#External_and_Embedded_Domain_Specific_Languages)).
To allow template engines to produce a _view_ based on a template, they generally use
the concept of [_model_](https://en.wikipedia.org/wiki/Data_model).

One of the problems of this technic is that you will end up with a template that
won't be type checked.
So if you have a typo inside your template, the compiler won't be able to help
you before the template is rendered.

HtmlFlow took a different approach. Templates are expressed in an
[_internal DSL_](https://en.wikipedia.org/wiki/Domain-specific_language#External_and_Embedded_Domain_Specific_Languages).
You will write normal Java code to produce your template. 
So, the full Java toolchain is at your disposal for templating. 
Put it simply, HtmlFlow templates are essentially plain Java functions.

HtmlFlow is not the only one using this approach. But it's the fastest one.
Bonus points it also produces only valid HTML according to HTML 5.2.

## Table of Contents

* [Installation](#installation)
* [Core Concepts](#core-concepts)
* [Data Binding](#data-binding)
* [If/else](#ifelse)
* [Loops](#loops)
* [Binding to Asynchronous data models](#binding-to-asynchronous-data-models)
* [Layout and partial views (aka _fragments_)](https://htmlflow.org/features#layout-and-partial-views-aka-fragments)
* [Legacy API 3.x](https://htmlflow.org/features_version3)
* [Changelog](https://htmlflow.org/news)
* [References](#references)
* [License](#license)
* [About](#about)

## Installation

First, in order to include it to your Gradle project, simply add the following dependency,
or use any other form provided in [Maven Central Repository](https://search.maven.org/artifact/com.github.xmlet/htmlflow/4.7/jar):

```groovy
implementation 'com.github.xmlet:htmlflow:4.7'
```

You can also download the artifact directly from [Maven
Central Repository](https://repo1.maven.org/maven2/com/github/xmlet/htmlflow)

## Core Concepts

HtmlFlow builders:
* element builders (such as `body()`, `div()`, `p()`, etc) return the **created element**
* `text()` and `raw()` return the **parent element** (e.g. `.h1().text("...")` returns the `H1` parent). `.text()`escapes HTML, while `raw()` doesn't.
* attribute builders - `attr<attribute name>()` - return their parent (e.g. `.img().attrSrc("...")` returns the `Img`).
* `__()` or `l` in Kotlin, returns the **parent element** and **emits the end tag** of an element.

HtmlFlow provides both an **_eager_** and a **_lazy_** approach for building HTML.
This allows the `Appendable` to be provided either _beforehand_ or _later_ when
the view is rendered.
The `doc()` and `view()` factory methods follow each of these approaches:
* **eager**:
  ```java
  HtmlFlow.doc(System.out).html().body().h1().text("Welcome").__().table().tr()...
  ```
* **eager in Kotlin**:
  ```kotlin
  System.out.doc { html { body { h1.text("Welcome").l.table { tr {...} } } } }
  ```
* **lazy**:
  ```java
  var view = HtmlFlow.<Model>view(page -> page.html().body().text("Welcome").__().table().tr()...)
  ```
* **lazy in Kotlin**:
  ```kotlin
  val view = view<Model> { html { body { h1.text("Welcome").l.table { tr {...} } } } }
  ```

An `HtmlView` is more **performant** than an `HtmlDoc` when we need to bind
the same template with different data **models**.
In this scenario, **static HTML blocks are resolved only once**, on `HtmlView` instantiation.

Given an `HtmlView` instance, e.g. `view`, we can render the HTML using one of the
following approaches:
* `String html = view.render(tracks)`
* `view.setOut(System.out).write(tracks)`

The `setOut()` method accepts any kind of `Appendable` object.


In the following examples, we showcase using only `HtmlDoc`.
You can find the equivalent `HtmlView` definition on [htmlflow.org](https://htmlflow.org/features#data-binding) 

## Data Binding

_Web templates_ in HtmlFlow are defined using functions (or methods in Java). The
**model** (or _context object_) may be passed as arguments to such functions.
Next, we have an example of a dynamic web page binding to a `Track` object.

```java
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
```

[trackView](https://htmlflow.org/features#data-binding) equivalent definition to `trackDoc`. 

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

```java
void trackDoc(Appendable out, Track track) {
  ...
    .ul()
      ...
      .of(ul -> {
        if(track.getDiedDate() != null)
          ul.li().text(format("Died in %d", track.getDiedDate().getYear())).__();
      })
      ...
}
```

[trackView](https://htmlflow.org/features#ifelse) equivalent definition to `trackDoc`.

## Loops

You can utilize any Java loop statement in your web template definition. Next,
we present an example that takes advantage of the `forEach` loop method of
`Iterable`:

```java
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
```

[playlistView](https://htmlflow.org/features#loops) equivalent definition to `playlistDoc`.

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


```java
HtmlViewAsync<Flux<Track>> playlistView = HtmlFlow.viewAsync(view -> view
  .html()
    .body()
      .table()
        .tr()
          .th().text("Artist").__()
          .th().text("Track").__()
        .__() // tr
        .<Flux<Track>>await((table, tracks, onCompletion) -> tracks
          .doOnComplete(onCompletion::finish)
          .doOnNext( trk ->
            table
              .tr()
                .td().text(trk.getArtist()).__()
                .td().text(trk.getName()).__()
              .__()
        ))
      .__() // table
    .__() // body
  .__() // html
);
```

HtmlFlow _await_ feature works regardless the type of asynchronous model and can be used with
any kind of asynchronous API.

## References

* 2024, [Progressive Server-Side Rendering with Suspendable Web Templates](https://gamboa.pt/img/my-papers/wise-2024-pssr-async-await.pdf), 25th edition of [WISE](https://wise2024-qatar.com/), Doha, Qatar, ([slides](https://gamboa.pt/img/my-papers/wise-2024-slides.pdf)).
* 2023,
  [Enhancing SSR in Low-Thread Web Servers](https://www.scitepress.org/Link.aspx?doi=10.5220/0012165300003584),
  [19th WebIst](http://www.webist.org/?y=2023) conference, 2013, Rome - This paper highlights the HtmlFlow templating
  approach that embraces any asynchronous AP I (e.g., Publisher, promises,
  suspend functions, flow, etc.) and allows for multiple asynchronous data
  sources ([slides](https://gamboa.pt/img/my-papers/webist-2023-slides.pdf)).
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

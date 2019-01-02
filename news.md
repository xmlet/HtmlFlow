---
layout: default
title: "News"
description: "Change log and other updates"
---

##### Release 3.2 (Nov. 2018)

Make views immutable.

##### Release 3.1 (Nov. 2018)

Add support for non thread-safe views. Now, in order to use the same view by multiple threads you 
should call the `threadSafe()` method. Check the unit test 
[testDivDetailsBindingWithRender](src/test/java/htmlflow/test/TestDivDetails.java#L141)
that renders 4 different context models in parallel with the same view.  

##### Release 3.0 (Nov. 2018)

* Improved performance. HtmlFlow is on this date the most performant template engine and Java DSL for HTML.

* Replaced the method `º()` by `__()`.

* New static factory methods `view()` of the new classes `DynamicHtml` and `StaticHtml`

* Removed the static factory methods `html()`, `head()` and `div()` from `HtmlView`.

* Html code is emitted on the fly when the methods of `HtmlApi` (e.g. `html()`, `div()`, `h1()`, etc)
are called.

* Now `HtmlView` is just a container of a template function and an `ElementVisitor`, which establishes 
the HTML output format.

* All emitted HTML is cached.

* Data binding requires the use of new method `dynamic()` to avoid caching. Otherwise, the context objects are ignored on further renders.

* New method `of()` to enable the use of other methods in the fluent chain.

* New `addPartial()` to enable the reuse of same HTML template function with different HTML fragments.

* Removed the method `binder()`. The role of this method is replaced by the concept of template function which receives the context object `U` that is captured and used whenever is needed, such as in `dynamic()`.


##### Release 2.1 (Aug. 2018)

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

##### Release 2.0 (March 2018)

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

##### Release 1.2 (Sep. 2017)

* Refactor unit tests to increase code coverage and to load the expected HTML output from the resources.

* Include HtmlFlow in [SonarCloud.io](https://sonarcloud.io/dashboard?id=com.github.fmcarvalho%3Ahtmlflow) to analyze code quality.

* Fix of [Issue 14](https://github.com/xmlet/HtmlFlow/issues/24) -- _Header.txt can't be loaded from resources_.

##### Release 1.1 (March 2017)

Read HTML header from a template resource located in `templates/HtmlView-Header.txt`.
This template is bundled and loaded form the HtmlFlow JAR by default.
However, you are free to use your own header template file `templates/HtmlView-Header.txt`
and include its location in the classpath to replace the existing one.
This also solves the #16 Issue.

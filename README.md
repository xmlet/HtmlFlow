# HtmlFlow-Datastar

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xmlet_HtmlFlow-Datastar&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=xmlet_HtmlFlow-Datastar)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=xmlet_HtmlFlow-Datastar&metric=coverage)](https://sonarcloud.io/summary/new_code?id=xmlet_HtmlFlow-Datastar)
[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.xmlet/htmlflow-datastar?filter=!*alpha*)](https://central.sonatype.com/artifact/com.github.xmlet/htmlflow-datastar)
[![javadoc](https://javadoc.io/badge2/com.github.xmlet/htmlflow-datastar/1.2.0/javadoc.svg)](https://javadoc.io/doc/com.github.xmlet/htmlflow-datastar/1.2.0)
[![examples](https://img.shields.io/badge/examples-htmlflow--datastar--examples-blue?logo=github)](https://github.com/xmlet/HtmlFlow-Datastar-Examples)


Type-Safe Hypermedia-First DSL for Reactive Backend-Driven Web Applications

This repository contains the source code of the **HtmlFlow-Datastar** library - a Kotlin DSL for building type-safe, reactive web applications with [Data-Star](https://data-star.dev/). 

For complete examples and demo applications, please visit the [HtmlFlow-Datastar-Examples](https://github.com/xmlet/HtmlFlow-Datastar-Examples) repository.


HtmlFlow DSL also provides a type-safe way to define Datastar
attributes in Kotlin. Next is a sample of the Counter example
with a strongly typed signal and another one from Active Search using events and modifiers:


<table class="table">
<tr>
<td>


```kotlin
div {
  val count: Signal = dataSignal("count", 0)
  div {
    dataInit { get(::getCounterEvents) }
    span {
      attrId("counter")
      dataText{ +count }
    }
  }
}
```

</td>
<td>


```kotlin
div {
  attrId("demo")
  input {
    attrType(EnumTypeInputType.TEXT)
    attrPlaceholder("Search...")
    dataBind("search")
    dataOn(Input) {
      get(::search)
      modifiers { debounce(200.milliseconds) }
    }
  }
}
```

</td>
</tr>
<tr>
    <td>
        Note that the <code>count</code> variable is of type <code>Signal</code>
        and simply binds to the data-text in a type-safe way,
        regardless of the name passed to the <code>Signal</code> constructor.
    </td>
    <td>
        Modifiers such as <code>debounce</code> are added inside a lambda using 
        builders, following an idiomatic Kotlin style.
    </td>
</tr>
</table>

Also, HtmlFlow DSL supports strongly typed DataStar expressions,
allowing their composition with the infix operator `and`, 
such as in the expression `!fetching and get(::clickToLoadMore)`.
Note how the JavaScript expression for the `onclick` event handler (right side)
is expressed in Kotlin through HtmlFlow in a type-safe way:


<table>
<tr>
<td>
    
```kotlin
button {
  val fetching = dataIndicator("_fetching")
  dataAttr("disabled") { +fetching }
  dataOn(Click) {
      !fetching and get(::clickToLoadMore)
  }
  text("Load More")
}
```

</td>
<td>

```html
<button
  data-indicator:_fetching
  data-attr:disabled="$_fetching"
  data-on:click="!$_fetching && @get('/examples/click_to_load/more')"
>
    Load More
</button>
```

</td>
</tr>
</table>

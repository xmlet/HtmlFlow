---
title: Spring-based pet clinic app with HtmlFlow views
date: 2020-01-23
authors: [miguel]
tags: [tutorial]
description: spring-petclinic implementation of the sample Spring-based pet clinic web application integrated with HtmlFlow views, replacing Thymeleaf template engine.
---

We have provided a standard implementation of the sample Spring-based pet clinic web application integrated with HtmlFlow views at [github.com/xmlet/spring-petclinic](https://github.com/xmlet/spring-petclinic).

{/_ truncate _/}

:::info
In this implementation, the Thymeleaf template engine has been replaced with HtmlFlow.
:::

The project defines templates as first-class functions, leveraging Java language features while removing auxiliary Spring infrastructure components like `ModelAndView` and `ViewResolver`. Consequently, validations regarding these mechanics have been removed from the unit tests.

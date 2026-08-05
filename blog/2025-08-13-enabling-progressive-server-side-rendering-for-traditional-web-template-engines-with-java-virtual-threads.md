---
title: Enabling Progressive Server-Side Rendering for Traditional Web Template Engines with Java Virtual Threads
date: 2025-08-13
authors: [bernardo, miguel]
tags: [announcement]
description: HtmlFlow research publication on using Java virtual threads to enable progressive server-side rendering with traditional template engines.
---

We have published the paper [Enabling Progressive Server-Side Rendering for Traditional Web Template Engines with Java Virtual Threads](https://doi.org/10.3390/software4030020) in *Software*.

{/* truncate */}

This paper studies how Java virtual threads can simplify the implementation of progressive server-side rendering by enabling blocking template engines to operate effectively in non-blocking environments.

The article was published in *Software* 2025, 4(3), 20 on 13 August 2025.

## Abstract

Modern web applications increasingly demand rendering techniques that optimize performance, responsiveness, and scalability. Progressive Server-Side Rendering (PSSR) bridges the gap between Server-Side Rendering and Client-Side Rendering by progressively streaming HTML content, improving perceived load times. Still, traditional HTML template engines often rely on blocking interfaces that hinder their use in asynchronous, non-blocking contexts required for PSSR. This paper analyzes how Java virtual threads, introduced in Java 21, enable non-blocking execution of blocking I/O operations, allowing the reuse of traditional template engines for PSSR without complex asynchronous programming models. We benchmark multiple engines across Spring WebFlux, Spring MVC, and Quarkus using reactive, suspendable, and virtual thread-based approaches. Results show that virtual threads allow blocking engines to scale comparably to those designed for non-blocking I/O, achieving high throughput and responsiveness under load. This demonstrates that virtual threads provide a compelling path to simplify the implementation of PSSR with familiar HTML templates, significantly lowering the barrier to entry while maintaining performance.
---
title: Progressive Server-Side Rendering with Suspendable Web Templates
date: 2024-12-02
authors: [miguel]
tags: [announcement]
description: HtmlFlow research publication on progressive server-side rendering with suspendable web templates, published at WISE 2024.
---

We have published the paper [Progressive Server-Side Rendering with Suspendable Web Templates](https://doi.org/10.1007/978-981-96-0576-7_33) at WISE 2024.

{/* truncate */}

This work presents a proposal for server-side rendering web templates that support the async/await pattern while preserving well-formed HTML output and enabling progressive server-side rendering.

The paper appears in [Web Information Systems Engineering - WISE 2024: 25th International Conference, Doha, Qatar, December 2-5, 2024, Proceedings, Part V](https://dl.acm.org/doi/proceedings/10.1007/978-981-96-0576-7), pages 458-473.

## Abstract

*Progressive server-side rendering* (PSSR) enhances the user experience by optimizing the first contentful paint and supporting incremental rendering as data becomes available. However, constructing web templates for asynchronous data models introduces complexity due to undesired interleaving between data access completion and template processing, potentially resulting in malformed HTML. Existing asynchronous programming idioms, such as async/await or suspending functions, enable developers to create code with a synchronous appearance while maintaining non-blocking progress. In this work, we introduce the first proposal for *server-side rendering* (SSR) web templates that seamlessly support the async/await pattern. Our proposal addresses the challenges associated with building SSR web templates for asynchronous data models, ensuring the production of well-formed HTML documents and preserving the capability of PSSR.
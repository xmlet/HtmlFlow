---
title: BRHC - Backend-driven Reactive Hypermedia Controls with a Statically Typed Kotlin DSL
date: 2026-07-01
authors: [leonel, paulo, ricardo, miguel]
tags: [announcement]
description: HtmlFlow research publication on backend-driven reactive hypermedia controls with statically typed Kotlin DSL support.
---

New paper regarding HtmlFlow Kotlin DSL extension with statically typed support for reactive Datastar [BRHC: Backend-driven Reactive Hypermedia Controls with a Statically Typed Kotlin DSL](https://arxiv.org/abs/2607.29338).

{/* truncate */}

This work extends the HtmlFlow Kotlin DSL with statically typed support for reactive Datastar attributes and signal-based bindings, aiming to reduce frontend/backend heterogeneity while nearly eliminating the need for JavaScript.

The paper was submitted to *Software: Practice and Experience (Wiley)* and is available on arXiv at [arXiv:2607.29338](https://doi.org/10.48550/arXiv.2607.29338).

## Abstract

AI-assisted coding tools (e.g., Copilot, Cursor, Claude) are increasingly ubiquitous and enable rapid generation of web applications. However, this raises concerns regarding complexity, longevity and the long-term maintainability of generated systems. A key source of complexity is the heterogeneity between backend and frontend programming models, where multiple languages and paradigms are combined within a single application, often leading to duplicated logic and fragmented state management. To address this issue, recent approaches (e.g., HTMX, Turbo Hotwire, Datastar, etc.) follow the Hypermedia-Driven Application (HDA) model, positioning HTML as the primary communication medium between client and server. Unlike SPA-centric architectures, HDA systems shift the application state and interaction logic to the server, where backend-driven reactive signals synchronize with the client user interface. However, these approaches still introduce complexity through custom attributes and do not fully eliminate JavaScript, particularly in computed expressions. In this work, we propose a statically typed approach using a Kotlin-based HTML DSL (Domain-Specific Language) for backend-driven reactive web applications. We extend the HtmlFlow Kotlin DSL with typed custom HTML attributes (i.e., Datastar data-* attributes) and signal-based bindings using statically typed builders. We demonstrate the approach through a catalog of reactive interaction patterns and a Petclinic Spring MVC case study. The results indicate that the proposed approach can nearly eliminate the need for JavaScript while improving type safety and preserving a homogeneous programming model across frontend and backend, bridged through a backend-driven reactive, signal-centric architecture.
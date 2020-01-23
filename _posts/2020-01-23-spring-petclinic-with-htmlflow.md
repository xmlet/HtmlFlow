---
layout: post
title: Spring-based pet clinic app with HtmlFlow views
date: 2020-01-23
type: post
published: true
status: publish
categories: []
tags: []
author:
  login: MiguelGamboa
  email: 
  display_name: Miguel Gamboa
  
---

We have provided in [github.com/xmlet/spring-petclinic](https://github.com/xmlet/spring-petclinic)
a standard implementation of the sample Spring-based pet clinic web application integrated
with HtmlFlow views.

We have replaced the Thymeleaf template engine with HtmlFlow.

This project defines templates as first-class functions and exploit all the Java language features
and suppress auxiliary Spring infrastructures, such as `ModelAndView`, `ViewResolver` and others.
Thus, we removed from the unit tests all validations regarding these mechanics.

---
layout: default
title: "News"
description: "Change log and other updates"
---

{% for post in site.posts %}
<div class="post-preview">
    <a href="{{ post.url | prepend: site.baseurl }}">
        <h6 class="post-title">
            {{ post.title }} (<em>{{ post.date | date: "%B %-d, %Y" }}</em>)
        </h6>
    </a>
    <p class="lead">
        {{ post.content | strip_html | truncatewords:75}}
        <br>
        <a href="{{ post.url }}">Read more...</a>
    </p>
</div>
<hr>
{% endfor %}

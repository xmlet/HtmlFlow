# ADR 002: The existence of value property in Signal:

**Status:** Accepted

**Date:** 2026-04-20

## Context

DataStar signals have values, but, our DSL operates at the level of HTML
generation, not runtime execution. Therefore, the `Signal` class represents only
the structure of a signal (its name and how it's emitted).

Before, with the intention of supporting Kotlin expressions, there was a value
property, to form expressions such as:
```kotlin
val response = dataSignal("")
val answer = dataSignal("bread")
val correct = dataComputed { response.value.lowerCase() == answer.value }
```
But as the plugin was discarded, so was this necessity.

## Decision

The decision was to remove the property value of `Signal`.

## Trigger

No longer served any purpose as the plugin was discarded.

## Consequences

**Positive:**
- Removal of unnecessary property.

**Negative:**
- None.

**Neutral:**
- None.

## Alternatives considered

No other alternatives were considered.

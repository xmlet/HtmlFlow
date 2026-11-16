# ADR-006: Signal Casing Consistency

**Status:** Accepted

**Date:** 2026-05-07

## Context

Signal casing was inconsistent. For example, this DSL code:

```kotlin
div {
    val birthDate = dataSignals("birthDate")
    input {
        dataBind(birthDate)
    }
    p { dataText { +birthDate } }
}
```

generated this HTML:

```html
<div data-signals:birthdate="">
	<input data-bind:birthdate></input>
	<p data-text="$birthDate"></p>
</div> 
```

The DSL treated signal names with camelCase as the default case modifier, but
it would only produce a change when the name contained a separator. For
example, `birth-date` was automatically converted to `birthDate`, while
`birthDate` was left unchanged in expressions.

This created an inconsistency. Expressions used the signal name exactly as
passed to the DSL, such as `birthDate`, but the generated HTML attribute name
that created or bound the signal was converted to lowercase by the browser.
HTML attribute names are case-insensitive, so a signal encoded in an attribute
name cannot reliably preserve its original casing.

## Decision

Change how signal names are emitted in generated HTML. For attributes where the
signal name may contain meaningful casing, emit the signal name as part of the
attribute value instead of as part of the attribute name.

This affects:

- `dataBind`;
- `dataComputed`;
- `dataSignal`;
- `dataSignals`;
- `dataIndicator`;
- `dataRef`.

Instead of:

```html
<input data-bind:birthDate />
```

generate:

```html
<input data-bind="birthDate" />
```

## Trigger

An inconsistency was detected between signal names created in HTML attributes
and signal names referenced in expressions. This caused generated code to fail
when the casing did not match.

## Consequences

**Positive:**

- Signal name casing is consistent across signal creation, expression
  references, and HTTP payloads;
- Signal names are preserved exactly as provided to the DSL;
- The use of the DSL remains the same, with no changes required to existing code
  written using it.

**Negative:**

- Some generated HTML becomes less intuitive, especially the `dataComputed`
  attribute, where the value is an expression. Instead of:
  ```html
  <div data-computed:foo="$bar + $baz"></div>
  ```
  the DSL now generates:
  ```html
  <div data-computed="{foo: () => $bar + $baz}"></div>
  ```
  This is required because the signal name is now part of the value notation and
  the attribute receives a function. The Kotlin code stays the same, but the
  generated HTML becomes more complex;
- This stops using Datastar's attribute-suffix form for the affected cases. That
  form may be useful with some templating languages, but no relevant project use
  case was identified.

**Neutral:**

- No neutral changes.

## Alternatives Considered

### Signal Name Conversion Inside The DSL

Always converting received signal names to camelCase inside the DSL was
considered as a way to maintain consistency across calls. This was rejected
because it would add complexity and duplicate behavior that the Datastar engine
already provides.

It would also generate HTML where the signal name differs from the name used in
JavaScript expressions and HTTP payloads. Although functional, that result would
not provide the desired consistency in the generated code.

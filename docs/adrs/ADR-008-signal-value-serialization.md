# ADR-008: Signal Value Serialization

**Status:** Accepted

**Date:** 2026-10-17

## Context

Datastar signal initializers and assignments must be rendered as valid JavaScript
literals in HTML attributes. The previous serializer inspected Kotlin data
classes with reflection and formatted a small set of values itself. It did not
provide one reliable mechanism for nested collections, maps, arrays,
serialization annotations, explicit nulls, default values, and `setValue`
assignments.

The generated output cannot be strict JSON alone: Datastar attributes accept
JavaScript expressions, and the DSL must control string quoting, render valid
object keys, and allow an intentional JavaScript expression when needed. The
previous `Function0` escape hatch implicitly evaluated a function to inject raw
JavaScript, which made the boundary between data and executable code unclear.

## Decision

Centralize signal-value serialization in `serialization.Serializer`.

- Serialize nulls, strings, numbers, booleans, maps, collections, arrays, and
  Kotlinx JSON elements recursively as JavaScript literals.
- For other values, resolve a registered `kotlinx.serialization` serializer,
  encode the value to a `JsonElement`, and render that element as a JavaScript
  literal. Defaults and explicit nulls are retained.
- Use `JavaScriptLiterals` for consistent string escaping and object-key
  rendering. `QuoteStyle` controls the quote style used for string values.
- Use `RawJS` or `String.asRawJS()` as the explicit opt-in for a value that is
  already a JavaScript expression. Do not use `Function0` for this purpose.
- Route `dataSignal`, `dataSignals`, computed signal rendering, and
  `Signal.setValue` through the centralized serialization facilities.

## Trigger

Signal initialization and assignment needed consistent support for complex,
nested domains while preserving valid JavaScript syntax in generated Datastar
attributes.

## Consequences

**Positive:**

- Complex serializable domain values have consistent recursive rendering;
- Kotlinx serialization metadata, such as `@SerialName`, is respected;
- String escaping and JavaScript object-key formatting are centralized;
- Raw JavaScript is explicit at the call site rather than inferred from a
  function value;
- Signal initialization and `setValue` use the same serialization rules.

**Negative:**

- The project now depends on the Kotlinx serialization plugin and JSON runtime;
- Custom structured values need a registered Kotlinx serializer to receive
  structural serialization;
- `RawJS` bypasses escaping and serialization, so callers must provide trusted,
  valid JavaScript;
- Code that used `Function0` to inject raw JavaScript must migrate to
  `asRawJS()`.

**Neutral:**

- The generated representation remains a JavaScript literal rather than strict
  JSON, because it is consumed by Datastar expressions.

## Alternatives Considered

### Continue Reflection-Based Serialization

Keeping the previous reflection-based serializer would avoid the new dependency,
but it would continue to require custom handling for each value shape and would
not honor Kotlinx serialization metadata. It also would not provide a single
consistent path for nested values and signal assignments.

### Emit Strict JSON Directly

Encoding values directly as JSON was considered because Kotlinx serialization
can produce JSON efficiently. It was not chosen because Datastar attributes use
JavaScript expressions and need JavaScript-specific literal formatting, quote
style control, and an explicit raw-expression escape hatch.

### Keep `Function0` as the Raw JavaScript Escape Hatch

Retaining `Function0` would preserve the former API, but evaluating a function
while serializing obscures intent and can introduce unexpected side effects.
`RawJS` makes the opt-in visible and separates raw code from ordinary data.

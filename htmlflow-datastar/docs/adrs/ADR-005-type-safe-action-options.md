# ADR-005: Type-Safe Action Options

**Status:** Accepted

**Date:** 2026-05-07

## Context

Actions can receive Datastar options, such as `contentType`. Previously, these
options were passed to the action functions as optional raw strings.

```kotlin
get(::endpoint, "{contentType: 'form'}")
```

This allowed invalid option names and values to compile, provided no IDE
completion, and moved validation from the Kotlin DSL into the generated
Datastar expression. This conflicted with the DSL principle of exposing Datastar
behavior through type-safe Kotlin APIs.

## Decision

Make action options strongly typed. To achieve this, action functions receive a
lambda with receiver instead of an optional string value parameter.

```kotlin
get(::endpoint) { contentType = ContentType.FORM }
```

The lambda receiver is `ActionOptions`, so the parameter type is
`ActionOptions.() -> Unit`. This class exposes the supported action options as
mutable properties. In the example above, the `contentType` property is assigned
inside the lambda.

When an action is created, it creates an `ActionOptions` instance, applies the
lambda to it, and serializes the configured properties into the Datastar options
object.

## Trigger

Action options needed to be type-safe and aligned with the rest of the Kotlin
DSL.

## Consequences

**Positive:**

- Option names and values are checked by the compiler;
- IDE completion is available when configuring action options;
- Serialization of action options is centralized in `ActionOptions`.

**Negative:**

- Complexity added to action functions;
- New class added;
- Properties of the new class are mutable;
- This is a breaking API change if the raw string options parameter is removed.

**Neutral:**

- Instead of passing the options in a value parameter, they are now passed
  inside a lambda.

## Alternatives Considered

### Builder With Functions For Each Option

This alternative follows the design pattern of the other builders,
`ExpressionBuilder` and `ModifierBuilder`. Each option would be represented by a
function available inside the lambda, and each function would add that option to
an accumulator.

This would remove publicly mutable properties and keep the builder patterns
consistent. However, function calls would make it less explicit that each option
can only have one value. Multiple calls to the same option would replace the
previous value in the background, without that replacement being visible in the
DSL usage.

This would also increase implementation complexity because repeated calls would
need to be handled consistently. Additionally, Datastar action options are
represented as an object with properties, so mirroring that model in the DSL is
more explicit and closer to Datastar.

### Keep Raw String Options

Keeping the raw string options parameter would avoid an API change and keep the
action functions simple. However, it would preserve the original problem:
options would remain outside the Kotlin type system, invalid option names or
values could still compile, and users would not receive IDE completion when
configuring action options.

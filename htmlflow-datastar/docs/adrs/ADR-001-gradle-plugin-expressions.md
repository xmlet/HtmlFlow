# ADR-001: Gradle Plugin Expressions

**Status:** Rejected

**Date:** 2026-04-16

## Context

We want to support type-safe authoring of DataStar expressions in Kotlin, which
are ultimately evaluated as JavaScript expressions.

Initially, we explored a Gradle/compiler plugin approach that would allow
developers to write Kotlin code and automatically transpile it into equivalent
JavaScript strings.

During prototyping, several constraints became clear:

- Kotlin compiler plugins operate at file/module level, not expression-level
  extraction
- Extracting a single lambda as a JavaScript expression requires full code
  generation
- The compiler-embeddable approach generates standalone artifacts, introducing
  unnecessary overhead
- The solution would require deep coupling to Kotlin compiler internals,
  increasing maintenance cost

These issues made the plugin approach impractical for the project's goals.

## Decision

We will not use a Gradle/compiler plugin to transpile Kotlin expressions into
JavaScript.

Instead, we will adopt a constrained Kotlin DSL for defining DataStar
expressions, where expressions are constructed explicitly and mapped to
JavaScript strings without relying on compiler-level transformations.

## Trigger

While prototyping type-safe DataStar expressions, we needed to decide whether to
proceed with a Gradle/compiler plugin approach or adopt a simpler alternative.

The increasing complexity observed during early experimentation made this
decision necessary before further investment.

## Consequences

**Positive:**
- Eliminates dependency on Kotlin compiler internals (reduces breakage risk)
- Keeps build pipeline simple (no custom plugin lifecycle)
- Makes expression generation explicit and debuggable
- Reduces long-term maintenance cost

**Negative:**
- Loss of idiomatic Kotlin syntax (no direct use of stdlib/functions)
- No automatic operator mapping (==, !, etc.)
- Control flow must be modeled explicitly in DSL
- Less expressive than full Kotlin

**Neutral:**
- DSL introduces constraints but improves predictability and safety

## Alternatives Considered

No viable alternatives were identified that met the project constraints.

The Gradle plugin approach was the only explored solution for achieving
type-safe expression authoring via transpilation.
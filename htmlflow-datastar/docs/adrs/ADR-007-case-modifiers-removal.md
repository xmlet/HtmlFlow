# ADR-007: Case Modifiers Removal

**Status:** Accepted

**Date:** 2026-05-07

## Context

Case modifiers exist in Datastar as a way to control the casing of a signal. As
the signal naming was moved to the expression, where the casing is maintained as
is, case modifiers no longer have use.

## Decision

Remove Case Modifiers.

## Trigger

The change in approach to signal naming by the DSL made case modifiers no longer
have use.

## Consequences

**Positive:**

- Removal of a modifier.

**Negative:**

- Drift from the original Datastar design.

**Neutral:**

- no neutral changes.

## Alternatives Considered

No other alternatives were considered.

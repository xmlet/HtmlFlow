# ADR 2026-0001: Introduce a Value-Based HTML API as an Alternative to `dynamic()` / `dyn()`

**Status:** Proposed  
**Date:** 2026-10-12

## Table of Contents

- [Context](#context)
- [Decision](#decision)
  - [Example: Continuation Chain Representation](#example-continuation-chain-representation)
    - [Current Approach in HtmlFlow 5.0.4: `dynamic()` / `dyn()`](#current-approach-in-htmlflow-504-dynamic--dyn)
    - [Proposed Approach: Value-Based Continuations](#proposed-approach-value-based-continuations)
  - [Implementation Notes](#implementation-notes)
- [Consequences](#consequences)
- [Review History](#review-history)
  - [Review 1 — Renderer Ownership and Polymorphism](#review-1--renderer-ownership-and-polymorphism)

## Context

Currently, the `dynamic()` builder (also available as `dyn()`) creates an
`HtmlContinuationSyncDynamic` node in the continuation chain during
preprocessing.

Each `HtmlContinuationSyncDynamic` encapsulates a `BiConsumer<E, M>`,
where `E` represents the parent HTML element and `M` represents the
template's model.  
During rendering, this callback may perform several operations that introduce
**runtime overhead**:

1. Accessing model values through getter methods.
2. Repeatedly invoking fluent HTML builders to construct and serialize HTML
   elements, including string conversion and concatenation.

## Decision

Introduce a **new value-based HtmlFlow API** as an alternative to the existing
`dynamic()` / `dyn()` builders.

Instead of representing an entire dynamic block as a single
`HtmlContinuationSyncDynamic`, the new API allows preprocessing to
decompose it into **pre-encoded static HTML fragments interleaved with
`HtmlContinuationSyncValue` nodes**.  
Each `HtmlContinuationSyncValue` retains a model accessor responsible
for extracting a single dynamic value at rendering time.

The preprocessing phase (in `HtmlFlow.preprocessing()`) consists of two steps:

1. **Continuation chain construction** — Performed by `PreprocessingVisitor`,
   which builds the continuation chain by interleaving static HTML fragments
   with the new continuation types: `HtmlContinuationSyncValue`,
   `HtmlContinuationSyncWhen`, and `HtmlContinuationSyncForeach`.
2. **Bytecode compilation** — Performed by `ChainCompiler`, which attempts
   to compile eligible continuation chains into specialized JVM bytecode,
   replacing linked-list traversal with direct execution through a
   `Renderer` implementation. If compilation is not supported or fails,
   the original continuation chain remains available as a fallback.

### Example: Continuation Chain Representation

Consider a template that produces the following HTML:

```
<ul>
    <li>Alice</li>
    <li>1995-06-15</li>
</ul>
```

Here, `Alice` and `1995-06-15` are values obtained from `model.getName()` and
`model.getBirth()`, respectively.

### Proposal

<table>
<tr>
<th>

Current Approach in HtmlFlow 5.0.4: `dynamic()` / `dyn()`

</th>
<th>

**Proposed Approach**: 1st - Value-Based Continuations

</th>
<th>

**Proposed Approach**: 2nd - `HtmlContinuationSyncCompiled` compiled to JVM bytecode 

</th>
</tr>
<tr>
<td>

The outer `<ul>` tags are emitted by static continuations. The dynamic
continuation builds the list items and inserts the model values.

At every rendering execution, the callback traverses the fluent HTML builders
and serializes the corresponding HTML structure.

</td>
<td>

1st - During preprocessing the same HTML fragment is decomposed into:

</td>
<td>

2nd - During preprocessing the continuations chain is compiled into:

</td>
</tr>
<tr>
<td>

```text
                 ...
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncStatic         |
+--------------------------------------+
| "<ul>"                               |
+--------------------------------------+
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncDynamic        |
+--------------------------------------+
| BiConsumer<Element, User>            |
|                                      |
| - build <li>                         |
| - model.getName()                    |
| - build </li><li>                    |
| - model.getBirth()                   |
| - build </li>                        |
+--------------------------------------+
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncStatic         |
+--------------------------------------+
| "</ul>"                              |
+--------------------------------------+
                  |
                  v
                 ...
```

</td>
<td>

```text
                 ...
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncStatic         |
+--------------------------------------+
| "<ul><li>"                           |
+--------------------------------------+
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncValue          |
+--------------------------------------+
| User::getName                        |
+--------------------------------------+
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncStatic         |
+--------------------------------------+
| "</li><li>"                          |
+--------------------------------------+
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncValue          |
+--------------------------------------+
| User::getBirth                       |
+--------------------------------------+
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncStatic         |
+--------------------------------------+
| "</li></ul>"                         |
+--------------------------------------+
                  |
                  v
                 ...
```

</td>
<td>

```text
                 ...
                  |
                  v
+--------------------------------------+
| : HtmlContinuationSyncCompiled       |
+--------------------------------------+
| JVM bytecode                         |
+--------------------------------------+
                  |
                  v
                 ...
```

</td>
</tr>
</table>

### Proposed Approach: Value-Based Continuations

The static fragments are encoded only once during preprocessing. At rendering
time, only the value accessors are evaluated and their results are appended
between the pre-encoded HTML fragments.

Furthermore, this representation is eligible for optimization by
`ChainCompiler`, which can compile the continuation chain into specialized JVM
bytecode, eliminating linked-list traversal during rendering.

**Key principle:** The new API separates static HTML structure from
model-dependent values, enabling preprocessing to eliminate repeated
HTML serialization while preserving the existing continuation-based
rendering architecture.

The existing `dynamic()` / `dyn()` API remains available for dynamic
blocks that require arbitrary HTML construction or behavior that cannot
be expressed through value-based continuations.

### Implementation Notes

The `HtmlContinuationSyncCompiled` class represents a continuation whose
original chain has been compiled into specialized JVM bytecode by
`ChainCompiler`.  
It holds a `Renderer` instance backed by a dynamically generated JVM class
containing the compiled bytecode.  
This renderer directly emits pre-encoded static HTML fragments interleaved
with model-dependent values, replacing linked-list traversal with
compiled rendering execution.

```java
final class HtmlContinuationSyncCompiled extends HtmlContinuation {
    private final Renderer renderer;
    ...
```

```java
public interface Renderer {
    void render(StringBuilder sb, Object model);

    void write(Appendable out, Object model) throws IOException;
}
```

New field `compiled` in `HtmlContinuation` (possible duplication with inherited `renderer`):

```java
public abstract class HtmlContinuation {
    ...
    /** The compiled chain starting at this node, or null when it did not compile. */
    private Renderer compiled;
    ...
    public final void compile() {
        compiled = ChainCompiler.compile(this);
    }
    ...
    /**
     * Copies this node for a new visitor. The copy renders through the
     * generated {@link Renderer} when the chain compiled, and through
     * the linked chain otherwise.
     */
    public final HtmlContinuation compiledCopy(HtmlVisitor visitor) {
        return compiled == null
            ? copy(visitor)
            : new HtmlContinuationSyncCompiled(compiled, visitor);
    }
    ...
```

`HtmlViewVisitor` now calls `compiledCopy` instead of `copy`:

```java
public class HtmlViewVisitor extends HtmlVisitor {
  public HtmlViewVisitor(Appendable out, boolean isIndented, HtmlContinuation first) {
      super(out, isIndented);
      this.first = first.compiledCopy(this);
  }
```

Additional step in `preprocessing` of `HtmlFlow`:

```java
pre.getFirst().compile();
```

## Consequences

- Value-based continuations separate static HTML structure from
  model-dependent values, avoiding repeated serialization of static fragments.
- Eligible chains can be compiled to avoid linked-list traversal during
  rendering. Chains that cannot be compiled retain the original continuation
  chain as a fallback.
- The existing `dynamic()` / `dyn()` API remains available for behavior that
  cannot be expressed with value-based continuations.
- The proposed compilation design adds renderer ownership and dispatch concerns
  that remain unresolved in the review history below.

## Review History

This section tracks architectural review feedback, proposed changes, and
their resolution throughout this ADR lifecycle.

| Date | Reviewer | Review Focus | Status |
|---|---|---|---|
| 2026-10-12 | gamboa | Renderer ownership and polymorphism | Open |

### Review 1 — Renderer Ownership and Polymorphism

**Date:** 2026-10-12  
**Status:** Open — Requires further discussion.

**Review comments:**

1. **Potential duplication of renderer references:** Investigate the
   duplication between the `Renderer compiled` field in `HtmlContinuation`
   and the `Renderer renderer` field in `HtmlContinuationSyncCompiled`.

2. **Avoid explicit null checks:** The `if (compiled == null)` logic is
   undesirable because it introduces conditional branching to distinguish
   between compiled and non-compiled rendering.

3. **Prefer polymorphism:** Rendering behavior should be determined through
   polymorphism rather than explicit checks for a compiled renderer.

4. **Separate responsibilities:** Consider removing the `Renderer` reference
   from the base `HtmlContinuation` class and keeping compiled rendering
   exclusively within `HtmlContinuationSyncCompiled`.

**Suggested direction:**

Keep `HtmlContinuation` independent of bytecode compilation concerns.
Use separate continuation implementations for compiled and interpreted
rendering, allowing each to provide its own execution behavior through
the existing continuation abstraction.

Consider introducing a common base interface for `HtmlContinuation` and
`HtmlContinuationSyncCompiled`.  
Perhaps the existing `Renderer` interface could serve this purpose, but
I do not like its name.  
Following HtmlFlow's `Html` prefix convention, `HtmlResolver` might be
more consistent with the terminology established in its foundations.  

I also dislike having two methods, `render()` and `write()`, in `Renderer`
(or its potential replacement, `HtmlResolver`).
Since both perform similar operations with different argument types,
they could share the same name through method overloading.

I am also unsure whether `HtmlContinuationSyncCompiled` should be called
a *continuation*.
The term *continuation* reflects the rationale behind the existing
singly linked list of continuation nodes.
If I understand correctly, `HtmlContinuationSyncCompiled` performs all
rendering operations as a single unit, without chaining to another
continuation.
If so, `HtmlResolverCompiled` might be a more appropriate name.

**Potential common members of `HtmlResolver`:**

- `resolve(Object model)` — Replaces the existing `execute(Object model)`.
- `copy(...)` — Creates a copy bound to the appropriate rendering context.
- `compile()` — Is this necessary in the common interface, or could
  compilation be handled entirely during preprocessing?

**Outcome:** Pending architectural review and decision.
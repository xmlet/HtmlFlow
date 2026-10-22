# ADR 2022-0001: Preprocess Templates into Continuation Chains

**Status:** Implemented — retrospective record of the merged change.

**Date:** 2022-12-17

**Commit:** `018cc99d73022f9c9e9209925fcdf8c963a51427`

The merge of `preprocessing-template` into `development` was committed at
14:59:12 UTC on this date.
This ADR reconstructs the implemented change from that merge and its first
parent, `ee9a647`, rather than recording a new proposal.
Historical class names and behavior below refer to those revisions.

## Context: Before Preprocessing

Synchronous views already stored serialized static HTML before this change.
The static blocks were collected during the first render with a real model,
rather than during a separate template preprocessing phase.

`HtmlView.render(model)` invoked its binder on every call.
The overload accepting partial views invoked `template.resolve(...)` on every
call instead.
Both paths then called `HtmlVisitor.finished()`.
Consequently, storing static output did not eliminate repeated traversal of
the template's fluent HTML builders.

### What Was Stored

`HtmlViewVisitor` maintained a `List<HtmlBlockInfo>` named `staticBlocksList`.
Each entry contained:

- `html`: the serialized static fragment.
- `currentDepth`: the nesting depth at the fragment's boundary.
- `isClosed`: whether the current opening tag had been closed.

Dynamic HTML output was not stored in this list.
Dynamic code remained part of the binder or template execution and generated
fresh output for each model.
There was no synchronous continuation chain retaining dynamic callbacks.

### First Render: Collect and Store Static Blocks

1. The binder or template executed with the supplied model.
   `isStaticPartResolved` was initially false, so visitor operations emitted
   both static and dynamic HTML.
2. At `visitOpenDynamic()`, the visitor copied the preceding static substring
   into `staticBlocksList`, together with `depth` and `isClosed`.
3. The dynamic block emitted HTML for the current model.
4. At `visitCloseDynamic()`, `staticBlockIndex` advanced past that dynamic
   output, excluding it from the next stored substring.
5. `finished()` stored the trailing static fragment and marked the static
   part resolved.
   It then returned or finalized the output and reset the list read index.

For string output, fragments were extracted from the visitor's
`StringBuilder`.
For stream output, `PrintStringBuilder` initially both forwarded output to
the destination and collected it for substring extraction.
After the first render, the stream visitor wrote directly to its destination.

### Later Renders: Replay Static Fragments During Template Traversal

The binder or template still executed for each model.
The visitor used the following condition to suppress serialization outside
dynamic blocks once the static fragments had been stored:

```java
return !isStaticPartResolved || openDynamic;
```

At each `visitOpenDynamic()`, it wrote the next stored static fragment and
restored that entry's `currentDepth` and `isClosed`.
The dynamic block then emitted fresh HTML.
`visitCloseDynamic()` disabled dynamic output, and `finished()` wrote the
last stored static fragment.

The following diagram shows a simplified template with one dynamic block.
`S0` and `S1` are static fragments; `D(model)` is generated HTML.

```text
Before: first render

  render(model A)
       |
       v
  Execute binder/template: S0 -> dynamic(model A) -> S1
       |                          |
       |                          +--> D(A), not retained
       v
  Output: S0 + D(A) + S1
       |
       +--> staticBlocksList
              [0] { html: S0, currentDepth, isClosed }
              [1] { html: S1, currentDepth, isClosed }

Before: later render using the same visitor

  render(model B)
       |
       v
  Execute binder/template again
       |
       +--> Static builders: serialization suppressed
       +--> visitOpenDynamic(): write stored S0, restore state
       +--> Dynamic block: generate D(B)
       +--> visitCloseDynamic(): stop dynamic output
       +--> finished(): write stored S1
       |
       v
  Output: S0 + D(B) + S1
```

The list of static blocks belonged to the visitor.
A newly created visitor began with an empty list, including visitors created
for separate threads through the thread-safe view's supplier.

### Asynchronous Views Before the Merge

The old `HtmlVisitorAsync` used a separate mechanism and did not support the
synchronous mechanism for storing static blocks.
Its `isWriting()` always returned true, and operations used to collect static
blocks, such as
`size()` and `finished()` threw `UnsupportedOperationException`.
It coordinated asynchronous actions through `AsyncNode` objects retaining
actions, publishers, child suppliers for `then()`, and completion futures.
That action chain did not store static HTML fragments as described above.

## Decision Implemented by the Merge

Move template traversal into view creation and retain an executable chain
that interleaves static HTML fragments with dynamic callbacks.

`HtmlFlow.preprocessing(...)` created a `PreprocessingVisitor` and an
auxiliary `HtmlView`, invoked `template.resolve(preView)`, and finalized the
chain through `preView.getVisitor().finish(null)`.
The new `HtmlTemplate<T>.resolve(HtmlPage<T> page)` signature no longer
received the rendering model as a template argument.

For each dynamic block, `PreprocessingVisitor.visitDynamic(...)` created:

- An `HtmlContinuationStatic` holding the preceding serialized fragment.
- An `HtmlContinuationDynamic` holding the element, `BiConsumer<E, U>`,
  visitor, saved depth, and opening-tag state.

Finalization appended the trailing static continuation.
`HtmlViewVisitorContinuations` copied the chain and bound it to the rendering
visitor.
Its `finish(model)` executed `first.execute(model)` and collected the output.
Static nodes wrote their stored strings; dynamic nodes restored visitor
state and invoked their callbacks with the rendering model.
Each node then invoked the next node.

```text
After: view creation

  HtmlFlow.view(template, modelClass, ...)
       |
       v
  template.resolve(preView)
       |
       v
  PreprocessingVisitor -> finish(null)
       |
       v
  HtmlContinuationStatic       stores S0
       |
       v
  HtmlContinuationDynamic      stores callback + element + state
       |
       v
  HtmlContinuationStatic       stores S1

After: rendering

  render(model) -> visitor.finish(model) -> first.execute(model)
       |
       v
  Write S0 -> invoke stored callback(model) -> write S1
       |
       v
  Output: S0 + D(model) + S1
```

### Detail Specific to the Initial Implementation

At this merge, synchronous `PreprocessingVisitor.visitDynamic(...)` also
manufactured a model through Podam and invoked the dynamic callback during
preprocessing to establish the state preceding the next static fragment.
It advanced `staticBlockIndex` past the generated dynamic output so that
output was not retained as a static fragment.
This explains the `modelClass` and generic type arguments in the synchronous
view factories at that revision.
The callback was retained and invoked again with the actual rendering model.

The asynchronous preprocessor followed a different path.
`PreprocessingVisitorAsync.visitAwait(...)` retained the callback in an
`HtmlContinuationAsync` and added an `HtmlContinuationCloseAndIndent`.
It did not invoke the asynchronous callback during preprocessing.

## Consequences

- Static HTML remained stored for reuse, but collecting those blocks moved
  from the first render into view creation.
- Rendering no longer traversed the outer template's static builders.
  It executed the stored continuation chain instead.
- Dynamic callbacks became retained nodes in that chain.
  Their generated HTML still depended on the model and was not retained.
- Rendering visitors received copies of the chain bound to their own
  context, reusing stored static strings and callback references.
- The initial synchronous implementation required a manufactured model and
  executed dynamic callbacks during preprocessing as well as rendering.

## Relationship to HtmlFlow 5.0.4

This merge introduced the static-fragment and dynamic-callback continuation
architecture underlying the **Current Approach in HtmlFlow 5.0.4** described
in [ADR 2026-0001](2026-0001-model-slot-builder.md).
That later description uses `HtmlContinuationSyncStatic` and
`HtmlContinuationSyncDynamic`, rather than the historical names above.
This record does not attribute the initial Podam-based preprocessing behavior
or the 2022 factory signatures to HtmlFlow 5.0.4.
The value-based nodes and JVM bytecode compilation proposed in that later ADR
are not part of this 2022 decision.

## Historical Sources

The comparison uses `018cc99d^1` (`ee9a647`) for the preceding implementation
and `018cc99d` for the merged implementation.
All paths below are relative to `src/main/java/` at those revisions.
They can be inspected with `git show <revision>:src/main/java/<path>`.

Before the merge:

- `htmlflow/HtmlView.java`
- `htmlflow/visitor/HtmlViewVisitor.java`
- `htmlflow/visitor/HtmlViewVisitorStringBuilder.java`
- `htmlflow/visitor/HtmlViewVisitorPrintStream.java`
- `htmlflow/util/PrintStringBuilder.java`
- `htmlflow/visitor/HtmlVisitorAsync.java`
- `htmlflow/async/AsyncNode.java`

At the merge:

- `htmlflow/HtmlFlow.java`
- `htmlflow/HtmlTemplate.java`
- `htmlflow/HtmlView.java`
- `htmlflow/visitor/PreprocessingVisitor.java`
- `htmlflow/visitor/PreprocessingVisitorAsync.java`
- `htmlflow/visitor/HtmlViewVisitorContinuations.java`
- `htmlflow/visitor/HtmlContinuationStatic.java`
- `htmlflow/visitor/HtmlContinuationDynamic.java`

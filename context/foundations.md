# HtmlFlow Foundations

This document describes the fundamental abstractions, responsibilities, and
design principles of HtmlFlow (`htmlflow-core` module). Its purpose is to
provide coding agents with the conceptual knowledge required to understand,
maintain, and extend the library.

## 1. Core Abstractions in `htmlflow-core` module

HtmlFlow is built around a small set of abstractions that separate the
definition of an HTML document from the mechanisms responsible for rendering it.

The fundamental concepts are:

- **`HtmlTemplate`** — Defines the structure of an HTML document.
- **`HtmDoc`** - provides a convenient API for building HTML documents directly.
- **`HtmlView`** — the **container** that brings together an `HtmlTemplate` and
  a `HtmlVisitor` (extends `ElementVisitor`).
- **`ElementVisitor`** (of `xsd2poet-java` module) — Determines how visited HTML
  elements and their attributes are processed and **how output is produced**.
  All its methods are abstract. `HtmlVisitor` implements HTML output behavior.
- **`HtmlViewVisitor`** - extends `HtmlVisitor` and manages rendering
  configuration and continuation execution.
- **`HtmlContinuation`** - base class for a **singly linked list** of
  continuations, where each node is responsible for emitting an HTML block and
  invoking the next continuation.
- **`PreprocessingVisitor`** - **pre-encodes static HTML fragments** and builds a
  sequence of **continuations** that preserve dynamic blocks for later
  rendering.
- **`HtmlFlow.view(template)`** - creates a `PreprocessingVisitor`, which
  processes the template **once** and constructs the **continuation chain**. It then
  instantiates a `HtmlView` with a `Supplier<HtmlViewVisitor>`, rather than a
  visitor instance, because **visitors cannot be shared** across concurrent
  rendering executions.

```
+----------------------+
|       HtmlPage       |
+----------------------+
| + html()             |
| + div()              |
| + tbody()            |
| + ...                |
+----------------------+
           ^
           |
     +-----+----------------+
     |                      |
+------------------+  +---------------------------+      +------------------------------+
|     HtmlDoc      |  |   HtmlView<M>             |----->|         HtmlTemplate         |
+------------------+  +---------------------------+      |        <<interface>>         |
|                  |  | - template                |      +------------------------------+
+------------------+  | - visitor                 |      | resolve(page:HtmlPage): void |
                      +---------------------------+      +------------------------------+
                      | + write(M model): void    |
                      | + render(M model): String |
                      +---------------------------+
                              |
                              |                 +------------------------+          +--------------+ 
                              +---------------->|       HtmlVisitor      |-- out -->|  Appendable  |
                                                +------------------------+          +--------------+
                                                | resolve(model: Object) | 
                                                |                        |<- visitor -+
                                                +------------------------+            |  +-- next --+
                                                     ^                                |  |          |
                                                     |                                |  v          |
                                            +-----------------+          +------------------------+ |
                                            | HtmlViewVisitor |- first ->|    HtmlContinuation    |-+
                                            +-----------------+          +------------------------+
                                                                         | + execute(Object model)|
                                                                         +------------------------+
```

The diagram above highlights **three methods operating at different layers** of the
rendering pipeline, each with a distinct responsibility in **processing the model**:
- `HtmlContinuation` — `void execute(Object model)`
- `HtmlVisitor` — `void resolve(Object model)`
- `HtmlView` — `void write(M model)` does `getVisitor().resolve(model);`

A new visitor may be instantiated for each `HtmlView.write()` invocation, since
visitors **cannot be shared** across concurrent rendering executions.

`HtmlTemplate` is a functional interface representing a function that defines an
HTML document.  
Its primary responsibility is to describe the HTML structure using the fluent
API provided by `org.xmlet.htmlapifaster` package from the `xsd2poet-java`
module.

```java
@FunctionalInterface
public interface HtmlTemplate {
    void resolve(HtmlPage page);
}
```

**Key principle:** An `HtmlTemplate` defines *what HTML to produce*, **without**
determining *how that HTML is rendered or emitted*.

The `HtmlVisitor` is responsible for processing HTML elements and attributes as they
are **visited during template execution**.  
Different visitor implementations may provide different rendering strategies
without requiring changes to the template definition.

**Key principle:** The `ElementVisitor` determines *how HTML is processed and emitted*,
independently of the template that defines its structure.

The following excerpts from `HtmlViewVisitor` illustrate two crucial aspects of
its design.
1. creating a new visitor from an existing continuation chain involves **copying
   the chain** and binding its nodes to the new visitor instance. Each visitor
   owns its own `Appendable` output
2. resolving a visitor simply delegates execution to the first continuation,
   which subsequently invokes the remaining nodes in the chain.

```java
public class HtmlViewVisitor extends HtmlVisitor {
  public HtmlViewVisitor(Appendable out, boolean isIndented, HtmlContinuation first) {
      super(out, isIndented);
      this.first = first.copy(this);
  }
  @Override
  public final void resolve(Object model) {
      first.execute(model);
  }
  ...
```

### Preprocessing and Continuation Chain Construction

`PreprocessingVisitor` performs a preprocessing phase, more accurately
described as **pre-encoding**, of an `HtmlTemplate`.

It traverses the template to pre-encode static HTML fragments while
constructing a sequence of continuation nodes for subsequent rendering.

The `HtmlFlow.view(template)` factory method builds the continuation chain
through a sequence of **awkward steps**, invoking operations at different
layers of the HtmlFlow architecture.

The process can be summarized as follows:

```java
PreprocessingVisitor pre = new PreprocessingVisitor(isIndented);
HtmlView<?> preView = new HtmlView<>(() -> pre, template, false); // 1.
template.resolve(preView);                                        // 2.
preView.getVisitor().resolve(null);                               // 3.
return new HtmlView<>(                                            // 4.
    () -> new HtmlViewVisitor(..., pre.getFirst()),
    ...
);
```

Although `resolve()` is invoked twice, first on `HtmlTemplate` and then on
`PreprocessingVisitor`, **these calls serve entirely different purposes**.
This distinction arises because `PreprocessingVisitor` repurposes the
semantics of `resolve()` to finalize the continuation chain rather than
perform HTML rendering.

The preprocessing sequence involves the following steps:

1. **Create an auxiliary `HtmlView`.**
   A temporary `HtmlView` is created with a `PreprocessingVisitor` that
   inherits HTML serialization behavior from `HtmlVisitor` and redirects
   output to an internal `StringBuilder`.
   This auxiliary view provides the root context required by the template.

2. **Traverse the template and Construct the continuation chain.** 
   Calling `template.resolve(preView)` executes the template and visits its HTML
   elements.
   * static HTML is serialized into the internal `StringBuilder`, while dynamic
   blocks are intercepted by `PreprocessingVisitor.visitDynamic()`.
   * each invocation of `visitDynamic()` extracts the accumulated static HTML
   into an `HtmlContinuationSyncStatic` node and appends it to the chain.  It
   then appends an `HtmlContinuationSyncDynamic` node containing the callback
   responsible for binding the model during rendering.

3. **Finalize the continuation chain.**
   After template traversal, the remaining static HTML must be appended
   to the chain.
   This is achieved by calling `preView.getVisitor().resolve(null)`.
   Unlike normal visitor resolution, `PreprocessingVisitor.resolve()`
   simply creates the final `HtmlContinuationSyncStatic` node, even
   when the remaining static HTML is empty.

4. **Create the reusable `HtmlView`.**
   Once preprocessing is complete, `HtmlFlow.view()` creates the final
   `HtmlView`, providing a supplier of `HtmlViewVisitor` instances
   configured with the first continuation from `pre.getFirst()`.

**Note** that other types of dynamic continuations, including asynchronous and
suspending variants, handle different kinds of dynamic blocks and are chained in
the same manner as `HtmlContinuationSyncDynamic`.

**Preprocessing occurs only once**, when the `HtmlView` is created.
Subsequent calls to `HtmlView.write(T model)` obtain a new
`HtmlViewVisitor`, which copies the entire continuation chain and binds
each copied node to the new visitor instance.

The copied nodes reuse references to the pre-encoded static HTML strings
and dynamic callbacks, avoiding repeated template traversal and static
HTML serialization.

**The key architectural concern is the overloaded meaning of
`resolve()`.**
While `HtmlTemplate.resolve()` traverses the HTML structure,
`PreprocessingVisitor.resolve()` finalizes the continuation chain,
and `HtmlViewVisitor.resolve()` executes that chain.
These operations have distinct responsibilities despite sharing the
same method name, making the preprocessing workflow less intuitive
and potentially misleading during maintenance or refactoring.

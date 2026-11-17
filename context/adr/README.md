# Architecture Decision Records

This directory is the shared home for ADRs across HtmlFlow and all its
subprojects, including decisions that span multiple modules.
Each record preserves the context, decision, consequences, date, and status of
an architectural choice.
Proposed records are not accepted requirements.

## Naming Convention

Use `YYYY-NNNN-<subproject>-<topic>.md` for subproject-specific records.
The subproject identifier makes the scope explicit, for example `datastar`.
Core and repository-wide records use `YYYY-NNNN-<topic>.md`.

- `YYYY` is the year recorded in the ADR's date, not the migration year.
- `NNNN` is a four-digit identifier within that year.
- Use `0001`–`0999` for core and repository-wide records.
- Use `1001`–`1999` for Datastar records.
- Allocate and document a separate, non-overlapping range for each additional
  subproject when its first ADR is added.
- Allocate the next unused identifier in the relevant year and range.
- Use lowercase, hyphen-separated topics.
- Match the heading to the identifier: `# ADR YYYY-NNNN: Title`.

For example: `2026-1001-datastar-gradle-plugin-expressions.md`.

## Index

### HtmlFlow Core 

| ADR | Status | Summary |
| --- | --- | --- |
| [2022-0001][c1] | Implemented | Preprocess templates into continuation chains. |
| [2026-0001][c2] | Proposed | Introduce a value-based HTML API. |

[c1]: 2022-0001-template-preprocessing.md
[c2]: 2026-0001-model-slot-builder.md

### Datastar

| ADR | Status | Summary |
| --- | --- | --- |
| [2026-1001][d1] | Rejected | Gradle plugin for expression transpilation. |
| [2026-1002][d2] | Accepted | Register expressions inside ExpressionBuilder. |
| [2026-1003][d3] | Accepted | Remove the value property of Signal. |
| [2026-1004][d4] | Accepted | Make DataStarExpression a sealed hierarchy. |
| [2026-1005][d5] | Accepted | Use type-safe action options. |
| [2026-1006][d6] | Accepted | Preserve signal casing in attribute values. |
| [2026-1007][d7] | Accepted | Remove case modifiers. |
| [2026-1008][d8] | Accepted | Centralize signal-value serialization. |

[d1]: 2026-1001-datastar-gradle-plugin-expressions.md
[d2]: 2026-1002-datastar-expression-registration.md
[d3]: 2026-1003-datastar-signal-value.md
[d4]: 2026-1004-datastar-expression-restructure.md
[d5]: 2026-1005-datastar-type-safe-action-options.md
[d6]: 2026-1006-datastar-signal-casing-consistency.md
[d7]: 2026-1007-datastar-case-modifiers-removal.md
[d8]: 2026-1008-datastar-signal-value-serialization.md

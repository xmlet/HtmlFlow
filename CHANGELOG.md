# Changelog

## v1.2.0

### Added
- Support for serializing complex signal values through registered `kotlinx.serialization` serializers, including nested collections, maps, arrays, defaults, and explicit nulls.
- `RawJS` and `asRawJS()` for explicitly embedding a JavaScript expression in a serialized value.

### Changed
- Replaced reflection-based signal value serialization with a centralized serializer for signal initialization, computed signals, and `setValue` assignments.
- Added `QuoteStyle` support for selecting the quote style used for serialized string values.

### Removed
- Implicit raw JavaScript injection through `Function0`; use `asRawJS()` instead.

## v1.1.0

### Changed
- Prevent nested modifier builder blocks by enforcing DSL scope restrictions at compile time.

## v1.0.0

### Changed
- First stable release of HtmlFlow-Datastar.
- No functional changes since `v1.3.0-alpha`.

## v1.3.0-alpha

### Added
- Type-safe builder support for DataStar action options
- Architecture Decision Records (ADRs) for project documentation
- Expression precedence and grouping support in the Expression Builder

### Changed
- Refactored internal helper functions related to attribute value serialization

### Removed
- Support for signal name case modifiers
- Unary plus (`+`) operator support for `Function<*>` in the Expression Builder

## v1.2.0-alpha

### Added or Changed
- Support for Signals with complex domain
- Enhanced documentation with more examples and explanations

### Removed
- Value property from Signal

## v1.1.0-alpha.1

### Added or Changed
- Javadoc documentation


## v1.1.0-alpha

### Added or Changed
- Refactor ExpressionBuilder and views to it
- New infix operators added and overloaded for more types
- Fragmented Tests to reduce compile time
- Refactor documentation to be more clear and concise

## v1.0.0-alpha

### Added or Changed
- Initial release of the DSL, a Kotlin Type-Safe DSL for Backend-Driven Web Applications with Hypermedia Controls
- Introduced core concepts such as Signals, DataStarExpressions, and DataStarActions
- Implemented basic operators and functions for working with Signals and DataStarExpressions
- Provided comprehensive documentation and examples to demonstrate the usage of the DSL

### Removed

- Gradle plugin for DataStar expression transpilation (rejected in ADR-001).

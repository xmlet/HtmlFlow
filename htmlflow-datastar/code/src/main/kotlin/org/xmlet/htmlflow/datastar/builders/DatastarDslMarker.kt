package org.xmlet.htmlflow.datastar.builders

/**
 * Marker annotation for DSL scopes that prevents nested builder calls.
 * This prevents unwanted implicit receivers in nested lambda blocks.
 */
@DslMarker
annotation class DatastarDslMarker

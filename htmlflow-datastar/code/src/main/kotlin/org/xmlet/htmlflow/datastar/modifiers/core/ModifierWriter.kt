package org.xmlet.htmlflow.datastar.modifiers.core

/**
 * An interface for accumulating modifier strings during DSL configuration.
 *
 * Implementations provide the ability to add modifiers that will be combined into a final string representation.
 */
interface ModifierWriter {
    /**
     * Adds a modifier string to the accumulator.
     *
     * @param mod the modifier string to add
     */
    fun append(mod: String)
}

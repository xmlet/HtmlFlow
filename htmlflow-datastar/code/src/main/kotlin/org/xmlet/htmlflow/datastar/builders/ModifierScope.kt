package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

/**
 * Defines a scope for building modifiers in a fluent API style. Implementations of this interface
 * should provide a way to accumulate modifier strings based on the provided block of code.
 */
interface ModifierScope<M : ModifierAccumulator> {
    fun getModifiers(): String

    fun modifiers(block: M.() -> Unit)
}

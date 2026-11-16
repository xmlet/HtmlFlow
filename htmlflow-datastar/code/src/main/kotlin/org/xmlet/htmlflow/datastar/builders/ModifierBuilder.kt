package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

/**
 * A concrete implementation of [ModifierBuilder] that accumulates modifier strings
 * by delegating to a [ModifierAccumulator].
 *
 * **Example usage:**
 * ```kotlin
 * modifiers {
 *     case(CaseStyle.PASCAL)
 * }
 * ```
 *
 * @param M the type of [ModifierAccumulator] used to accumulate modifiers
 * @param builder [ModifierAccumulator] to apply lambda received
 */
class ModifierBuilder<M : ModifierAccumulator>(
    private val builder: M,
) : ModifierScope<M> {
    override fun modifiers(block: M.() -> Unit) {
        builder.apply(block)
    }

    override fun getModifiers() = builder.modifiers
}

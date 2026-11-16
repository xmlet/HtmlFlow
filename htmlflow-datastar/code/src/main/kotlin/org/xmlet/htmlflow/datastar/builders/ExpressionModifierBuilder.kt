package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

/**
 * A composite builder combining [ExpressionBuilder] and [ModifierBuilder] for building
 * both DataStar expressions and modifiers in a single fluent API.
 *
 * **Example usage:**
 * ```kotlin
 * signal1 and action1
 * modifiers {
 *     case(CaseStyle.PASCAL)
 * }
 * ```
 * @param M the type of [ModifierAccumulator] used to accumulate modifiers
 * @param builder the [ModifierBuilder] instance used to build modifiers alongside expressions
 * @property expressionBuilder delegates [ExpressionScope] to [ExpressionBuilder]
 * @property modifierBuilder delegates [ModifierScope] to [ModifierBuilder]
 */
class ExpressionModifierBuilder<M : ModifierAccumulator>(
    builder: M,
    private val expressionBuilder: ExpressionBuilder = ExpressionBuilder(),
    private val modifierBuilder: ModifierBuilder<M> = ModifierBuilder(builder),
) : ExpressionModifierScope<M>,
    ExpressionScope by expressionBuilder,
    ModifierScope<M> by modifierBuilder

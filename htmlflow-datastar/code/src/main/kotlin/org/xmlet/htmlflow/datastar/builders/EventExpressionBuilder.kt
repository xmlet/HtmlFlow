package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnModifiers

/**
 * An event-aware builder extending [ExpressionModifierBuilder] with access to a triggering [Event].
 *
 * Used within `data-on` event handler lambdas to build expressions and modifiers while accessing
 * event-specific data through the [evt] property.
 *
 * **Example usage:**
 * ```kotlin
 * dataOn(click) {
 *     signal1.setValue(evt.detail) and action1
 *     modifiers {
 *         debounce(300.milliseconds)
 *     }
 * }
 * ```
 *
 * @param EVT the type of [Event] available in this builder context
 * @property evt the triggering event, providing access to event data and properties
 * @property expressionModifierBuilder delegates [ExpressionModifierScope] to [ExpressionModifierBuilder]
 */
class EventExpressionBuilder<EVT : Event>(
    val evt: EVT,
    private val expressionModifierBuilder: ExpressionModifierBuilder<DataOnModifiers> = ExpressionModifierBuilder(DataOnModifiers()),
) : ExpressionModifierScope<DataOnModifiers> by expressionModifierBuilder

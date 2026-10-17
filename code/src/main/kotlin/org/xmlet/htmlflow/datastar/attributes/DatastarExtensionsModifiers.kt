package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SelectAll
import org.xmlet.htmlapifaster.TextGroup
import org.xmlet.htmlflow.datastar.builders.EventExpressionBuilder
import org.xmlet.htmlflow.datastar.builders.ExpressionModifierBuilder
import org.xmlet.htmlflow.datastar.builders.ModifierBuilder
import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import org.xmlet.htmlflow.datastar.expressions.signal
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataBindModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataIgnoreModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataInitModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataJsonSignalsModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnIntersectModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnIntervalModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnSignalPatchModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataSignalsModifiers
import org.xmlet.htmlflow.datastar.serialization.JavaScriptLiterals
import org.xmlet.htmlflow.datastar.serialization.QuoteStyle
import org.xmlet.htmlflow.datastar.serialization.Serializer
import org.xmlet.htmlflow.datastar.serialization.Serializer.serializeSignals

/**
 * Initializes one or more signals with their initial values and modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signals attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @param block configuration lambda for (signal) modifiers
 * @return a list of Signal instances with the given names
 */
fun <E : Element<*, *>, P : Element<*, *>, T> Element<E, P>.dataSignals(
    vararg signals: Pair<String, T>,
    quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    block: ModifierBuilder<DataSignalsModifiers>.() -> Unit = {},
): List<Signal<T>> {
    signals.toList().serializeSignals(quoteStyle).also {
        val mods = ModifierBuilder(DataSignalsModifiers()).apply(block).getModifiers()
        this.visitor.visitAttribute("data-signals$mods", it)
    }
    return signals.map { (name, _) ->
        Signal(name)
    }
}

/**
 * Initializes a single signal with its initial value and modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param value the value of the signal
 * @param block configuration lambda for (signal) modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R,
    quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    block: ModifierBuilder<DataSignalsModifiers>.() -> Unit = {},
): Signal<R> {
    val mods = ModifierBuilder(DataSignalsModifiers()).apply(block).getModifiers()
    val serialized = Serializer.serializeValue(value, quoteStyle)
    this.visitor.visitAttribute("data-signals$mods", JavaScriptLiterals.objectLiteral(listOf(name to serialized)))

    return Signal(name)
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param block configuration lambda for (signal) modifiers
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    block: ModifierBuilder<DataSignalsModifiers>.() -> Unit = {},
): Signal<Any?> = dataSignal(name, "", QuoteStyle.DOUBLE, block)

/**
 * Attaches an event listener to an element, executing an expression whenever the event is triggered.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on attribute will be added
 * @param event the event to listen for
 * @param block configuration lambda for (event) modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>, EVT : Event> Element<E, P>.dataOn(
    event: EVT,
    block: EventExpressionBuilder<EVT>.() -> Unit,
) {
    val builder = EventExpressionBuilder(event)

    builder.block()
    val expr = builder.getExpression()
    val mods = builder.getModifiers()

    this.visitor.visitAttribute("data-on:$event$mods", expr)
}

/**
 * Runs an expression once when an element is initialized, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param block configuration lambda for (initialization) modifiers amd create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(block: ExpressionModifierBuilder<DataInitModifiers>.() -> Unit) {
    val result = ExpressionModifierBuilder(DataInitModifiers()).apply(block)
    val expression = result.getExpression()
    val modifiers = result.getModifiers()
    this.visitor.visitAttribute("data-init$modifiers", expression)
}

/**
 * Initializes multiple signals from a JSON object, with modifiers.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-json-signals attribute will be added
 * @param block configuration lambda for (JSON signals) modifiers
 * @param filterBuilder a builder that allow to JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(
    filterBuilder: SignalPatchFilter.() -> Unit,
    block: ModifierBuilder<DataJsonSignalsModifiers>.() -> Unit,
) {
    val modifiers = ModifierBuilder(DataJsonSignalsModifiers()).apply(block).getModifiers()
    val filter = SignalPatchFilter().apply(filterBuilder)
    this.visitor.visitAttribute("data-json-signals$modifiers", "$filter")
}

/**
 * Runs an expression at a regular interval.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-Interval attribute will be added
 * @param block configuration lambda for (duration/transition) modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnInterval(
    block: ExpressionModifierBuilder<DataOnIntervalModifiers>.() -> Unit,
) {
    val builder = ExpressionModifierBuilder(DataOnIntervalModifiers()).apply(block)
    val expression = builder.getExpression()
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-on-interval$modifiers", expression)
}

/**
 * Runs an expression when the element intersects with the viewport.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-intersect attribute will be added
 * @param block configuration lambda for (intersect) modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnIntersect(
    block: ExpressionModifierBuilder<DataOnIntersectModifiers>.() -> Unit,
) {
    val builder = ExpressionModifierBuilder(DataOnIntersectModifiers()).apply(block)
    val expression = builder.getExpression()
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-on-intersect$modifiers", expression)
}

/**
 * Tells DataStar to ignore this element and its descendants.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-intersect attribute will be added
 * @param block configuration lambda for (ignore) modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIgnore(block: ExpressionModifierBuilder<DataIgnoreModifiers>.() -> Unit) {
    val builder = ExpressionModifierBuilder(DataIgnoreModifiers()).apply(block)
    val expression = builder.getExpression()
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-ignore$modifiers", expression)
}

/**
 * Runs an expression whenever any Signal is patched.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch attribute will be added
 * @param block configuration lambda for (delay/debounce/throttle) modifiers and create expressions
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatch(
    block: ExpressionModifierBuilder<DataOnSignalPatchModifiers>.() -> Unit,
) {
    val builder = ExpressionModifierBuilder(DataOnSignalPatchModifiers()).apply(block)
    val expression = builder.getExpression()
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-on-signal-patch$modifiers", expression)
}

/**
 * Binds an existing signal to a text-based element.
 *
 * @receiver the TextGroup element to bind
 * @param signal the Signal to bind to (its value takes precedence)
 * @param block configuration lambda for (binding) modifiers and create expressions
 * @return the same Signal instance
 */
fun <R> TextGroup<*, *>.dataBind(
    signal: Signal<R>,
    block: ModifierBuilder<DataBindModifiers>.() -> Unit = {},
): Signal<R> {
    val builder = ModifierBuilder(DataBindModifiers()).apply(block)
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-bind$modifiers", signal.name)
    return signal
}

/**
 * Binds an existing signal to a select element.
 *
 * @receiver the Select element to bind
 * @param signal the Signal to bind to (its value takes precedence)
 * @param block configuration lambda for (binding) modifiers and create expressions
 * @return the same Signal instance
 */
fun <R> SelectAll<*, *>.dataBind(
    signal: Signal<R>,
    block: ModifierBuilder<DataBindModifiers>.() -> Unit = {},
): Signal<R> {
    val builder = ModifierBuilder(DataBindModifiers()).apply(block)
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-bind$modifiers", signal.name)
    return signal
}

/**
 * Creates a signal and sets up two-way data binding with modifiers.
 *
 * @receiver the TextGroup element to which the data-bind attribute will be added
 * @param name the name of the signal to create and bind
 * @param block configuration lambda for (binding) modifiers and create expressions
 * @return a Signal instance with the given name
 */
fun TextGroup<*, *>.dataBind(
    name: String,
    block: ModifierBuilder<DataBindModifiers>.() -> Unit = {},
): Signal<Any> {
    val builder = ModifierBuilder(DataBindModifiers()).apply(block)
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-bind$modifiers", name)
    return signal(name)
}

/**
 * Creates a signal and sets up two-way data binding with modifiers.
 *
 * @receiver the SelectAll element to which the data-bind attribute will be added
 * @param name the name of the signal to create and bind
 * @param block configuration lambda for (binding) modifiers and create expressions
 * @return a Signal instance with the given name
 */
fun SelectAll<*, *>.dataBind(
    name: String,
    block: ModifierBuilder<DataBindModifiers>.() -> Unit = {},
): Signal<Any> {
    val builder = ModifierBuilder(DataBindModifiers()).apply(block)
    val modifiers = builder.getModifiers()
    this.visitor.visitAttribute("data-bind$modifiers", name)
    return signal(name)
}

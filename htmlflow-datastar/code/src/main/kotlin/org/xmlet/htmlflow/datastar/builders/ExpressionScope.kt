package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.expressions.ActionOptions
import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import org.xmlet.htmlflow.datastar.serialization.QuoteStyle
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

@DatastarDslMarker
interface ExpressionScope {
    fun getExpression(): String

    operator fun Signal<*>.unaryPlus()

    operator fun String.unaryPlus()

    // Frontend actions

    fun peek(callable: () -> String): DataStarAction

    fun peek(js: String): DataStarAction

    fun setAll(
        value: Any,
        filterBuilder: SignalPatchFilter.() -> Unit,
    ): DataStarAction

    fun toggleAll(filterBuilder: SignalPatchFilter.() -> Unit): DataStarAction

    // Backend actions

    fun get(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun get(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun post(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun post(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun put(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun put(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun delete(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun delete(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun patch(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    fun patch(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit = {},
    ): DataStarAction

    // JavaScript operators

    /**
     * Equal to the JavaScript ! operator, used to negate an expression.
     */
    operator fun <T> Signal<T>.not(): DataStarExpressionOp

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Equal to the JavaScript && operator, used to chain multiple expressions together.
     */
    infix fun String.and(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Equal to the JavaScript || operator, used to chain multiple expressions together.
     */
    infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Equal to the JavaScript || operator, used to chain multiple expressions together.
     */
    infix fun String.or(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Equal to the JavaScript == operator, used to compare two expressions.
     */
    infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Equal to the JavaScript == operator, used to compare two expressions.
     */
    infix fun <T> Signal<T>.eq(value: T): DataStarExpressionOp

    /**
     * Equal to the assignment operator in JavaScript, used to assign the signal value to the new value.
     */
    fun <T> Signal<T>.setValue(
        value: T,
        quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    ): DataStarExpressionOp

    /**
     * Equal to the assignment operator in JavaScript, used to assign the passed expression to another expression.
     */
    fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpressionOp

    /**
     * Accessing properties of signals
     */
    fun <T, V> Signal<T>.on(prop: KProperty1<T, V>): Signal<V>
}

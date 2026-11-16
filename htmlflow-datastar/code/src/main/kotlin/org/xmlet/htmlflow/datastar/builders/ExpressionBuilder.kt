package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.expressions.ActionOptions
import org.xmlet.htmlflow.datastar.expressions.ActionType
import org.xmlet.htmlflow.datastar.expressions.DataStarAction
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.addApostrophe
import org.xmlet.htmlflow.datastar.expressions.DataStarAction.Companion.convertFuncToPath
import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp
import org.xmlet.htmlflow.datastar.expressions.ExpressionPrecedence
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import org.xmlet.htmlflow.datastar.serialization.QuoteStyle
import org.xmlet.htmlflow.datastar.serialization.Serializer
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * A DSL builder for constructing DataStar expressions by combining signals, actions,
 * and operators.
 *
 * Multiple expressions are accumulated and separated by semicolons in the final output.
 * Composed operator expressions track JavaScript precedence and add parentheses when
 * needed to preserve the grouping expressed by the Kotlin DSL.
 *
 * **Example usage:**
 * ```kotlin
 * +signal1
 * "confirm(\"Are you sure\")" and delete(::someRow)
 * signal2 and action2
 * signal.setValue(newValue)
 * ```
 */
class ExpressionBuilder : ExpressionScope {
    private val builderExpression = mutableListOf<DataStarExpression>()

    private fun appendExpression(expression: DataStarExpression) {
        builderExpression.addLast(expression)
    }

    private fun removeIfPresent(vararg expressions: DataStarExpression) {
        expressions.forEach { expression ->
            if (expression !is Signal<*>) {
                val lastIndex = builderExpression.lastIndexOf(expression)
                if (lastIndex != -1) {
                    builderExpression.removeAt(lastIndex)
                }
            }
        }
    }

    private fun SignalPatchFilter.applySignalPatchFilter(filterBuilder: SignalPatchFilter.() -> Unit): String =
        apply(filterBuilder)
            .toString()
            .takeUnless { it == "{}" }
            .orEmpty()

    private fun binaryExpression(
        left: DataStarExpression,
        operator: String,
        right: DataStarExpression,
        precedence: Int,
        groupEqualPrecedence: Boolean = false,
    ): DataStarExpressionOp {
        val leftSyntax = left.renderAsOperand(precedence, groupEqualPrecedence)
        val rightSyntax = right.renderAsOperand(precedence, groupEqualPrecedence = true)
        return DataStarExpressionOp("$leftSyntax $operator $rightSyntax", precedence)
    }

    private fun DataStarExpression.renderAsOperand(
        parentPrecedence: Int,
        groupEqualPrecedence: Boolean,
    ): String =
        if (precedence < parentPrecedence || groupEqualPrecedence && precedence == parentPrecedence) {
            "($syntax)"
        } else {
            syntax
        }

    override operator fun Signal<*>.unaryPlus() {
        appendExpression(this)
    }

    override operator fun String.unaryPlus() {
        appendExpression(DataStarExpressionOp(this))
    }

    override fun getExpression() = builderExpression.joinToString("; ") { it.syntax }

    /**
     * Frontend actions
     */
    override fun peek(callable: () -> String): DataStarAction {
        val action = DataStarAction(ActionType.PEEK, callable)
        appendExpression(action)
        return action
    }

    override fun peek(js: String): DataStarAction {
        val action = DataStarAction(ActionType.PEEK, js)
        appendExpression(action)
        return action
    }

    override fun setAll(
        value: Any,
        filterBuilder: SignalPatchFilter.() -> Unit,
    ): DataStarAction {
        val action = DataStarAction(ActionType.SET_ALL, value, SignalPatchFilter().applySignalPatchFilter(filterBuilder))
        appendExpression(action)
        return action
    }

    override fun toggleAll(filterBuilder: SignalPatchFilter.() -> Unit): DataStarAction {
        val action = DataStarAction(ActionType.TOGGLE_ALL, SignalPatchFilter().applySignalPatchFilter(filterBuilder))
        appendExpression(action)
        return action
    }

    /**
     * Backend actions
     */
    override fun get(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.GET, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    override fun get(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.GET, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    override fun post(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.POST, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    override fun post(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.POST, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    override fun put(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PUT, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    override fun put(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PUT, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    override fun delete(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.DELETE, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    override fun delete(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.DELETE, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    override fun patch(
        func: KFunction<*>,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PATCH, convertFuncToPath(func), options)
        appendExpression(action)
        return action
    }

    override fun patch(
        path: String,
        optionsBuilder: ActionOptions.() -> Unit,
    ): DataStarAction {
        val options = ActionOptions().apply(optionsBuilder).toString()
        val action = DataStarAction(ActionType.PATCH, addApostrophe(path), options)
        appendExpression(action)
        return action
    }

    /**
     * JavaScript operators
     */

    override operator fun <T> Signal<T>.not(): DataStarExpressionOp {
        val result = DataStarExpressionOp("!${this.renderAsOperand(ExpressionPrecedence.UNARY)}", ExpressionPrecedence.UNARY)
        appendExpression(result)
        return result
    }

    override infix fun DataStarExpression.and(expression: DataStarExpression): DataStarExpressionOp {
        val result = binaryExpression(this, "&&", expression, ExpressionPrecedence.LOGICAL_AND)
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    override infix fun String.and(expression: DataStarExpression): DataStarExpressionOp {
        removeIfPresent(expression)
        val result =
            DataStarExpressionOp(
                "$this && ${expression.renderAsOperand(ExpressionPrecedence.LOGICAL_AND)}",
                ExpressionPrecedence.LOGICAL_AND,
            )
        appendExpression(result)
        return result
    }

    override infix fun DataStarExpression.or(expression: DataStarExpression): DataStarExpressionOp {
        val result = binaryExpression(this, "||", expression, ExpressionPrecedence.LOGICAL_OR)
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    override infix fun String.or(expression: DataStarExpression): DataStarExpressionOp {
        val result =
            DataStarExpressionOp(
                "$this || ${expression.renderAsOperand(ExpressionPrecedence.LOGICAL_OR)}",
                ExpressionPrecedence.LOGICAL_OR,
            )
        removeIfPresent(expression)
        appendExpression(result)
        return result
    }

    override infix fun DataStarExpression.eq(expression: DataStarExpression): DataStarExpressionOp {
        val result = binaryExpression(this, "==", expression, ExpressionPrecedence.EQUALITY, groupEqualPrecedence = true)
        removeIfPresent(this, expression)
        appendExpression(result)
        return result
    }

    override infix fun <T> Signal<T>.eq(value: T): DataStarExpressionOp {
        val result = DataStarExpressionOp("${this.syntax} == $value", ExpressionPrecedence.EQUALITY)
        removeIfPresent(this)
        appendExpression(result)
        return result
    }

    override fun <T> Signal<T>.setValue(
        value: T,
        quoteStyle: QuoteStyle,
    ): DataStarExpressionOp {
        val serializedValue = Serializer.serializeValue(value)
        val result = DataStarExpressionOp("${this.syntax} = $serializedValue", ExpressionPrecedence.ASSIGNMENT)
        appendExpression(result)
        return result
    }

    override fun Signal<*>.setValue(expression: DataStarExpression): DataStarExpressionOp {
        removeIfPresent(expression)
        val result =
            DataStarExpressionOp(
                "${this.syntax} = ${expression.renderAsOperand(ExpressionPrecedence.ASSIGNMENT)}",
                ExpressionPrecedence.ASSIGNMENT,
            )
        appendExpression(result)
        return result
    }

    override fun <T, V> Signal<T>.on(prop: KProperty1<T, V>): Signal<V> {
        val expr = "${this.name}.${prop.name}"
        return Signal(expr)
    }
}

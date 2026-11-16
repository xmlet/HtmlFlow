package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a DataStar expression containing JavaScript code, signals, and DataStar actions
 * to be evaluated within data-* attribute.
 *
 * @property syntax The raw string representation of the expression to be evaluated
 *                  and processed by the DataStar framework.
 *
 * Expressions also carry JavaScript operator precedence metadata so composed expressions can
 * render parentheses where needed to preserve the grouping expressed in the Kotlin DSL.
 */
sealed class DataStarExpression(
    val syntax: String,
) {
    internal open val precedence: Int = ExpressionPrecedence.PRIMARY

    internal fun renderAsOperand(parentPrecedence: Int): String =
        if (precedence < parentPrecedence) {
            "($syntax)"
        } else {
            syntax
        }
}

internal object ExpressionPrecedence {
    const val ASSIGNMENT = 3
    const val LOGICAL_OR = 4
    const val LOGICAL_AND = 5
    const val EQUALITY = 8
    const val UNARY = 14
    const val PRIMARY = 20
}

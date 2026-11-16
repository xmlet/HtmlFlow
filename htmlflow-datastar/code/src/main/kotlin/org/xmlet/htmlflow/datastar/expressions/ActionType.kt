package org.xmlet.htmlflow.datastar.expressions

/**
 * Available DataStar actions.
 *
 * Each action type represents a specific operation that can be performed on HTML elements
 * in the DataStar framework. The syntax value is the DataStar-specific symbol used to
 * represent each action in expressions.
 *
 * @property syntax The DataStar syntax symbol for this action type.
 */
enum class ActionType(
    val syntax: String,
) {
    PEEK("@peek"),
    SET_ALL("@setAll"),
    TOGGLE_ALL("@toggleAll"),
    GET("@get"),
    POST("@post"),
    PUT("@put"),
    DELETE("@delete"),
    PATCH("@patch"),
}

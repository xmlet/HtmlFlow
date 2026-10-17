package org.xmlet.htmlflow.datastar.expressions

import jakarta.ws.rs.Path
import org.xmlet.htmlflow.datastar.serialization.JavaScriptLiterals
import kotlin.reflect.KFunction

/**
 * Represents a DataStar action that can be triggered on HTML elements.
 *
 * A [DataStarAction] encapsulates a specific action type and its parameters,
 * generating the appropriate DataStar expression syntax for execution.
 *
 * @property type The [ActionType] that defines the action to be performed.
 */
class DataStarAction internal constructor(
    val type: ActionType,
    vararg params: Any?,
) : DataStarExpression(makeExpression(type, *params)) {
    companion object {
        internal fun convertFuncToPath(func: KFunction<*>): String {
            val pathAnnotation = func.annotations.filterIsInstance<Path>().firstOrNull()
            val path = pathAnnotation?.value ?: throw IllegalArgumentException("Function ${func.name} must be annotated with @Path")
            return JavaScriptLiterals.stringLiteral(path)
        }

        internal fun addApostrophe(path: String) = JavaScriptLiterals.stringLiteral(path)
    }
}

private fun makeExpression(
    type: ActionType,
    vararg params: Any?,
): String =
    when (type) {
        ActionType.PEEK -> {
            if (params[0] is String) {
                type.syntax + "(" + params[0] + ")"
            } else {
                throw NotImplementedError("Only string literals are supported for peek actions at the moment.")
            }
        }

        else -> {
            val normalizedParams =
                params.mapNotNull { param ->
                    when (param) {
                        null -> null
                        is String -> param.takeUnless { it.isBlank() }
                        else -> param
                    }
                }

            "${type.syntax}(${normalizedParams.joinToString(", ")})"
        }
    }

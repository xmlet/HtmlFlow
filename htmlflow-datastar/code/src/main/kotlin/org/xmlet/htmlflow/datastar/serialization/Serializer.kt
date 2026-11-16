package org.xmlet.htmlflow.datastar.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.full.createType

/**
 * Object responsible for serializing values, computed signal and signals.
 */
internal object Serializer {
    private val json =
        Json {
            encodeDefaults = true
            explicitNulls = true
        }

    /**
     * Transforms a list of pairs containing the names and values of signals to create
     *  into a JSON representation to pass to the `data-signals` attribute.
     *
     * @receiver The list of pairs to transform
     * @return The String with the corresponding representation in JSON
     */
    internal fun List<Pair<String, Any?>>.serializeSignals(quoteStyle: QuoteStyle = QuoteStyle.SINGLE): String =
        JavaScriptLiterals.objectLiteral(
            this.map { (name, value) ->
                name to serializeValue(value, quoteStyle)
            },
        )

    /**
     * Serializes the value received for correct display of complex Signal domains.
     *
     * @param value The value to serialize
     * @return The string representation of the value
     */
    internal fun serializeValue(
        value: Any?,
        quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    ): String =
        when (value) {
            null -> {
                "null"
            }

            is String -> {
                JavaScriptLiterals.stringLiteral(value, quoteStyle)
            }

            is Number, is Boolean -> {
                "$value"
            }

            is RawJS -> {
                value.js
            }

            is Map<*, *> -> {
                value.entries.joinToString(prefix = "{", postfix = "}", separator = ", ") { (key, item) ->
                    "${JavaScriptLiterals.objectKey(key.toString())}: ${serializeValue(item, quoteStyle)}"
                }
            }

            is Collection<*> -> {
                value.joinToString(prefix = "[", postfix = "]", separator = ", ") { serializeValue(it, quoteStyle) }
            }

            is Array<*> -> {
                value.toList().joinToString(prefix = "[", postfix = "]", separator = ", ") { serializeValue(it, quoteStyle) }
            }

            is JsonNull -> {
                "null"
            }

            is JsonPrimitive -> {
                if (value.isString) JavaScriptLiterals.stringLiteral(value.content, quoteStyle) else value.content
            }

            else -> {
                val serializer =
                    json.serializersModule.serializerOrNull(value::class.createType())
                        ?: return value.toString()

                serializeValue(json.encodeToJsonElement(serializer, value), quoteStyle)
            }
        }

    /**
     * Serializes a computed signal into a JavaScript object containing a lambda expression associated with the given signal name.
     *
     * @param name the name of the signal
     * @param expression The expression evaluated by the lambda.
     * @return A JavaScript object representation for the `data-computed` attribute.
     */
    internal fun serializeComputed(
        name: String,
        expression: String,
    ): String = JavaScriptLiterals.objectLiteral(listOf(name to "() => $expression"))
}

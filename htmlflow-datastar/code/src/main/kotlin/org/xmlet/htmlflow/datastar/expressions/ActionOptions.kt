package org.xmlet.htmlflow.datastar.expressions

import org.xmlet.htmlflow.datastar.serialization.JavaScriptLiterals
import org.xmlet.htmlflow.datastar.serialization.QuoteStyle
import kotlin.time.Duration

/**
 * Holds optional configuration values for a DataStar action.
 *
 * These options are serialized into the action argument object when present.
 */
class ActionOptions {
    var contentType: ContentType? = null
    var filterSignals: SignalPatchFilter.() -> Unit = {}
    var selector: String? = null
    var headers: String? = null
    var openWhenHidden: Boolean? = null
    var payload: String? = null
    var retry: RetryMode? = null
    var retryInterval: Duration? = null
    var retryScaler: Int? = null
    var retryMaxCount: Int? = null
    var requestCancellation: RequestCancellationMode? = null

    override fun toString(): String {
        val parts =
            buildList {
                contentType?.let { add("contentType: '${it.name.lowercase()}'") }

                val filter = SignalPatchFilter().apply(filterSignals).toString()
                if (filter != "{}") {
                    add("filterSignals: $filter")
                }

                selector?.let { add("selector: ${JavaScriptLiterals.stringLiteral(it)}") }
                headers?.let { add("headers: ${JavaScriptLiterals.stringLiteral(it, QuoteStyle.DOUBLE)}") }
                openWhenHidden?.let { add("openWhenHidden: $it") }
                payload?.let { add("payload: ${JavaScriptLiterals.stringLiteral(it, QuoteStyle.DOUBLE)}") }
                retry?.let { add("retry: '${it.name.lowercase()}'") }
                retryInterval?.let { add("retryInterval: ${it.inWholeMilliseconds}") }
                retryScaler?.let { add("retryScaler: $it") }
                retryMaxCount?.let { add("retryMaxCount: $it") }
                requestCancellation?.let { add("requestCancellation: '${it.name.lowercase()}'") }
            }

        return if (parts.isEmpty()) "" else parts.joinToString(prefix = "{", postfix = "}")
    }
}

enum class ContentType {
    JSON,
    FORM,
}

enum class RetryMode {
    AUTO,
    ERROR,
    ALWAYS,
    NEVER,
}

enum class RequestCancellationMode {
    AUTO,
    CLEANUP,
    DISABLED,
}

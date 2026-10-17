package org.xmlet.htmlflow.datastar.serialization

enum class QuoteStyle(
    val char: Char,
) {
    SINGLE('\''),
    DOUBLE('"'),
}

internal object JavaScriptLiterals {
    private val identifierRegex = Regex("^[A-Za-z_$][A-Za-z0-9_$]*$")

    fun stringLiteral(
        value: String,
        quoteStyle: QuoteStyle = QuoteStyle.SINGLE,
    ): String {
        val quote = quoteStyle.char
        val escaped =
            buildString {
                value.forEach { char ->
                    when (char) {
                        '\\' -> append("\\\\")
                        '\b' -> append("\\b")
                        '\u000C' -> append("\\f")
                        '\n' -> append("\\n")
                        '\r' -> append("\\r")
                        '\t' -> append("\\t")
                        quote -> append('\\').append(char)
                        else -> append(char)
                    }
                }
            }

        return "$quote$escaped$quote"
    }

    fun objectKey(name: String): String =
        if (identifierRegex.matches(name)) {
            name
        } else {
            stringLiteral(name)
        }

    fun objectLiteral(properties: Iterable<Pair<String, String>>): String =
        properties.joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
            "${objectKey(name)}: $value"
        }

    fun regexLiteral(regex: Regex): String =
        buildString {
            append('/')
            regex.pattern.forEach { char ->
                when (char) {
                    '/' -> append("\\/")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    else -> append(char)
                }
            }
            append('/')
        }
}

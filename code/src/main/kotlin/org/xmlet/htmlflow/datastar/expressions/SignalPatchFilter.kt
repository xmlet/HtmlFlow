package org.xmlet.htmlflow.datastar.expressions

import org.xmlet.htmlflow.datastar.serialization.JavaScriptLiterals

/**
 * Represents a filter object for signal patching.
 *
 * You can filter which signals to watch by including or excluding patterns.
 *
 * Contains two optional regular expression properties:
 *  - [include] — signals matching this pattern are included
 *  - [exclude] — signals matching this pattern are excluded
 */
class SignalPatchFilter {
    var include: Regex? = null
    var exclude: Regex? = null

    override fun toString(): String {
        val parts = mutableListOf<Pair<String, String>>()
        include?.let { parts.add("include" to JavaScriptLiterals.regexLiteral(it)) }
        exclude?.let { parts.add("exclude" to JavaScriptLiterals.regexLiteral(it)) }
        return JavaScriptLiterals.objectLiteral(parts)
    }
}

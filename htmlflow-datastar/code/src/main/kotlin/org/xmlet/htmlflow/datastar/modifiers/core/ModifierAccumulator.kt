package org.xmlet.htmlflow.datastar.modifiers.core

import org.xmlet.htmlflow.datastar.builders.DatastarDslMarker

/**
 * An abstract core class for accumulating DataStar modifiers into a string representation.
 *
 * Subclasses implement specific modifier configurations by calling [append] to build
 * the final modifier string accessible via [modifiers].
 */
@DatastarDslMarker
abstract class ModifierAccumulator : ModifierWriter {
    private val sb = StringBuilder()

    override fun append(mod: String) {
        sb.append(mod)
    }

    val modifiers: String get() = sb.toString()
}

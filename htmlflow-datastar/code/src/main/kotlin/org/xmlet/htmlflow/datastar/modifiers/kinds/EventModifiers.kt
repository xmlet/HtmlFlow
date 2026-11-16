package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface EventModifiers :
    BaseEventModifiers,
    ModifierWriter {
    /**
     * Only for built-in events
     */
    fun passive() = append("__passive")

    /**
     * Only for built-in events
     */
    fun capture() = append("__capture")

    fun window() = append("__window")

    fun outside() = append("__outside")

    fun prevent() = append("__prevent")

    fun stop() = append("__stop")

    fun document() = append("__document")
}

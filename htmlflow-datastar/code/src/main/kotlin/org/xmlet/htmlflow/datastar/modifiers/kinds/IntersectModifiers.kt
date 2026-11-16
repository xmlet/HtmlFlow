package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface IntersectModifiers :
    BaseEventModifiers,
    ModifierWriter {
    fun half() = append("__half")

    fun full() = append("__full")

    fun exit() = append("__exit")
}

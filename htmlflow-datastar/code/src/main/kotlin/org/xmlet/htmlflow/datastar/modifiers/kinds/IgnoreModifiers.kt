package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface IgnoreModifiers : ModifierWriter {
    fun self() = append("__self")
}

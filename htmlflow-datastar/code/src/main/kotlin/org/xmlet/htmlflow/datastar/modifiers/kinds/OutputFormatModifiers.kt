package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface OutputFormatModifiers : ModifierWriter {
    fun terse() = append("__terse")
}

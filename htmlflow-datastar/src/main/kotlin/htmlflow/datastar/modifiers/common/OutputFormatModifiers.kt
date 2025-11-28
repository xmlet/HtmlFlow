package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope

interface OutputFormatModifiers : ModifierScope {
    fun terse() = addModifier("__terse")
}

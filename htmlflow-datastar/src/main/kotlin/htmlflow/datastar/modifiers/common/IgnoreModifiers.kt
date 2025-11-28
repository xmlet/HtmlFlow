package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope

interface IgnoreModifiers : ModifierScope {
    fun self() = addModifier("__self")
}

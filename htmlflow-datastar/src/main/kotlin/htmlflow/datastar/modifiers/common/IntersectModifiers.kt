package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope

interface IntersectModifiers :
    BaseEventModifiers,
    ModifierScope {
    fun half() = addModifier("__half")

    fun full() = addModifier("__full")
}

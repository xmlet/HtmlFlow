package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope

interface ViewTransitionModifiers : ModifierScope {
    fun viewTransition() = addModifier("__viewtransition")
}

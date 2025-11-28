package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope

interface BaseEventModifiers :
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers,
    ViewTransitionModifiers,
    ModifierScope {
    fun once() = addModifier("__once")
}

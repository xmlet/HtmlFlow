package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface BaseEventModifiers :
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers,
    ViewTransitionModifiers,
    ModifierWriter {
    fun once() = append("__once")
}

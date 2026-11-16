package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

interface ViewTransitionModifiers : ModifierWriter {
    fun viewTransition() = append("__viewtransition")
}

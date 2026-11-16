package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter
import org.xmlet.htmlflow.datastar.modifiers.core.toModifierDuration
import kotlin.time.Duration

interface DebounceModifiers : ModifierWriter {
    fun debounce(
        time: Duration,
        block: (TimingEdgeModifiers.() -> Unit)? = null,
    ) {
        append("__debounce.${time.toModifierDuration()}")
        block?.invoke(TimingEdgeModifiers(this))
    }
}

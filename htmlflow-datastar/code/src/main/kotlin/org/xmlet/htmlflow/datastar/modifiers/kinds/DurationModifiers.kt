package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter
import org.xmlet.htmlflow.datastar.modifiers.core.toModifierDuration
import kotlin.time.Duration

interface DurationModifiers : ModifierWriter {
    fun duration(
        time: Duration,
        leading: Boolean,
    ) = append("__duration.${time.toModifierDuration()}" + if (leading) ".leading" else "")
}

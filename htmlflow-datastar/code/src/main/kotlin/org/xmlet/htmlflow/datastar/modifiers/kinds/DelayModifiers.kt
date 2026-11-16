package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter
import org.xmlet.htmlflow.datastar.modifiers.core.toModifierDuration
import kotlin.time.Duration

interface DelayModifiers : ModifierWriter {
    fun delay(time: Duration) = append("__delay.${time.toModifierDuration()}")
}

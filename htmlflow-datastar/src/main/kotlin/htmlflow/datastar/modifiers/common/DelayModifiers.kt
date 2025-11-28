package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface DelayModifiers : ModifierScope {
    fun delay(time: Duration) = addModifier("__delay.$time")
}

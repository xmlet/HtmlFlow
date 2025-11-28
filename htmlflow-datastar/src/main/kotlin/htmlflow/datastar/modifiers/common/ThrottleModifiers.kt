package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface ThrottleModifiers : ModifierScope {
    fun throttle(
        time: Duration,
        block: (TimingEdgeModifiers.() -> Unit)? = null,
    ) {
        addModifier("__throttle.$time")
        block?.invoke(TimingEdgeModifiers(this))
    }
}

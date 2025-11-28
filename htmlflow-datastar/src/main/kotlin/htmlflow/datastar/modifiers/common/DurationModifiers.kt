package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope
import kotlin.time.Duration

interface DurationModifiers : ModifierScope {
    fun duration(
        time: Duration,
        leading: Boolean,
    ) = addModifier("__duration.$time" + if (leading) ".leading" else "")
}

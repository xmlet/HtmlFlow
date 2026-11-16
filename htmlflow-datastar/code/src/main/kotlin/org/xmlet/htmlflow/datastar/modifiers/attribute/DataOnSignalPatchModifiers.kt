package org.xmlet.htmlflow.datastar.modifiers.attribute

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator
import org.xmlet.htmlflow.datastar.modifiers.kinds.DebounceModifiers
import org.xmlet.htmlflow.datastar.modifiers.kinds.DelayModifiers
import org.xmlet.htmlflow.datastar.modifiers.kinds.ThrottleModifiers

class DataOnSignalPatchModifiers :
    ModifierAccumulator(),
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers

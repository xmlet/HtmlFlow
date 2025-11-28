package htmlflow.datastar.modifiers.attributes

import htmlflow.datastar.modifiers.base.ModifierBuilder
import htmlflow.datastar.modifiers.common.CaseModifiers
import htmlflow.datastar.modifiers.common.DebounceModifiers
import htmlflow.datastar.modifiers.common.DelayModifiers
import htmlflow.datastar.modifiers.common.DurationModifiers
import htmlflow.datastar.modifiers.common.EventModifiers
import htmlflow.datastar.modifiers.common.IgnoreModifiers
import htmlflow.datastar.modifiers.common.IntersectModifiers
import htmlflow.datastar.modifiers.common.OutputFormatModifiers
import htmlflow.datastar.modifiers.common.ThrottleModifiers
import htmlflow.datastar.modifiers.common.ViewTransitionModifiers

class DataBindModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataClassModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataComputedModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataIgnoreModifiers :
    ModifierBuilder(),
    IgnoreModifiers

class DataIndicatorModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataInitModifiers :
    ModifierBuilder(),
    DelayModifiers,
    ViewTransitionModifiers

class DataJsonSignalsModifiers :
    ModifierBuilder(),
    OutputFormatModifiers

class DataOnIntersectModifiers :
    ModifierBuilder(),
    IntersectModifiers

class DataOnIntervalModifiers :
    ModifierBuilder(),
    DurationModifiers,
    ViewTransitionModifiers

class DataOnModifiers :
    ModifierBuilder(),
    EventModifiers,
    CaseModifiers

class DataOnSignalPatchModifiers :
    ModifierBuilder(),
    DelayModifiers,
    DebounceModifiers,
    ThrottleModifiers

class DataRefModifiers :
    ModifierBuilder(),
    CaseModifiers

class DataSignalModifiers :
    ModifierBuilder(),
    CaseModifiers {
    fun ifMissing() = addModifier("__ifmissing")
}

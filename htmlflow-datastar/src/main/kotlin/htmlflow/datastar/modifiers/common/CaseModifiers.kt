package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.CaseStyle
import htmlflow.datastar.modifiers.base.ModifierScope

interface CaseModifiers : ModifierScope {
    fun case(style: CaseStyle) = addModifier("__case.${style.tag}")
}

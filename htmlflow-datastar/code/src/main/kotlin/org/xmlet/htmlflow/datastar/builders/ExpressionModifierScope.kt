package org.xmlet.htmlflow.datastar.builders

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

interface ExpressionModifierScope<M : ModifierAccumulator> :
    ExpressionScope,
    ModifierScope<M>

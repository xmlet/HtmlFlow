package org.xmlet.htmlflow.datastar.modifiers.attribute

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

class DataSignalsModifiers : ModifierAccumulator() {
    fun ifMissing() = append("__ifmissing")
}

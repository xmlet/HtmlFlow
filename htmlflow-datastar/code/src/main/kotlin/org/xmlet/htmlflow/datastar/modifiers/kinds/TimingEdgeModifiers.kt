package org.xmlet.htmlflow.datastar.modifiers.kinds

import org.xmlet.htmlflow.datastar.modifiers.core.ModifierWriter

class TimingEdgeModifiers(
    private val scope: ModifierWriter,
) {
    fun leading() = scope.append(".leading")

    fun noTrailing() = scope.append(".notrailing")
}

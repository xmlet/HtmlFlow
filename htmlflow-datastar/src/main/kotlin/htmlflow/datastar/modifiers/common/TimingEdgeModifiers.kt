package htmlflow.datastar.modifiers.common

import htmlflow.datastar.modifiers.base.ModifierScope

class TimingEdgeModifiers(
    private val scope: ModifierScope,
) {
    fun leading() = scope.addModifier(".leading")

    fun noTrailing() = scope.addModifier(".notrailing")
}

package htmlflow.datastar.modifiers.base

abstract class ModifierBuilder : ModifierScope {
    private val modifiers = mutableListOf<String>()

    override fun addModifier(mod: String) {
        modifiers += mod
    }

    override fun toString(): String = modifiers.joinToString("")
}

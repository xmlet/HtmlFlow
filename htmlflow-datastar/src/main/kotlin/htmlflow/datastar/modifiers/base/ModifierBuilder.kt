package htmlflow.datastar.modifiers.base

abstract class ModifierBuilder : ModifierScope {
    private val sb = StringBuilder()

    override fun addModifier(mod: String) {
        sb.append(mod)
    }

    override fun toString(): String = sb.toString()
}

import java.util.concurrent.ConcurrentHashMap

class ViewResolver<T> {

    private val resolutionCache = ConcurrentHashMap<Class<*>, ViewInfo>()

    fun findCompatibleView(model: T, viewRegistry: Map<Class<*>, ViewInfo>): ViewInfo {
        val modelClass = model!!::class.java

        viewRegistry[modelClass]?.let { return it }

        resolutionCache[modelClass]?.let { return it }

        val resolved = resolveViewThroughHierarchy(modelClass, viewRegistry)

        resolved?.let { resolutionCache[modelClass] = it }

        return resolved ?: throw ViewNotFound(model)
    }

    private fun resolveViewThroughHierarchy(
        modelClass: Class<*>,
        viewRegistry: Map<Class<*>, ViewInfo>
    ): ViewInfo? {
        findViewInInheritanceChain(modelClass, viewRegistry)?.let { return it }

        findViewInInterfaces(modelClass, viewRegistry)?.let { return it }

        return null
    }

    private fun findViewInInheritanceChain(
        modelClass: Class<*>,
        viewRegistry: Map<Class<*>, ViewInfo>
    ): ViewInfo? {
        var currentClass: Class<*>? = modelClass.superclass
        while (currentClass != null && currentClass != Any::class.java) {
            viewRegistry[currentClass]?.let { return it }
            currentClass = currentClass.superclass
        }
        return null
    }

    private fun findViewInInterfaces(
        modelClass: Class<*>,
        viewRegistry: Map<Class<*>, ViewInfo>
    ): ViewInfo? {
        for (interfaceClass in modelClass.interfaces) {
            viewRegistry[interfaceClass]?.let { return it }
        }
        return null
    }
}

import java.io.File
import java.net.URL

class ClasspathScanner {

    companion object {
        private val EXCLUDED_PACKAGES = setOf(
            "kotlin", "kotlinx",
            "java", "javax", "jdk", "sun", "com.sun", "com.oracle",
            "org.slf4j", "ch.qos.logback", "org.apache.logging",
            "org.junit", "org.mockito", "org.springframework", "org.jetbrains.annotations"
        )
    }

    fun scanPackage(basePackage: String, classProcessor: (String) -> Unit) {
        val classLoaders = getAvailableClassLoaders()
        val packagePath = basePackage.replace('.', '/')
        var resourcesFound = false

        for ((index, classLoader) in classLoaders.withIndex()) {
            try {
                val resources = classLoader.getResources(packagePath)
                while (resources.hasMoreElements()) {
                    resourcesFound = true
                    val resource = resources.nextElement()
                    processClasspathResource(resource, basePackage, classProcessor)
                }
                if (resourcesFound) break
            } catch (e: Exception) {
                if (index == classLoaders.lastIndex) {
                    throw e
                }
            }
        }
    }

    private fun getAvailableClassLoaders(): List<ClassLoader> {
        return listOfNotNull(
            Thread.currentThread().contextClassLoader,
            this::class.java.classLoader,
            ClassLoader.getSystemClassLoader()
        ).distinct()
    }

    private fun processClasspathResource(
        resource: URL,
        basePackage: String,
        classProcessor: (String) -> Unit
    ) {
        when (resource.protocol) {
            "file" -> processFileSystemResource(resource, basePackage, classProcessor)
        }
    }

    private fun processFileSystemResource(
        resource: URL,
        basePackage: String,
        classProcessor: (String) -> Unit
    ) {
        val packageDirectory = File(resource.toURI())
        if (packageDirectory.exists() && packageDirectory.isDirectory) {
            scanDirectory(packageDirectory, basePackage, classProcessor)
        }
    }

    private fun scanDirectory(
        directory: File,
        packageName: String,
        classProcessor: (String) -> Unit
    ) {
        if (shouldExcludePackage(packageName)) return

        val files = directory.listFiles() ?: return

        files.forEach { file ->
            when {
                file.isDirectory -> {
                    val subPackage = buildSubPackageName(packageName, file.name)
                    scanDirectory(file, subPackage, classProcessor)
                }
                isRelevantClassFile(file) -> {
                    val className = buildClassName(packageName, file.nameWithoutExtension)
                    classProcessor(className)
                }
            }
        }
    }

    private fun shouldExcludePackage(packageName: String): Boolean {
        return EXCLUDED_PACKAGES.any { packageName.startsWith(it) }
    }

    private fun isRelevantClassFile(file: File): Boolean {
        return file.name.endsWith(".class") && !file.name.contains('$')
    }

    private fun buildSubPackageName(parentPackage: String, directoryName: String): String {
        return if (parentPackage.isEmpty()) directoryName else "$parentPackage.$directoryName"
    }

    private fun buildClassName(packageName: String, simpleClassName: String): String {
        return if (packageName.isEmpty()) simpleClassName else "$packageName.$simpleClassName"
    }
}

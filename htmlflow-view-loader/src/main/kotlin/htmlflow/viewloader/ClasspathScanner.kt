/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow.viewloader

import java.io.File
import java.net.URL

/**
 * @author Bernardo Pereira
 */
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

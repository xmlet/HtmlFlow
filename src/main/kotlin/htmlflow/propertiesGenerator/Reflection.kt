package htmlflow.propertiesGenerator

import org.xmlet.htmlapifaster.Element
import java.net.URL
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

fun main() {
    doStuff()
}

fun doStuff() {
    val url = Element::class.java.protectionDomain.codeSource.location
    val list = listKClasses(url)
    val element = Element::class
    val generator = HtmlFlowExtensionPropertiesGenerator()

    val filteredList = list
        .filter { it.isSubclassOf(element) }
        .filter {
            it.simpleName != "Text"
                    && it.simpleName != "CustomElement"
                    && it.simpleName != "Html"
        }

    generator.createHtmlFlowExtensionProperties(
        list = filteredList,
        element = element,
    )
}

fun listKClasses(url: URL): List<KClass<*>> {
    val loader = Element::class.java.protectionDomain.classLoader
    ZipInputStream(url.openStream()).use { zip ->
        val res = mutableListOf<KClass<*>>()
        var entry = zip.nextEntry
        while (entry != null) {
            println(entry.name)
            if (!entry.isDirectory
                && entry.name.indexOf(".class") >= 0
                //&& !entry.name.lowercase().contains("enum")
            ) {
                val clazz = loader.loadClass(qualifiedName(entry)).kotlin
                if (!clazz.java.isInterface && !clazz.java.isEnum)
                    res.add(clazz)
            }
            entry = zip.nextEntry
        }
        return res;
    }
}


fun qualifiedName(entry: ZipEntry) = entry
    .name
    .replace('/', '.') // including ".class"
    .let { name -> name.substring(0, name.length - ".class".length) }
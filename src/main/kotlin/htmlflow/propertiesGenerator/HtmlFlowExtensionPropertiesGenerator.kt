package htmlflow.propertiesGenerator

import com.squareup.kotlinpoet.*
import java.io.File
import kotlin.reflect.KClass

class HtmlFlowExtensionPropertiesGenerator {

    val t = TypeVariableName("T")
    val type = TypeVariableName("T : Element<T,Z>, Z : Element<*,*>")

    fun createHtmlFlowExtensionProperties(
        list: List<KClass<*>>,
        name : String = "HtmlFlowExtensionProperties",
        destinationPackage : String = "src/main/kotlin",
        element : KClass<*>
    ) {
        val file = FileSpec.builder("htmlflow", name)
        file.addImport(element,"")
        list.forEach {kClass ->
            addProperty(file, kClass)
            addFun(file, kClass)
        }
        file.build().writeTo(File(destinationPackage))
    }

    private fun addProperty(
        file: FileSpec.Builder,
        clazz: KClass<*>
    ){
        clazz.simpleName?.let { className ->
            file.addProperty(
                PropertySpec.builder(className.lowercase(),TypeVariableName("${clazz.simpleName}<T>"))
                    .receiver(t)
                    .addTypeVariable(type)
                    .getter(
                        FunSpec.getterBuilder()
                            .addModifiers(KModifier.INLINE)
                            .addStatement("return %T(%L.self())", clazz, "this")
                            .build()
                    )
                    .build()
            )
        }
    }

    private fun addFun(
        file: FileSpec.Builder,
        clazz: KClass<*>,
    ) {
        clazz.simpleName?.let { className ->
            val typeVariable = TypeVariableName("${className}<T>")
            file.addFunction(
                FunSpec.builder(className.lowercase())
                    .returns(t)
                    .receiver(t)
                    .addTypeVariable(type)
                    .addParameter("block", LambdaTypeName.get(
                        receiver = typeVariable,
                        returnType = ClassName("kotlin", "Unit"),
                    ))
                    .addStatement("val elem =  %T(%L)", clazz, "this")
                    .addStatement("elem.block()")
                    .addStatement("return elem.l", clazz, "this")
                    .build()
            )
        }
    }
}
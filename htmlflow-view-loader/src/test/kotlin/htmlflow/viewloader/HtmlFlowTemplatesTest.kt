package htmlflow.viewloader

import ViewNotFound
import ClassInspector
import htmlflow.HtmlFlow
import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.viewloader.views.objects.ClassTestViews
import htmlflow.viewloader.views.simple.SimpleTestViews
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.p
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HtmlFlowTemplatesTest {
    private lateinit var htmlFlowTemplates: HtmlFlowTemplates

    @BeforeEach
    fun setUp() {
        htmlFlowTemplates = HtmlFlowTemplates()
    }

    @Nested
    inner class ViewFindingTests {
        @Test
        fun `should find views in simple package`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.simple",
                )

            val simpleModel = SimpleTestViewModel("test content")
            val result = renderer(simpleModel)

            assertTrue(result.contains("Simple View") || result.contains("Method View"))
            assertTrue(result.contains("test content"))
        }

        @Test
        fun `should find views in objects package`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.objects",
                )

            val complexModel = ComplexTestViewModel("Test Title", listOf("item1", "item2"), true)
            val result = renderer(complexModel)

            assertTrue(result.contains("Complex View") || result.contains("Object Complex View"))
            assertTrue(result.contains("Test Title"))
        }

        @Test
        fun `should handle empty package`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.empty",
                )

            val model = SimpleTestViewModel("test")
            assertThrows<ViewNotFound> { renderer(model) }
        }

        @Test
        fun `should find views across multiple packages`() {
            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views")

            val simpleModel = SimpleTestViewModel("test content")
            val result = renderer(simpleModel)

            assertNotNull(result)
            assertTrue(result.contains("test content"))
        }

        @Test
        fun `should cache view registry across multiple calls`() {
            val renderer1 =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.simple",
                )
            val renderer2 =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.simple",
                )

            val model = SimpleTestViewModel("test")
            val result1 = renderer1(model)
            val result2 = renderer2(model)

            assertEquals(result1, result2)
        }
    }

    @Nested
    inner class ViewRenderingTests {
        @Test
        fun `should render HtmlView correctly`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.simple",
                )

            val model = SimpleTestViewModel("Hello World")
            val result = renderer(model)

            assertTrue(result.contains("Hello World"))
            assertTrue(result.contains("<html>"))
            assertTrue(result.contains("</html>"))
        }

        @Test
        fun `should render complex view with lists and conditionals`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.objects",
                )

            val model = ComplexTestViewModel("My Title", listOf("First", "Second", "Third"), true)
            val result = renderer(model)

            assertTrue(result.contains("My Title"))
            assertTrue(result.contains("First"))
            assertTrue(result.contains("Second"))
            assertTrue(result.contains("Third"))
            assertTrue(result.contains("Active"))
        }

        @Test
        fun `should handle inactive status in complex view`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.objects",
                )

            val model = ComplexTestViewModel("Inactive Title", listOf("Item"), false)
            val result = renderer(model)

            assertTrue(result.contains("Inactive Title"))
            assertTrue(result.contains("Item"))
            assertFalse(result.contains("Active"))
        }

        @Test
        fun `should throw exception for unknown view type`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.simple",
                )

            val unknownModel = object : ViewModel {}

            assertThrows<ViewNotFound> { renderer(unknownModel) }
        }
    }

    @Nested
    inner class InheritanceTests {
        @Test
        fun `should find view for base class when using inherited model`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.inheritance",
                )

            val inheritedModel = InheritedTestViewModel("base content", "derived content")
            val result = renderer(inheritedModel)

            assertTrue(result.contains("base content"))
        }

        @Test
        fun `should find view for interface when using implementing model`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.inheritance",
                )

            val interfaceModel = InterfaceTestViewModel("interface content", "additional content")
            val result = renderer(interfaceModel)

            assertTrue(result.contains("interface content"))
        }

        @Test
        fun `should handle direct base class model`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.inheritance",
                )

            val baseModel = BaseTestViewModel("direct base content")
            val result = renderer(baseModel)

            assertTrue(result.contains("direct base content"))
        }
    }

    @Nested
    inner class DuplicateViewTests {
        @Test
        fun `should throw exception when duplicate views are found`() {
            val exception =
                assertThrows<IllegalStateException> {
                    htmlFlowTemplates.CachingClasspath(
                        "htmlflow.viewloader.viewsfailing.duplicate",
                    )
                }

            assertTrue(exception.message!!.contains("Multiple views found"))
            assertTrue(exception.message!!.contains("SimpleTestViewModel"))
        }
    }

    @Nested
    inner class TypeCheckingTests {

        @Test
        fun `should correctly identify HtmlView methods`() {
            val method = createMockMethod(HtmlView::class.java)
            val result = callPrivateMethod("isHtmlViewMethod", method)
            assertTrue(result as Boolean)
        }

        @Test
        fun `should reject non-view methods`() {
            val method = createMockMethod(String::class.java)
            val result = callPrivateMethod("isHtmlViewMethod", method)
            assertFalse(result as Boolean)
        }

        private fun createMockMethod(returnType: Class<*>): Method {
            val methods = DummyMethodHolder::class.java.declaredMethods
            return methods.firstOrNull { returnType.isAssignableFrom(it.returnType) }
                ?: throw IllegalArgumentException("No dummy method found for return type: ${returnType.name}")
        }
    }

    @Nested
    inner class ObjectInstanceTests {
        @Test
        fun `should successfully create Kotlin object instance`() {
            val result = callPrivateMethod("tryObjectInstance", SimpleTestViews::class.java)
            assertNotNull(result)
        }

        @Test
        fun `should return null for non-object class`() {
            val result = callPrivateMethod("tryObjectInstance", ClassTestViews::class.java)
            assertNull(result)
        }

        @Test
        fun `should create default constructor instance`() {
            val result = callPrivateMethod("tryDefaultConstructor", ClassTestViews::class.java)
            assertNotNull(result)
        }
    }

    @Nested
    inner class ViewTypeExtractionTests {
        @Test
        fun `should handle non-parameterized types gracefully`() {
            val result = callPrivateMethod("extractFromClassType", String::class.java)
            assertNull(result)
        }
    }

    @Nested
    inner class IntegrationTests {
        @Test
        fun `should work with extension functions`() {
            val view: htmlflow.HtmlView<SimpleTestViewModel> =
                HtmlFlow.view {
                    it.html {
                        body {
                            div {
                                attrClass("extension-test")
                                dyn { model: SimpleTestViewModel ->
                                    p { text("Extension: ${model.content}") }
                                }
                            }
                        }
                    }
                }

            val renderer = view.renderer()
            val model = SimpleTestViewModel("extension test")
            val result = renderer(model)

            assertTrue(result.contains("Extension: extension test"))
        }

        @Test
        fun `should handle type mismatch in extension function`() {
            val view: htmlflow.HtmlView<SimpleTestViewModel> = HtmlFlow.view { it.html { body {} } }

            val renderer = view.renderer()
            val wrongModel = AsyncTestViewModel("wrong type")

            val exception = assertThrows<ViewNotFound> { renderer(wrongModel) }

            assertTrue(exception.message!!.contains("No view found for model type"))
        }
    }

    @Nested
    inner class TemplatesInterfaceTests {
        @Test
        fun `should return working TemplateRenderer from CachingClasspath`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.simple",
                )
            assertNotNull(renderer)
        }
    }

    @Nested
    inner class DirectMatchTests {

        @Test
        fun `should find view through direct match - Example 1`() {
            val userModel = UserVm("Alice")
            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views.kdoc")
            val result = renderer(userModel)

            assertTrue(result.contains("User Profile"))
            assertTrue(result.contains("Welcome, Alice!"))
            assertTrue(result.contains("class=\"user-view\""))
        }
    }

    @Nested
    inner class InheritanceChainTests {

        @Test
        fun `should find view through superclass match - Example 3`() {
            val derivedModel = DerivedVm("base content")
            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views.kdoc")
            val result = renderer(derivedModel)

            assertTrue(result.contains("Base View"))
            assertTrue(result.contains("Base content: base content"))
            assertTrue(result.contains("class=\"base-view\""))
            assertFalse(result.contains("derived content"))
        }

        @Test
        fun `should cache inheritance resolution - Example 2`() {
            val derivedModel1 = DerivedVm("first")
            val derivedModel2 = DerivedVm("second")

            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views.kdoc")
            val result1 = renderer(derivedModel1)
            assertTrue(result1.contains("Base content: first"))

            val cacheSize = getResolutionCacheSize()
            assertTrue(
                cacheSize > 0,
                "Resolution cache should contain cached entries after first resolution"
            )

            val result2 = renderer(derivedModel2)
            assertTrue(result2.contains("Base content: second"))

            Assertions.assertEquals(
                cacheSize,
                getResolutionCacheSize(),
                "Cache size should not change on subsequent lookups"
            )
        }
    }

    @Nested
    inner class InterfaceMatchTests {

        @Test
        fun `should find view through interface match - Example 4`() {
            val publicProfile = PublicProfile("John Doe", "Software Developer")
            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views.kdoc")
            val result = renderer(publicProfile)

            assertTrue(result.contains("Profile Interface View"))
            assertTrue(result.contains("Profile name: John Doe"))
            assertTrue(result.contains("class=\"profile-view\""))
        }

        @Test
        fun `should handle multiple interfaces with deterministic precedence`() {
            val multiInterfaceModel = MultiInterfaceVm("Multi User", "Secondary Info")
            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views.kdoc")
            val result = renderer(multiInterfaceModel)

            assertTrue(
                result.contains("Profile name: Multi User") ||
                        result.contains("Secondary: Secondary Info")
            )
        }
    }

    @Nested
    inner class ResolutionPrecedenceTests {

        @Test
        fun `should follow correct precedence order`() {
            val model = DerivedVm("test content")

            clearResolutionCache()
            val renderer = htmlFlowTemplates.CachingClasspath("htmlflow.viewloader.views.kdoc")

            val result = renderer(model)

            assertTrue(result.contains("Base View"))
            assertTrue(result.contains("test content"))
        }
    }

    @Nested
    inner class ErrorCaseTests {

        @Test
        fun `should throw error when no view found`() {
            val renderer =
                htmlFlowTemplates.CachingClasspath(
                    "htmlflow.viewloader.views.kdoc.nonexistent"
                )
            data class UnmatchedVm(val content: String) : ViewModel

            val unmatchedModel = UnmatchedVm("no view for this")

            Assertions.assertThrows(ViewNotFound::class.java) { renderer(unmatchedModel) }
        }
    }

    @Test
    fun `all examples should render successfully`() {
        val templates = HtmlFlowTemplates()
        val renderer = templates.CachingClasspath("htmlflow.viewloader.views.kdoc")

        val userResult = renderer(UserVm("Direct User"))
        assertContainsExpectedContent(userResult, "User Profile", "Direct User")

        val firstDerived = renderer(DerivedVm("First Base"))
        assertContainsExpectedContent(firstDerived, "Base View", "First Base")

        val secondDerived = renderer(DerivedVm("Second Base"))
        assertContainsExpectedContent(secondDerived, "Base View", "Second Base")

        val profileResult = renderer(PublicProfile("Interface User", "Developer"))
        assertContainsExpectedContent(profileResult, "Profile Interface View", "Interface User")
    }

    @Test
    fun `hot reload should clear caches and work correctly`() {
        val templates = HtmlFlowTemplates()

        val cachingRenderer = templates.CachingClasspath("htmlflow.viewloader.views.kdoc")
        val cachingResult = cachingRenderer(DerivedVm("Caching Test"))
        assertContainsExpectedContent(cachingResult, "Base View", "Caching Test")

        val hotReloadRenderer = templates.HotReload("htmlflow.viewloader.views.kdoc")
        val hotReloadResult = hotReloadRenderer(DerivedVm("Hot Reload Test"))
        assertContainsExpectedContent(hotReloadResult, "Base View", "Hot Reload Test")
    }

    @Test
    fun `resolution precedence should be consistent`() {
        val templates = HtmlFlowTemplates()
        val renderer = templates.CachingClasspath("htmlflow.viewloader.views.kdoc")

        val models =
            listOf(
                DerivedVm("Test 1"),
                DerivedVm("Test 2"),
                DerivedVm("Test 3")
            )

        val results = models.map { renderer(it) }

        results.forEach { result ->
            assertTrue(
                result.contains("Base View"),
                "All derived models should resolve to base view"
            )
        }
    }

    private fun assertContainsExpectedContent(html: String, viewTitle: String, content: String) {
        assertTrue(html.contains(viewTitle), "HTML should contain view title: $viewTitle")
        assertTrue(html.contains(content), "HTML should contain expected content: $content")
        assertTrue(html.contains("<html>"), "Should be valid HTML")
        assertTrue(html.contains("</html>"), "Should be valid HTML")
    }

    private fun clearResolutionCache() {
        try {
            val resolutionCacheField =
                HtmlFlowTemplates::class.java.getDeclaredField("resolutionCache")
            resolutionCacheField.isAccessible = true
            val cache =
                resolutionCacheField.get(null) as? ConcurrentHashMap<*, *>
            cache?.clear()
        } catch (_: Exception) {
        }
    }

    private fun getResolutionCacheSize(): Int {
        return try {
            val loader = HtmlFlowTemplates::class.java
                .getDeclaredField("loader")
            val loaderInstance = loader.apply { isAccessible = true }.get(htmlFlowTemplates)

            val resolverField = loaderInstance::class.java
                .getDeclaredField("viewResolver")
            val resolverInstance = resolverField.apply { isAccessible = true }.get(loaderInstance)

            val resolutionCacheField = resolverInstance::class.java
                .getDeclaredField("resolutionCache")
            resolutionCacheField.isAccessible = true
            val cache =
                resolutionCacheField.get(resolverInstance) as? ConcurrentHashMap<*, *>
            cache?.size ?: 0
        } catch (_: Exception) {
            0
        }
    }


    private fun callPrivateMethod(
        methodName: String,
        vararg args: Any?,
    ): Any? {
        val loader =
            htmlFlowTemplates::class
                .java
                .getDeclaredField("loader")
        val loaderValue = loader.apply { isAccessible = true }.get(htmlFlowTemplates)

        val inspector = loaderValue!!::class.java.getDeclaredField("classInspector")
        val inspectorValue = inspector.apply { isAccessible = true }.get(loaderValue)

        val method: Method = ClassInspector::class.java.getDeclaredMethod(methodName, *args.map { it!!::class.java }.toTypedArray())
        method.isAccessible = true

        return method.invoke(inspectorValue, *args)
    }

    @Suppress("unused")
    private class DummyFieldHolder {
        val htmlViewField: htmlflow.HtmlView<*>? = null

        val stringField: String = ""
    }

    @Suppress("unused")
    private class DummyMethodHolder {
        fun htmlViewMethod(): htmlflow.HtmlView<*>? = null
        fun stringMethod(): String? = null
    }
}

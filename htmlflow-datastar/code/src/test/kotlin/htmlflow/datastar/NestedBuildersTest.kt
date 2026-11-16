package htmlflow.datastar

import htmlflow.div
import htmlflow.view
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Input
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class NestedBuildersTest {
    @Test
    fun `@DslMarker annotation prevents nested modifier blocks`() {
        val validExample =
            view<Unit> {
                div {
                    attrId("demo")
                    input {
                        attrType(EnumTypeInputType.TEXT)
                        attrPlaceholder("Search...")
                        dataBind("search")
                        dataOn(Input) {
                            get("/search")
                            modifiers {
                                debounce(200.milliseconds)
                            }
                        }
                    }
                }
            }

        val expected =
            """
            <div id="demo">
            	<input type="text" placeholder="Search..." data-bind="search" data-on:input__debounce.200ms="@get('/search')">
            </div>
            """.trimIndent()
        assertEquals(expected, validExample.render())

        // INVALID: The following code structure would NOT compile
        // due to the @DslMarker restriction on ModifierAccumulator.
        //
        // Uncommenting this would produce compiler errors like:
        // "fun modifiers(...) cannot be called in this context with implicit receiver"
        //
        //     val invalidExample = view<Unit> {
        //         div {
        //             dataOn(Input) {
        //                 get("/search")
        //                 modifiers {
        //                     debounce(200.milliseconds)
        //                     modifiers {  // ← ERROR: Cannot nest modifiers!
        //                         post("/should-not-work")
        //                         modifiers { get("also-not-allowed") }  // ← ERROR
        //                     }
        //                 }
        //             }
        //         }
        //     }
    }
}

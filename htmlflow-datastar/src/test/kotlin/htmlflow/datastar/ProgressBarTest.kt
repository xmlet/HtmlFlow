package htmlflow.datastar

import htmlflow.html
import htmlflow.l
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.svg
import kotlin.test.assertEquals

class ProgressBarTest {
    @Test
    fun `Progress bar of the Datastar Frontend Reactivity`() {
        val out = StringBuilder()
        demoDastarRx.setOut(out).write()
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<Unit> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        attrId("progress-bar")
                        dataInit("@get('/examples/progress_bar/updates', {openWhenHidden: true})")
                        comment("When progress is less than 100%")
                        svg {
                            attrWidth(200)
                            attrHeight(200)
                            addAttr("viewbox", "-25 -25 250 250")
                            attrStyle("transform: rotate(-90deg)")

                            custom("circle")
                                .addAttr("r", "90")
                                .addAttr("cx", "100")
                                .addAttr("cy", "100")
                                .addAttr("fill", "transparent")
                                .addAttr("stroke", "#e0e0e0")
                                .addAttr("stroke-width", "16px")
                                .addAttr("stroke-dasharray", "565.48px")
                                .addAttr("stroke-dashoffset", "565px")
                                .l

                            custom("circle")
                                .addAttr("r", "90")
                                .addAttr("cx", "100")
                                .addAttr("cy", "100")
                                .addAttr("fill", "transparent")
                                .addAttr("stroke", "#6bdba7")
                                .addAttr("stroke-width", "16px")
                                .addAttr("stroke-linecap", "round")
                                .addAttr("stroke-dashoffset", "282px")
                                .addAttr("stroke-dasharray", "565.48px")
                                .l

                            custom("text")
                                .addAttr("x", "44")
                                .addAttr("y", "115")
                                .addAttr("fill", "#6bdba7")
                                .addAttr("font-size", "52")
                                .addAttr("font-weight", "bold")
                                .addAttr("style", "transform:rotate(90deg) translate(0px, -196px)")
                                .text("50%")
                                .l
                        }
                        comment("When progress is 100%")
                        button {
                            val fetching = dataIndicator("fetching")
                            dataAttr("aria-disabled", "`${'$'}{$fetching}`")
                            dataOn(
                                "click",
                                "!$fetching && @get('/examples/progress_bar/updates', {openWhenHidden: true})",
                            )
                            i { attrClass("material-symbols:check-circle") }
                            +"Completed! Try again?"
                        }
                    }
                }
            }
        }

    private val expectedDatastarRx =
        """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="progress-bar" data-init="@get('/examples/progress_bar/updates', {openWhenHidden: true})">
                    <!-- When progress is less than 100% -->
                    <svg width="200" height="200" viewbox="-25 -25 250 250" style="transform: rotate(-90deg)">
                        <circle r="90" cx="100" cy="100" fill="transparent" stroke="#e0e0e0" stroke-width="16px" stroke-dasharray="565.48px" stroke-dashoffset="565px">
                        </circle>
                        <circle r="90" cx="100" cy="100" fill="transparent" stroke="#6bdba7" stroke-width="16px" stroke-linecap="round" stroke-dashoffset="282px" stroke-dasharray="565.48px">
                        </circle>
                        <text x="44" y="115" fill="#6bdba7" font-size="52" font-weight="bold" style="transform:rotate(90deg) translate(0px, -196px)">
                            50%
                        </text>
                    </svg>
                    <!-- When progress is 100% -->
                    <button data-indicator-fetching="" data-attr-aria-disabled="`${'$'}{${'$'}fetching}`" data-on-click="!${'$'}fetching && @get('/examples/progress_bar/updates', {openWhenHidden: true})">
                        <i class="material-symbols:check-circle">
                        </i>
                        Completed! Try again?
                    </button>
                </div>
            </body>
        </html>
        """
}

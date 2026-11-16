@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressiveLoadTest {
    @Test
    fun `Progressive Load of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        StringBuilder()
            .apply {
                doc {
                    html {
                        head {
                            script {
                                attrType(EnumTypeScriptType.MODULE)
                                attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                            }
                        }
                        body {
                            div {
                                div {
                                    attrClass("actions")
                                    button {
                                        attrId("load-button")
                                        val loadDisabled = dataSignal("load-disabled", false)
                                        dataOn(Click) {
                                            loadDisabled.setValue(true)
                                            get(::progressiveLoadUpdates)
                                        }
                                        dataAttr("disabled") { +loadDisabled }
                                        dataIndicator("progressive-Load")
                                        text("Load")
                                    }
                                    comment("Indicator element")
                                }
                                p { text("Each part is loaded randomly and progressively.") }
                            }
                            div {
                                attrId("Load")
                                header {
                                    attrId("header")
                                    text("Welcome to my blog")
                                }
                                buildArticleSection()
                                buildCommentsSection()
                                div {
                                    attrId("footer")
                                    text("Hope you like it")
                                }
                            }
                        }
                    }
                }
            }

    fun Div<*>.buildArticleSection() {
        section {
            attrId("article")
            h4 { text("This is my article") }
            section {
                attrId("articleBody")
                p { text("Lorem ipsum dolor sit amet...") }
            }
        }
    }

    fun Div<*>.buildCommentsSection() {
        section {
            attrId("comments")
            h5 { text("Comments") }
            p { text("This is the comments section. It will also be progressively loaded as you scroll down.") }
            ul {
                attrId("comments-list")
                li {
                    attrId("1")
                    img {
                        attrSrc("https://avatar.iran.liara.run/username?username=example")
                        attrAlt("Avatar")
                        attrClass("avatar")
                    }
                    text("This is a comment...")
                }
                comment("More comments loaded progressively")
            }
        }
    }

    @Path("/examples/progressive_load/updates")
    private fun progressiveLoadUpdates() {}

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div>
        <div class="actions">
            <button id="load-button" data-signals="{'load-disabled': false}" data-on:click="$load-disabled = true; @get('/examples/progressive_load/updates')" data-attr:disabled="$load-disabled" data-indicator="progressive-Load">
                Load
            </button>
            <!-- Indicator element -->
        </div>
        <p>
            Each part is loaded randomly and progressively.
        </p>
    </div>
    <div id="Load">
        <header id="header">
            Welcome to my blog
        </header>
        <section id="article">
            <h4>
                This is my article
            </h4>
            <section id="articleBody">
                <p>
                    Lorem ipsum dolor sit amet...
                </p>
            </section>
        </section>
        <section id="comments">
            <h5>
                Comments
            </h5>
            <p>
                This is the comments section. It will also be progressively loaded as you scroll down.
            </p>
            <ul id="comments-list">
                <li id="1">
                    <img src="https://avatar.iran.liara.run/username?username=example" alt="Avatar" class="avatar">
                    This is a comment...
                </li>
                <!-- More comments loaded progressively -->
            </ul>
        </section>
        <div id="footer">
            Hope you like it
        </div>
    </div>
</body>
</html>
    """
}

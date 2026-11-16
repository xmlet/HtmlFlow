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

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.attributes.dataComputed
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataShow
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class DatastarTest {
    @Test
    fun `test example of the Datastar Frontend Reactivity`() {
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
                                val response = dataSignal("response")
                                val answer = dataSignal("answer", "bread")
                                val correct =
                                    dataComputed("correct") {
                                        +"$response.toLowerCase() == $answer"
                                    }
                                div {
                                    attrId("question")
                                    text("What do you put in a toaster?")
                                }
                                button {
                                    dataOn(Click) {
                                        +"$response = prompt('Answer:') ?? ''"
                                    }
                                    text("BUZZ")
                                }
                                div {
                                    dataShow {
                                        +"$response != ''"
                                    }
                                    raw("You answered \"")
                                    span {
                                        dataText {
                                            +response
                                        }
                                    }
                                    raw("\".")
                                    span {
                                        dataShow {
                                            +correct
                                        }
                                        text("That is correct ✅")
                                    }
                                    span {
                                        dataShow {
                                            !correct
                                        }
                                        raw("The correct answer is \"")
                                        span { dataText { +answer } }
                                        raw("\" 🤷")
                                    }
                                }
                            }
                        }
                    }
                }
            }

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div data-signals="{response: ""}" data-signals="{answer: 'bread'}" data-computed="{correct: () => $response.toLowerCase() == $answer}">
        <div id="question">
            What do you put in a toaster?
        </div>
        <button data-on:click="$response = prompt('Answer:') ?? ''">
            BUZZ
        </button>
        <div data-show="$response != ''">
            You answered "
            <span data-text="$response">
            </span>
            ".
            <span data-show="$correct">
                That is correct ✅
            </span>
            <span data-show="!$correct">
                The correct answer is "
                <span data-text="$answer">
                </span>
                " 🤷
            </span>
        </div>
    </div>
</body>
</html>
    """
}

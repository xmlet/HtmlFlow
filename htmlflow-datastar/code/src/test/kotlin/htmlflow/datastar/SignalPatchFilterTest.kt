package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlflow.datastar.attributes.dataOnSignalPatchFilter
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalPatchFilterTest {
    @Test
    fun `Checks include filter only`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataOnSignalPatchFilter {
                                    include = Regex("^counter$")
                                }
                            }
                        }
                    }
                }.toString()

        assertEquals(expectedIncludeOnlyHtml, html)
    }

    @Test
    fun `renders include and exclude filters`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataOnSignalPatchFilter {
                                    include = Regex("user")
                                    exclude = Regex("password")
                                }
                            }
                        }
                    }
                }.toString()

        assertEquals(expectedIncludeAndExcludeHtml, html)
    }

    @Test
    fun `escapes slash characters in regex filters`() {
        val html =
            StringBuilder()
                .apply {
                    doc {
                        html {
                            div {
                                dataOnSignalPatchFilter {
                                    include = Regex("users/active")
                                }
                            }
                        }
                    }
                }.toString()

        assertEquals(expectedEscapedRegexHtml, html)
    }

    private val expectedIncludeOnlyHtml =
        """
        <!DOCTYPE html>
        <html>
        	<div data-on-signal-patch-filter="{include: /^counter$/}">
        	</div>
        </html>
        """.trimIndent()

    private val expectedIncludeAndExcludeHtml =
        """
        <!DOCTYPE html>
        <html>
        	<div data-on-signal-patch-filter="{include: /user/, exclude: /password/}">
        	</div>
        </html>
        """.trimIndent()

    private val expectedEscapedRegexHtml =
        """
        <!DOCTYPE html>
        <html>
        	<div data-on-signal-patch-filter="{include: /users\/active/}">
        	</div>
        </html>
        """.trimIndent()
}

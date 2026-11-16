package htmlflow.datastar

import org.xmlet.htmlflow.datastar.expressions.ActionOptions
import org.xmlet.htmlflow.datastar.expressions.ContentType
import org.xmlet.htmlflow.datastar.expressions.RequestCancellationMode
import org.xmlet.htmlflow.datastar.expressions.RetryMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ActionsOptionsTests {
    @Test
    fun `ActionOptions with only contentType JSON`() {
        val options =
            ActionOptions().apply {
                contentType = ContentType.JSON
            }
        assertEquals("{contentType: 'json'}", options.toString())
    }

    @Test
    fun `ActionOptions with only contentType FORM`() {
        val options =
            ActionOptions().apply {
                contentType = ContentType.FORM
            }
        assertEquals("{contentType: 'form'}", options.toString())
    }

    @Test
    fun `ActionOptions with only selector`() {
        val options =
            ActionOptions().apply {
                selector = "#my-element"
            }
        assertEquals("{selector: '#my-element'}", options.toString())
    }

    @Test
    fun `ActionOptions with only headers`() {
        val options =
            ActionOptions().apply {
                headers = "Authorization: Bearer token"
            }
        assertEquals("{headers: \"Authorization: Bearer token\"}", options.toString())
    }

    @Test
    fun `ActionOptions with only openWhenHidden true`() {
        val options =
            ActionOptions().apply {
                openWhenHidden = true
            }
        assertEquals("{openWhenHidden: true}", options.toString())
    }

    @Test
    fun `ActionOptions with only openWhenHidden false`() {
        val options =
            ActionOptions().apply {
                openWhenHidden = false
            }
        assertEquals("{openWhenHidden: false}", options.toString())
    }

    @Test
    fun `ActionOptions with only payload`() {
        val options =
            ActionOptions().apply {
                payload = "custom_data"
            }
        assertEquals("{payload: \"custom_data\"}", options.toString())
    }

    @Test
    fun `ActionOptions with only retry AUTO`() {
        val options =
            ActionOptions().apply {
                retry = RetryMode.AUTO
            }
        assertEquals("{retry: 'auto'}", options.toString())
    }

    @Test
    fun `ActionOptions with only retry ERROR`() {
        val options =
            ActionOptions().apply {
                retry = RetryMode.ERROR
            }
        assertEquals("{retry: 'error'}", options.toString())
    }

    @Test
    fun `ActionOptions with only retry ALWAYS`() {
        val options =
            ActionOptions().apply {
                retry = RetryMode.ALWAYS
            }
        assertEquals("{retry: 'always'}", options.toString())
    }

    @Test
    fun `ActionOptions with only retry NEVER`() {
        val options =
            ActionOptions().apply {
                retry = RetryMode.NEVER
            }
        assertEquals("{retry: 'never'}", options.toString())
    }

    @Test
    fun `ActionOptions with only retryInterval in milliseconds`() {
        val options =
            ActionOptions().apply {
                retryInterval = 500.milliseconds
            }
        assertEquals("{retryInterval: 500}", options.toString())
    }

    @Test
    fun `ActionOptions with only retryInterval in seconds`() {
        val options =
            ActionOptions().apply {
                retryInterval = 2.seconds
            }
        assertEquals("{retryInterval: 2000}", options.toString())
    }

    @Test
    fun `ActionOptions with only retryScaler`() {
        val options =
            ActionOptions().apply {
                retryScaler = 2
            }
        assertEquals("{retryScaler: 2}", options.toString())
    }

    @Test
    fun `ActionOptions with only retryMaxCount`() {
        val options =
            ActionOptions().apply {
                retryMaxCount = 5
            }
        assertEquals("{retryMaxCount: 5}", options.toString())
    }

    @Test
    fun `ActionOptions with only requestCancellation AUTO`() {
        val options =
            ActionOptions().apply {
                requestCancellation = RequestCancellationMode.AUTO
            }
        assertEquals("{requestCancellation: 'auto'}", options.toString())
    }

    @Test
    fun `ActionOptions with only requestCancellation CLEANUP`() {
        val options =
            ActionOptions().apply {
                requestCancellation = RequestCancellationMode.CLEANUP
            }
        assertEquals("{requestCancellation: 'cleanup'}", options.toString())
    }

    @Test
    fun `ActionOptions with only requestCancellation DISABLED`() {
        val options =
            ActionOptions().apply {
                requestCancellation = RequestCancellationMode.DISABLED
            }
        assertEquals("{requestCancellation: 'disabled'}", options.toString())
    }

    @Test
    fun `ActionOptions with empty filterSignals`() {
        val options =
            ActionOptions().apply {
                filterSignals = {}
            }
        assertEquals("", options.toString())
    }

    @Test
    fun `ActionOptions with only filterSignals include`() {
        val options =
            ActionOptions().apply {
                filterSignals = {
                    include = Regex("^counter$")
                }
            }
        assertEquals("{filterSignals: {include: /^counter$/}}", options.toString())
    }

    @Test
    fun `ActionOptions with only filterSignals exclude`() {
        val options =
            ActionOptions().apply {
                filterSignals = {
                    exclude = Regex("^password$")
                }
            }
        assertEquals("{filterSignals: {exclude: /^password$/}}", options.toString())
    }

    @Test
    fun `ActionOptions with filterSignals include and exclude`() {
        val options =
            ActionOptions().apply {
                filterSignals = {
                    include = Regex("user")
                    exclude = Regex("password")
                }
            }
        assertEquals("{filterSignals: {include: /user/, exclude: /password/}}", options.toString())
    }

    @Test
    fun `ActionOptions with multiple basic properties`() {
        val options =
            ActionOptions().apply {
                contentType = ContentType.JSON
                selector = "#form"
                openWhenHidden = true
            }
        assertEquals(
            "{contentType: 'json', selector: '#form', openWhenHidden: true}",
            options.toString(),
        )
    }

    @Test
    fun `ActionOptions with retry configuration`() {
        val options =
            ActionOptions().apply {
                retry = RetryMode.AUTO
                retryInterval = 1.seconds
                retryScaler = 2
                retryMaxCount = 3
            }
        assertEquals(
            "{retry: 'auto', retryInterval: 1000, retryScaler: 2, retryMaxCount: 3}",
            options.toString(),
        )
    }

    @Test
    fun `ActionOptions with all properties set`() {
        val options =
            ActionOptions().apply {
                contentType = ContentType.FORM
                filterSignals = {
                    include = Regex("^data")
                }
                selector = ".update-btn"
                headers = "X-Custom-Header: value"
                openWhenHidden = false
                payload = "extra_info"
                retry = RetryMode.ERROR
                retryInterval = 500.milliseconds
                retryScaler = 3
                retryMaxCount = 5
                requestCancellation = RequestCancellationMode.CLEANUP
            }
        val result = options.toString()

        // Verify all properties are present in the output
        assert(result.contains("contentType: 'form'")) { "Missing contentType in: $result" }
        assert(result.contains("filterSignals: {include: /^data/}")) { "Missing filterSignals in: $result" }
        assert(result.contains("selector: '.update-btn'")) { "Missing selector in: $result" }
        assert(result.contains("headers: \"X-Custom-Header: value\"")) { "Missing headers in: $result" }
        assert(result.contains("openWhenHidden: false")) { "Missing openWhenHidden in: $result" }
        assert(result.contains("payload: \"extra_info\"")) { "Missing payload in: $result" }
        assert(result.contains("retry: 'error'")) { "Missing retry in: $result" }
        assert(result.contains("retryInterval: 500")) { "Missing retryInterval in: $result" }
        assert(result.contains("retryScaler: 3")) { "Missing retryScaler in: $result" }
        assert(result.contains("retryMaxCount: 5")) { "Missing retryMaxCount in: $result" }
        assert(result.contains("requestCancellation: 'cleanup'")) { "Missing requestCancellation in: $result" }
        assert(result.startsWith("{") && result.endsWith("}")) { "Result should be wrapped in braces" }
    }

    @Test
    fun `ActionOptions properties can be updated after initialization`() {
        val options = ActionOptions()
        assertEquals("", options.toString())

        options.contentType = ContentType.JSON
        assertEquals("{contentType: 'json'}", options.toString())

        options.selector = "#element"
        assert(options.toString().contains("contentType: 'json'"))
        assert(options.toString().contains("selector: '#element'"))
    }

    @Test
    fun `ActionOptions with complex selector`() {
        val options =
            ActionOptions().apply {
                selector = "div.container > button[data-action='save']"
            }
        assertEquals("{selector: 'div.container > button[data-action=\\'save\\']'}", options.toString())
    }

    @Test
    fun `ActionOptions with headers containing special characters`() {
        val options =
            ActionOptions().apply {
                headers = "Content-Type: application/json; charset=utf-8"
            }
        assertEquals("{headers: \"Content-Type: application/json; charset=utf-8\"}", options.toString())
    }

    @Test
    fun `ActionOptions escapes string literal control characters`() {
        val options =
            ActionOptions().apply {
                selector = "button[data-action='save']\n.active"
                headers = "X-Trace: \"quoted\"\\value"
                payload = "line1\nline2"
            }

        assertEquals(
            "{selector: 'button[data-action=\\'save\\']\\n.active', headers: \"X-Trace: \\\"quoted\\\"\\\\value\", payload: \"line1\\nline2\"}",
            options.toString(),
        )
    }
}

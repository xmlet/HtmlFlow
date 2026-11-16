package htmlflow.datastar

import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.signal
import kotlin.test.Test
import kotlin.test.assertEquals

class SignalFactoryTest {
    @Test
    fun `public signal factory creates typed signals`() {
        val fetching: Signal<Boolean> = signal("fetching")

        assertEquals("fetching", fetching.name)
        assertEquals("$fetching", fetching.toString())
    }
}

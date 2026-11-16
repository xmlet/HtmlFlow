package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.junit.jupiter.api.Test
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import org.xmlet.htmlflow.datastar.expressions.Signal
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ExpressionBuilderTests {
    @Test
    fun `Expression builder testing referencing a signal two times to see if its not removed from the expression`() {
        val fetching = Signal<Boolean>("fetching")
        val builder = ExpressionBuilder()
        with(builder) {
            +fetching
            fetching.setValue(false) and get(::someHandler)
        }

        assertEquals("$fetching; ($fetching = false) && @get('/some/url')", builder.getExpression())
    }

    @Path("/some/url")
    private fun someHandler() {}

    @Test
    fun `Expression builder testing referencing a signal two times to see if its removed from the expression`() {
        val fetching = Signal<Boolean>("fetching")
        val other = Signal<Boolean>("other")
        val builder = ExpressionBuilder()
        with(builder) {
            +fetching
            get(::someHandler) and post(::someHandler)
            other.setValue(fetching)
        }

        assertEquals("$fetching; @get('/some/url') && @post('/some/url'); $other = $fetching", builder.getExpression())
    }

    @Test
    fun `Expression builder testing referencing a DataStarCompoundExprElem two times to see if its removed from the expression`() {
        val fetching = Signal<Boolean>("fetching")
        val other = Signal<Boolean>("other")
        val builder = ExpressionBuilder()
        with(builder) {
            !fetching
            get(::someHandler)
            other.setValue(!fetching)
        }

        assertEquals("!$fetching; @get('/some/url'); $other = !$fetching", builder.getExpression())
    }

    @Test
    fun `Expression builder test with multiple js operators`() {
        val fetching = Signal<Boolean>("fetching")
        val other = Signal<Boolean>("other")
        val builder = ExpressionBuilder()
        with(builder) {
            fetching.setValue(!fetching) and get(::someHandler) or other.setValue(fetching)
            !fetching
        }
        assertEquals("($fetching = !$fetching) && @get('/some/url') || ($other = $fetching); !$fetching", builder.getExpression())
    }

    @Test
    fun `Expression builder test where both parameters of js operator are js operators`() {
        val count1 = Signal<Int>("count1")
        val count2 = Signal<Int>("count2")
        val count3 = Signal<Int>("count3")
        val builder = ExpressionBuilder()
        with(builder) {
            (count1 eq 1) or (count2 eq 2) and (count3 eq 3)
        }
        assertEquals("($count1 == 1 || $count2 == 2) && $count3 == 3", builder.getExpression())
    }

    data class User(
        val name: String,
        val age: Int,
    )

    @Test
    fun `Expression with Signal on call`() {
        val builder = ExpressionBuilder()
        StringBuilder()
            .apply {
                doc {
                    html {
                        div {
                            val (user1) =
                                dataSignals(
                                    "user1" to User("John Doe", 30),
                                    "user2" to User("Chuck Norris", 50),
                                )
                            with(builder) {
                                assertEquals(
                                    $$"$user1.name",
                                    user1.on(User::name).syntax,
                                )
                                assertEquals(
                                    $$"$user1.age",
                                    user1.on(User::age).syntax,
                                )
                            }
                        }
                    }
                }
            }
    }

    @Test
    fun `Expression builder test where both parameters are equal`() {
        val count1 = Signal<Int>("count1")
        val count2 = Signal<Int>("count2")
        val builder = ExpressionBuilder()
        with(builder) {
            (count1 eq 1) eq (count2 eq 1)
        }
        assertEquals("($count1 == 1) == ($count2 == 1)", builder.getExpression())
    }

    @Test
    fun `Expression builder preserves explicit logical grouping with JavaScript precedence`() {
        val count1 = Signal<Int>("count1")
        val count2 = Signal<Int>("count2")
        val count3 = Signal<Int>("count3")
        val builder = ExpressionBuilder()
        with(builder) {
            (count1 eq 1) or ((count2 eq 2) and (count3 eq 3))
        }

        assertEquals("$count1 == 1 || $count2 == 2 && $count3 == 3", builder.getExpression())
    }

    @Test
    fun `Expression builder removes standalone operands after composing logical action expression`() {
        val builder = ExpressionBuilder()
        with(builder) {
            val fetchUsers = get(::someHandler)
            val postUsers = post(::someHandler)
            fetchUsers and postUsers
        }

        val expression = builder.getExpression()

        assertEquals("@get('/some/url') && @post('/some/url')", expression)
        assertFalse(expression.contains("; @get('/some/url')"))
        assertFalse(expression.contains("; @post('/some/url')"))
    }

    @Test
    fun `Expression builder preserves independent statements before composed expressions`() {
        val fetching = Signal<Boolean>("_fetching")
        val builder = ExpressionBuilder()
        with(builder) {
            +fetching
            !fetching and get(::someHandler)
        }

        val expression = builder.getExpression()

        assertEquals("$fetching; !$fetching && @get('/some/url')", expression)
        assertEquals(2, expression.split("; ").size)
    }
}

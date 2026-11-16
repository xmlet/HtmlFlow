package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class DataStarActionsTests {
    @Test
    fun `actions toString() must return correct js string `() {
        val handler = ExpressionBuilder()
        StringBuilder()
            .apply {
                doc {
                    html {
                        div {
                            val bar = dataSignal("bar", "initialValue")
                            val peekDataStarAction = handler.peek("() => $bar")
                            assertEquals("@peek(() => $bar)", peekDataStarAction.syntax)
                        }
                    }
                }
            }

        val setAllActions = handler.setAll(true) { include = Regex("^foo$") }
        assertEquals("@setAll(true, {include: /^foo$/})", setAllActions.syntax)

        val toggleAllDataStarAction = handler.toggleAll { include = Regex("^foo$") }
        assertEquals("@toggleAll({include: /^foo$/})", toggleAllDataStarAction.syntax)

        val getDataStarAction = handler.get(::getUsers)
        assertEquals("@get('/users')", getDataStarAction.syntax)

        val patchDataStarAction = handler.patch(::patchUsers)
        assertEquals("@patch('/users')", patchDataStarAction.syntax)

        val postDataStarAction = handler.post(::createUser)
        assertEquals("@post('/users')", postDataStarAction.syntax)

        val putDataStarAction = handler.put(::editUser)
        assertEquals("@put('/users')", putDataStarAction.syntax)

        val deleteDataStarAction = handler.delete(::deleteUser)
        assertEquals("@delete('/users')", deleteDataStarAction.syntax)

        val postPathDataStarAction = handler.post("/users")
        assertEquals("@post('/users')", postPathDataStarAction.syntax)

        val putPathDataStarAction = handler.put("/users")
        assertEquals("@put('/users')", putPathDataStarAction.syntax)

        val patchPathDataStarAction = handler.patch("/users")
        assertEquals("@patch('/users')", patchPathDataStarAction.syntax)
    }

    @Test
    fun `backend action paths escape JavaScript string literals`() {
        val handler = ExpressionBuilder()

        assertEquals("@get('/users/\\'active\\'')", handler.get("/users/'active'").syntax)
        assertEquals("@post('/users\\\\archive')", handler.post("/users\\archive").syntax)
    }

    @Path("/users")
    fun getUsers() {}

    @Path("/users")
    fun patchUsers() {}

    @Path("/users")
    fun createUser() { }

    @Path("/users")
    fun editUser() { }

    @Path("/users")
    fun deleteUser() { }
}

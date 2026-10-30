/*
 * MIT License
 *
 * Copyright (c) 2026, xmlet HtmlFlow
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

package htmlflow.test

import htmlflow.HtmlFlow
import htmlflow.HtmlPage
import htmlflow.dyn
import htmlflow.forEachIndexedOf
import htmlflow.forEachOf
import htmlflow.html
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.xmlet.htmlapifaster.Indexed
import org.xmlet.htmlapifaster.Slot
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.ol
import org.xmlet.htmlapifaster.ul

/**
 * Replicates for Kotlin the original Java TestWhenAndNesting test forEachOf and whenOf,
 * checked against the {@code dynamic()} block they replace.
 */
class TestWhenAndNestingExtensions {
    class Group internal constructor(
        val name: String,
        vararg members: String,
    ) {
        val members: MutableList<String> = mutableListOf(*members)
    }

    private val groups: List<Group> =
        listOf(
            Group("empty"),
            Group("pair", "ana", "bob"),
            Group("single", "cat"),
        )

    @Test
    fun a_loop_and_a_conditional_nest_inside_a_loop() {
        val expected =
            HtmlFlow
                .view<Iterable<Group>>(::nestedAsDynamic)
                .render(groups)
        Assertions.assertEquals(
            expected,
            HtmlFlow
                .view<Iterable<Group>>(::nestedTemplate)
                .render(groups),
        )
    }

    @Test
    fun forEachIndexedOf_numbers_the_items_from_0_on_every_render() {
        val expected =
            HtmlFlow
                .view<Iterable<Group>>(::indexedAsDynamic)
                .render(groups)
        Assertions.assertEquals(
            expected,
            HtmlFlow
                .view<Iterable<Group>>(::indexedTemplate)
                .render(groups),
        )
    }

    /**
     * Second version of the same test.
     * Due to coverage issue on inline function it does not detect the call.
     */
    @Test
    fun forEachIndexedOf_test_2() {
        val expected =
            HtmlFlow
                .view<Iterable<Group>>(::indexedAsDynamic)
                .render(groups)
        Assertions.assertEquals(
            expected,
            HtmlFlow
                .view<Iterable<Group>>(::indexedTemplate2)
                .render(groups),
        )
    }

    private fun nestedAsDynamic(page: HtmlPage) {
        page.html {
            body {
                ul {
                    dyn { groups: Iterable<Group> ->
                        groups.forEach { g ->
                            li {
                                text(g.name)
                                if (!g.members.isEmpty()) {
                                    ul {
                                        g.members.forEach { member ->
                                            li { text(member) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun nestedTemplate(page: HtmlPage) {
        page.html {
            body {
                ul {
                    forEachOf { group: Slot<Group> ->
                        li {
                            textOf(group.read { it.name })
                            whenOf(group.readBool { it.members.isNotEmpty() }) { li, members ->
                                li.ul {
                                    forEachOf(
                                        members.read { it.members },
                                    ) { member: Slot<String> ->
                                        li {
                                            textOf(member.read { it })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun indexedTemplate(page: HtmlPage) {
        page.html {
            body {
                ol {
                    forEachIndexedOf { row: Slot<Indexed<Group>> ->
                        li {
                            attrOf("class", row.read { if (it.index() % 2 == 0) "even" else "odd" })
                            intOf(row.readInt { it.index() + 1 })
                            textOf(row.read { " " + it.item().name })
                        }
                    }
                }
            }
        }
    }

    /**
     * Second version of the same template but using the overloaded builder forEachIndexedOf
     * with 2 arguments.
     * Due to coverage issue on inline function it does not detect the call.
     */
    private fun indexedTemplate2(page: HtmlPage) {
        page.html {
            body {
                ol {
                    forEachIndexedOf({ model: Iterable<Group> -> model }) { row: Slot<Indexed<Group>> ->
                        li {
                            attrOf("class", row.read { if (it.index() % 2 == 0) "even" else "odd" })
                            intOf(row.readInt { it.index() + 1 })
                            textOf(row.read { " " + it.item().name })
                        }
                    }
                }
            }
        }
    }

    private fun indexedAsDynamic(page: HtmlPage) {
        page.html {
            body {
                ol {
                    dyn { groups: List<Group> ->
                        for (i in 0 until groups.size) {
                            li {
                                attrClass(if (i % 2 == 0) "even" else "odd")
                                text(i + 1)
                                text(" " + groups[i].name)
                            }
                        }
                    }
                }
            }
        }
    }
}

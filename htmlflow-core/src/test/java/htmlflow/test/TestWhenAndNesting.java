package htmlflow.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * The two control-flow slots, checked against the {@code dynamic()} block they replace. Both are
 * preencoded, so the failure to catch is right text at the wrong indentation. The preprocessor
 * records a sub-chain and has to hand the outer recording back exactly as it found it. A dynamic
 * block re-emits everything interpretively and so cannot get the depth wrong.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestWhenAndNesting {

    static final class Group {

        final String name;
        final List<String> members;

        Group(String name, String... members) {
            this.name = name;
            this.members = Arrays.asList(members);
        }
    }

    private static final List<Group> GROUPS = Arrays.asList(
        new Group("empty"),
        new Group("pair", "ana", "bob"),
        new Group("single", "cat")
    );

    private static void whenTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .div()
            .whenOf(
                (List<Group> g) -> g.size() > 2,
                (div, all) -> div.p().textOf(all.read(g -> "many:" + g.size())).__(),
                (div, all) -> div.p().textOf(all.read(g -> "few:" + g.size())).__()
            )
            .whenOf((List<Group> g) -> g.isEmpty(), div -> div.p().text("none").__())
            .__()
            .__()
            .__();
    }

    private static void whenAsDynamic(HtmlPage page) {
        page
            .html()
            .body()
            .div()
            .<List<Group>>dynamic((div, g) -> {
                if (g.size() > 2) div.p().text("many:" + g.size()).__();
                else div.p().text("few:" + g.size()).__();
                if (g.isEmpty()) div.p().text("none").__();
            })
            .__()
            .__()
            .__();
    }

    @Test
    void whenOf_matches_the_dynamic_block_it_replaces() {
        HtmlView<List<Group>> slots = HtmlFlow.view(TestWhenAndNesting::whenTemplate);
        String expected = HtmlFlow
            .<List<Group>>view(TestWhenAndNesting::whenAsDynamic)
            .render(GROUPS);
        assertEquals(expected, slots.render(GROUPS));
        assertEquals(expected, slots.render(GROUPS), "second render differs from the first");
        assertEquals(
            expected,
            Utils.renderThroughLinkedChain(TestWhenAndNesting::whenTemplate, GROUPS),
            "linked-chain fallback differs from the generated class"
        );
    }

    private static void nestedTemplate(HtmlPage page) {
        page
            .html()
            .body()
            .ul()
            .forEachOf((List<Group> groups) -> groups, (ul, group) ->
                ul
                    .li()
                    .textOf(group.read(g -> g.name))
                    .whenOf(
                        group.readBool(g -> !g.members.isEmpty()),
                        (li, g2) ->
                            li
                                .ul()
                                .forEachOf(g2.read(g -> g.members), (inner, member) ->
                                    inner.li().textOf(member.read(m -> m)).__()
                                )
                                .__()
                    )
                    .__()
            )
            .__()
            .__()
            .__();
    }

    private static void nestedAsDynamic(HtmlPage page) {
        page
            .html()
            .body()
            .ul()
            .<List<Group>>dynamic((ul, groups) -> {
                for (Group g : groups) {
                    var li = ul.li().text(g.name);
                    if (!g.members.isEmpty()) {
                        var inner = li.ul();
                        for (String m : g.members) inner.li().text(m).__();
                        inner.__();
                    }
                    li.__();
                }
            })
            .__()
            .__()
            .__();
    }

    @Test
    void a_loop_and_a_conditional_nest_inside_a_loop() {
        String expected = HtmlFlow
            .<List<Group>>view(TestWhenAndNesting::nestedAsDynamic)
            .render(GROUPS);
        assertEquals(
            expected,
            HtmlFlow.<List<Group>>view(TestWhenAndNesting::nestedTemplate).render(GROUPS)
        );
        assertEquals(
            expected,
            Utils.renderThroughLinkedChain(TestWhenAndNesting::nestedTemplate, GROUPS),
            "linked-chain fallback differs from the generated class"
        );
    }
}

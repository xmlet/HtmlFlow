package htmlflow.viewloader.views.objects

import htmlflow.HtmlFlow
import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.viewloader.ComplexTestViewModel
import htmlflow.viewloader.SimpleTestViewModel
import htmlflow.viewloader.SimpleTestViewModel3
import htmlflow.viewloader.SimpleTestViewModel4
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.h3
import org.xmlet.htmlapifaster.h4
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.title
import org.xmlet.htmlapifaster.ul

@Suppress("unused")
object ObjectTestViews {
    val complexView: HtmlView<ComplexTestViewModel> =
        HtmlFlow.view {
            it.html {
                head { title { text("Complex View") } }
                body {
                    div {
                        attrClass("complex")
                        h1 { text("Object Complex View") }
                        dyn { model: ComplexTestViewModel ->
                            h2 { text(model.title) }
                            if (model.isActive) {
                                div {
                                    attrClass("active")
                                    text("Status: Active")
                                }
                            }
                            ul { model.items.forEach { item -> li { text(item) } } }
                        }
                    }
                }
            }
        }
}


@Suppress("unused")
/** Regular class-based test views */
class ClassTestViews {
    val classView: HtmlView<SimpleTestViewModel3> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("class-view")
                        h3 { text("Class View") }
                        dyn { model: SimpleTestViewModel ->
                            p { text("Class content: ${model.content}") }
                        }
                    }
                }
            }
        }

    fun getClassMethodView(): HtmlView<SimpleTestViewModel4> {
        return HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("class-method")
                        h4 { text("Class Method View") }
                        dyn { model: SimpleTestViewModel ->
                            div { text("Class method: ${model.content}") }
                        }
                    }
                }
            }
        }
    }
}

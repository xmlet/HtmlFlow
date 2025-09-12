package htmlflow.viewloader.viewsfailing.duplicate

import htmlflow.HtmlFlow
import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.viewloader.SimpleTestViewModel
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.p

val simpleHtmlView: HtmlView<SimpleTestViewModel> =
    HtmlFlow.view {
        it.html {
            body {
                div {
                    attrClass("simple")
                    h1 { text("Simple View") }
                    dyn { model: SimpleTestViewModel ->
                        p { text("Content: ${model.content}") }
                    }
                }
            }
        }
    }

val simpleHtmlView2: HtmlView<SimpleTestViewModel> =
    HtmlFlow.view {
        it.html {
            body {
                div {
                    attrClass("simple")
                    h1 { text("Simple View") }
                    dyn { model: SimpleTestViewModel ->
                        p { text("Content: ${model.content}") }
                    }
                }
            }
        }
    }

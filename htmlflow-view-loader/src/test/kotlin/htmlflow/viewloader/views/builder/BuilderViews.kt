package htmlflow.viewloader.views.builder

import htmlflow.HtmlFlow
import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.viewloader.BuilderTestViewModel
import htmlflow.viewloader.BuilderTestViewModel2
import htmlflow.viewloader.BuilderTestViewModel3
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.p

@Suppress("unused")
val simpleBuilderView: HtmlView<BuilderTestViewModel2> =
    HtmlFlow.view {
        it.html {
            body {
                div {
                    attrClass("builder-configured")
                    h2 { text("Builder Configured View") }
                    dyn { model: BuilderTestViewModel2 ->
                        p { text("Builder content: ${model.content}") }
                    }
                }
            }
        }
    }

@Suppress("unused")
class BuilderViews(private val builder: HtmlFlow.ViewFactory) {

    val simpleBuilderView: HtmlView<BuilderTestViewModel> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("builder-configured")
                        h2 { text("Builder Configured View") }
                        dyn { model: BuilderTestViewModel ->
                            p { text("Builder content: ${model.content}") }
                        }
                    }
                }
            }
        }
}

@Suppress("unused")
class MultiParameterViews(builder: HtmlFlow.ViewFactory, private val config: String) {

    val multiParamView: HtmlView<BuilderTestViewModel3> =
        builder.view {
            it.html {
                body {
                    div {
                        attrClass("multi-param")
                        h2 { text("Multi Parameter View: $config") }
                        dyn { model: BuilderTestViewModel3 ->
                            p { text("Multi param: ${model.content}") }
                        }
                    }
                }
            }
        }
}

@Suppress("unused")
class NoBuilderViews {

    val defaultView: HtmlView<BuilderTestViewModel3> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("no-builder")
                        h2 { text("No Builder View") }
                        dyn { model: BuilderTestViewModel3 ->
                            p { text("Default: ${model.content}") }
                        }
                    }
                }
            }
        }
}

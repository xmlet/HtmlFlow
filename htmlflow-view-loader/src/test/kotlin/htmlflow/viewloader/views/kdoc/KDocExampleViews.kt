package htmlflow.viewloader.views.kdoc

import htmlflow.HtmlFlow
import htmlflow.HtmlView
import htmlflow.dyn
import htmlflow.html
import htmlflow.viewloader.BaseVm
import htmlflow.viewloader.ProfileLike
import htmlflow.viewloader.SecondaryInterface
import htmlflow.viewloader.UserVm
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.h2
import org.xmlet.htmlapifaster.h3
import org.xmlet.htmlapifaster.p

@Suppress("unused")
object KDocExampleViews {

    val userView: HtmlView<UserVm> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("user-view")
                        h1 { text("User Profile") }
                        dyn { model: UserVm -> p { text("Welcome, ${model.name}!") } }
                    }
                }
            }
        }

    val baseView: HtmlView<BaseVm> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("base-view")
                        h2 { text("Base View") }
                        dyn { model: BaseVm -> p { text("Base content: ${model.baseContent}") } }
                    }
                }
            }
        }

    val profileView: HtmlView<ProfileLike> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("profile-view")
                        h2 { text("Profile Interface View") }
                        dyn { model: ProfileLike -> p { text("Profile name: ${model.name}") } }
                    }
                }
            }
        }

    val secondaryView: HtmlView<SecondaryInterface> =
        HtmlFlow.view {
            it.html {
                body {
                    div {
                        attrClass("secondary-view")
                        h3 { text("Secondary Interface View") }
                        dyn { model: SecondaryInterface ->
                            p { text("Secondary: ${model.secondary}") }
                        }
                    }
                }
            }
        }
}

package org.xmlet.htmlflow.datastar.modifiers

enum class DomProperty(
    val tag: String,
) {
    VALUE("value"),
    CHECKED("checked"),
    SELECTED("selected"),
    DISABLED("disabled"),
    HIDDEN("hidden"),
    READ_ONLY("readOnly"),
    REQUIRED("required"),
    OPEN("open"),
}

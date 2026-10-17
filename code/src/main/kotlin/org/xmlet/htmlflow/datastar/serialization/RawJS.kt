package org.xmlet.htmlflow.datastar.serialization

/**
 * Indicates that the string received should not be changes or serialized.
 * @property js The Raw JavaScript string
 */
@JvmInline
value class RawJS(
    val js: String = "",
)

/**
 * Function that will ensure the String passed is not changed or serialized.
 * @receiver The String containing the raw JavaScript
 */
fun String.asRawJS(): RawJS = RawJS(this)

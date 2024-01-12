package htmlflow

import org.xmlet.htmlapifaster.Element

val <T : Element<*,*>, Z : Element<*,*>> Element<T, Z>.l: Z
    get() = this.`__`()


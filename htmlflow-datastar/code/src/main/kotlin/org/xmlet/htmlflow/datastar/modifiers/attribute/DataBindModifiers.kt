package org.xmlet.htmlflow.datastar.modifiers.attribute

import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.modifiers.DomProperty
import org.xmlet.htmlflow.datastar.modifiers.core.ModifierAccumulator

class DataBindModifiers : ModifierAccumulator() {
    fun prop(prop: DomProperty) = append("__prop.${prop.tag}")

    fun event(event: Event) = append("__event.$event")
}

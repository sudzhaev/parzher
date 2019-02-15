package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.events.Attribute as XmlAttribute
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement

operator fun StartElement.get(attributeName: String): String? {
    attributes.asSequence()
        .map { it as XmlAttribute }
        .map { it.name.localPart to it.value }
        .forEach { (name, value) ->
            if (attributeName == name) {
                return value
            }
        }
    return null
}

fun StartElement.localname(): String = name.localPart

fun EndElement.localname(): String = name.localPart

fun List<Attribute>.allMatch(startElement: StartElement)
        = all { (name, value) -> startElement[name] == value }

fun <T> Stack<T>.peekOrNull(): T? {
    return if (isNotEmpty()) peek() else null
}

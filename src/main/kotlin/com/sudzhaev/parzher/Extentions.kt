package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.Attribute as XmlAttribute

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

fun List<Attribute>.allMatch(startElement: StartElement) = all { (name, value) -> startElement[name] == value }

fun <T> Stack<T>.peekOrNull(): T? = if (isNotEmpty()) peek() else null

fun StartElement.extract(attributes: List<String>) = attributes.map { it to this[it] }.toMap()

fun <T> Iterable<Map<T, T?>>.sum(): Map<T, T?> {
    if (!iterator().hasNext()) {
        return emptyMap()
    }
    return reduce { acc, value -> acc + value }
}

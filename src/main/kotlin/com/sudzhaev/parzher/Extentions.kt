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

fun StartElement.extract(attributes: List<Extract<Any>>): Map<String, Any?> {
    return attributes
        .map { (attributeName, converter) -> attributeName to converter(this[attributeName]) }
        .toMap()
}

fun <T> Iterable<Map<T, T?>>.sum(): Map<T, T?> {
    return when {
        !iterator().hasNext() -> emptyMap()
        else -> reduce { acc, value -> acc + value }
    }
}

fun Stack<Pair<Tag, Map<String, Any?>>>.toMap() = map { it.second }.reduce { acc, map -> acc + map }

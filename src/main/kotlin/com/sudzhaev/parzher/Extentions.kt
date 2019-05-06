package com.sudzhaev.parzher

import java.util.*
import javax.xml.namespace.QName
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.Attribute as XmlAttribute

operator fun StartElement.get(attributeName: String) = getAttributeByName(QName.valueOf(attributeName))?.value

fun StartElement.name(): String = name.localPart

fun EndElement.name(): String = name.localPart

operator fun StartElement.contains(attributes: List<Attribute>) =
    attributes.all { (name, value) -> this[name] == value }

fun <T> Stack<T>.peekOrNull(): T? = if (isNotEmpty()) peek() else null

fun StartElement.getAttributes(attributes: List<Extract<*>>): Map<String, Any?> {
    return attributes
        .map { (attributeName, propertyName, converter) -> propertyName to converter(this[attributeName]) }
        .toMap()
}

fun <T, R> Iterable<Map<T, R?>>.reduceSumming(): Map<T, R?> {
    return when {
        !iterator().hasNext() -> emptyMap()
        else -> reduce { acc, value -> acc + value }
    }
}

fun Stack<Map<String, Any?>>.toMap() = reduceSumming()

fun String.simplify() = trimIndent().replace("\n", " ")

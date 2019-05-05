package com.sudzhaev.parzher

import javax.xml.bind.Unmarshaller
import javax.xml.stream.XMLEventReader

data class Attribute(val name: String, val value: String)

data class Tag(
    val name: String,
    val attributes: List<Attribute> = emptyList(),
    val extract: List<Extract<Any>> = emptyList(),
    val terminate: Boolean = false,
    val unmarshalWrapper: UnmarshalWrapper<*>? = null
)

data class XMLFilter(val tag: Tag, val nestedFilters: List<XMLFilter>) {

    fun reverse(): Map<Tag, Tag?> {
        val map = HashMap<Tag, Tag?>().apply { put(tag, null) }
        nestedFilters.forEach { map.putAll(it.reverse(tag)) }
        return map
    }

    private fun reverse(parentTag: Tag): Map<Tag, Tag?> = HashMap<Tag, Tag?>().apply {
        put(tag, parentTag)
        putAll(nestedFilters.map { it.reverse(tag) }.reduceSumming())
    }
}

data class Extract<out T>(val attributeName: String, val propertyName: String, val converter: (String?) -> T?)

data class UnmarshalWrapper<T>(
    val clazz: Class<out T>,
    val unmarshaller: Unmarshaller,
    val propertyName: String = clazz.simpleName.decapitalize(),
    val resultHandler: (T) -> T? = { it }
) {

    fun unmarshal(xmlEventReader: XMLEventReader): Pair<String, T?> =
        propertyName to resultHandler(unmarshaller.unmarshal(xmlEventReader, clazz).value)
}

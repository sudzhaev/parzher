package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent

class XmlEventParser(private val xmlFilters: List<XMLFilter>) {

    private val reversedFlatFilter: Map<Tag, Tag?> by lazy {
        xmlFilters.map { it.reverse() }.reduceSumming()
    }

    private val skipStack = Stack<String>()
    private val readStack = Stack<Tag>()
    private val filterStack = Stack<List<XMLFilter>>().apply { push(xmlFilters) }

    fun accept(event: XMLEvent): WrappedTag? {
        return when {
            event.isStartElement -> handleStartElement(event as StartElement)
            event.isEndElement -> handleEndElement(event as EndElement)
            else -> null
        }
    }

    private fun handleStartElement(startElement: StartElement): StartTag? {
        if (skipStack.isNotEmpty()) {
            return null
        }
        val tagName = startElement.localname()
        filterStack.peek()
            .asSequence()
            .map { it.tag }
            .filter { it.name == tagName }
            .filter { it.attributes in startElement }
            .forEach { filterTag ->
                val lastRead = readStack.peekOrNull()
                val parentTag = reversedFlatFilter[filterTag]
                if (parentTag == null || lastRead == parentTag) {
                    filterStack.push(filterStack.peek().flatMap { it.nestedFilters })
                    readStack.push(filterTag)
                    return StartTag(filterTag)
                }
                skipStack.push(tagName)
            }
        return null
    }

    private fun handleEndElement(endElement: EndElement): EndTag? {
        val tagName = endElement.localname()
        if (skipStack.peekOrNull() == tagName) {
            skipStack.pop()
        }
        if (readStack.peekOrNull()?.name == tagName) {
            pop()
            return EndTag
        }
        return null
    }

    fun pop() {
        filterStack.pop()
        readStack.pop()
    }
}

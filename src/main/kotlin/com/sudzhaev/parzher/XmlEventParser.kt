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
        getFilterTags(startElement).forEach { filterTag ->
            if (isFilterMatches(filterTag)) {
                filterStack.push(filterStack.peek().flatMap { it.nestedFilters })
                readStack.push(filterTag)
                return StartTag(filterTag)
            }
            skipStack.push(startElement.name())
        }
        return null
    }

    private fun handleEndElement(endElement: EndElement): EndTag? {
        val tagName = endElement.name()
        if (skipStack.peekOrNull() == tagName) {
            skipStack.pop()
        }
        if (readStack.peekOrNull()?.name == tagName) {
            pop()
            return EndTag
        }
        return null
    }

    private fun getFilterTags(startElement: StartElement): Sequence<Tag> {
        return filterStack.peek()
            .asSequence()
            .map { it.tag }
            .filter { it.name == startElement.name() }
            .filter { it.attributes in startElement }
    }

    private fun isFilterMatches(filterTag: Tag): Boolean {
        val lastRead = readStack.peekOrNull()
        val parentTag = reversedFlatFilter[filterTag]
        return parentTag == null || lastRead == parentTag
    }

    fun pop() {
        filterStack.pop()
        readStack.pop()
    }
}

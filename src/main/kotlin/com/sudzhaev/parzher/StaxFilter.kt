package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.EventFilter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent


class StaxFilter(
    private val xmlFilters: List<XMLFilter>,
    private val acceptedCallback: (Tag) -> Unit
) : EventFilter {

    private val reversedFlatFilter: Map<Tag, Tag?> by lazy {
        xmlFilters
            .map { it.reverse() }
            .reduce { acc, value -> acc + value }
    }

    private val skipStack = Stack<String>()
    private val readStack = Stack<Tag>()
    private val filterStack = Stack<List<XMLFilter>>().apply { push(xmlFilters) }

    var filterEnded = false

    override fun accept(event: XMLEvent): Boolean {
        return when {
            event.isStartElement -> handleStartElement(event as StartElement)
            event.isEndElement -> handleEndElement(event as EndElement)
            else -> false
        }
    }

    private fun handleStartElement(startElement: StartElement): Boolean {
        filterEnded = false
        if (skipStack.isNotEmpty()) {
            return false
        }
        val tagName = startElement.localname()
        filterStack.peek()
            .asSequence()
            .map { it.tag }
            .filter { it.name == tagName }
            .filter { it.attributes.allMatch(startElement) }
            .forEach { filterTag ->
                val lastRead = readStack.peekOrNull()
                val parentTag = reversedFlatFilter[filterTag]
                if (parentTag == null || lastRead == parentTag) {
                    filterStack.push(filterStack.peek().flatMap { it.nestedFilters })
                    readStack.push(filterTag)
                    acceptedCallback(filterTag)
                    return true
                }
                skipStack.push(tagName)
            }
        return false
    }

    private fun handleEndElement(endElement: EndElement): Boolean {
        val tagName = endElement.localname()
        if (skipStack.peekOrNull() == tagName) {
            skipStack.pop()

        }
        if (readStack.peekOrNull()?.name == tagName) {
            readStack.pop()
            filterStack.pop()
            if (readStack.isEmpty()) {
                println("filter ended")
                filterEnded = true
            }
        }
        return false
    }
}

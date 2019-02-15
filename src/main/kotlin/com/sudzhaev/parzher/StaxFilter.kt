package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.EventFilter
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent


class StaxFilter(val xmlFilters: List<XMLFilter>) : EventFilter {

    private val reversedFlatFilter: Map<Tag, Tag?> by lazy {
        xmlFilters
            .map { it.reverse() }
            .reduce { acc, value -> acc + value }
    }

    private val skipStack = Stack<String>()
    private val readStack = Stack<Tag>()
    private val filterStack = Stack<List<XMLFilter>>().apply {
        push(xmlFilters)
    }

    override fun accept(event: XMLEvent): Boolean {
        return when {
            event.isStartElement -> handleStartElement(event as StartElement)
            event.isEndElement -> handleEndElement(event as EndElement)
            else -> return false
        }
    }

    private fun handleStartElement(startElement: StartElement): Boolean {
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
                if (lastRead != null) {
                    if (parentTag == lastRead) {
                        filterStack.push(filterStack.peek().flatMap { it.nestedFilters })
                        readStack.push(filterTag)
                        return true
                    }
                }
                if (parentTag == null) {
                    filterStack.push(filterStack.peek().flatMap { it.nestedFilters })
                    readStack.push(filterTag)
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
        }
        return false
    }
}

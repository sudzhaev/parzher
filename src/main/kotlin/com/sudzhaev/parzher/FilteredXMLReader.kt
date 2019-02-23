package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamException
import javax.xml.stream.events.XMLEvent
import javax.xml.stream.util.EventReaderDelegate

class FilteredXMLReader(reader: XMLEventReader, private val staxFilter: StaxFilter) : EventReaderDelegate(reader) {

    private var cache: XMLEvent? = null

    private fun getCacheAndReset(): XMLEvent? {
        try {
            return cache
        } finally {
            cache = null
        }
    }

    override fun next(): Any {
        return this.nextEvent()
    }

    override fun hasNext(): Boolean {
        return try {
            this.peek() != null
        } catch (e: XMLStreamException) {
            false
        }
    }

    override fun nextEvent(): XMLEvent {
        if (cache != null) {
            super.nextEvent()
            return getCacheAndReset()!!
        }
        if (super.hasNext()) {
            val event = super.nextEvent()
            return if (staxFilter.accept(event)) {
                cache = null
                event
            } else {
                this.nextEvent()
            }
        } else {
            throw NoSuchElementException()
        }
    }

    override fun nextTag(): XMLEvent = throw UnsupportedOperationException()

    override fun peek(): XMLEvent? {
        if (cache != null) {
            return getCacheAndReset()
        }
        while (true) {
            val event = super.peek() ?: return null
            if (staxFilter.accept(event)) {
                cache = event
                return event
            }
            super.next()
        }
    }
}

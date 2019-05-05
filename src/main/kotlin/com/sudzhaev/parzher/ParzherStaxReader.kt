package com.sudzhaev.parzher

import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.XMLEvent

class ParzherStaxReader(reader: XMLEventReader) : XMLEventReader by reader, Iterable<XMLEvent> {

    private var previousEvent: XMLEvent? = null

    override fun hasNext(): Boolean {
        if (previousEvent == null) {
            previousEvent = peek()
        } else {
            nextEvent()
            previousEvent = peek()
        }
        return previousEvent != null
    }

    override fun next(): XMLEvent? {
        return previousEvent
    }

    override fun iterator(): Iterator<XMLEvent> {
        @Suppress("UNCHECKED_CAST")
        return this as Iterator<XMLEvent>
    }
}

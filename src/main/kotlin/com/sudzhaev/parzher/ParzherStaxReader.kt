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

    override fun next(): XMLEvent? = previousEvent

    @Suppress("UNCHECKED_CAST")
    override fun iterator(): Iterator<XMLEvent> = this as Iterator<XMLEvent>
}

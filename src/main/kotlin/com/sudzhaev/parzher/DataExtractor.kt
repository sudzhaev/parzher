package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class DataExtractor(private val xmlEventReader: XMLEventReader, private val xmlEventParser: XmlEventParser) {

    private val attributeStack = Stack<Pair<Tag, Map<String, String?>>>()

    fun next(): Map<String, String?>? {
        while (xmlEventReader.hasNext()) {
            val xmlEvent = xmlEventReader.nextEvent()
            val wrappedTag = xmlEventParser.accept(xmlEvent) ?: continue
            when (wrappedTag) {
                is StartTag -> {
                    val tag = wrappedTag.tag
                    val startElement = xmlEvent as StartElement
                    val extractedAttributes = startElement.extract(tag.extract)
                    attributeStack.push(tag to extractedAttributes)
                    if (tag.terminate) {
                        return attributeStack.toMap()
                    }
                }
                EndTag -> attributeStack.pop()
            }
        }
        return null
    }
}

package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.events.StartElement

class DataExtractor(private val parzherStaxReader: ParzherStaxReader, private val xmlEventParser: XmlEventParser) {

    private val attributeStack = Stack<Map<String, Any?>>()

    fun next(): Map<String, Any?>? {
        for (xmlEvent in parzherStaxReader) {
            when (val wrappedTag = xmlEventParser.accept(xmlEvent)) {
                is StartTag -> {
                    val tag = wrappedTag.tag
                    val startElement = xmlEvent as StartElement
                    val extractedAttributes = startElement.getAttributes(tag.extract).toMutableMap()
                    if (tag.unmarshalWrapper != null) {
                        val unmarshalResult = tag.unmarshalWrapper.unmarshal(parzherStaxReader)
                        extractedAttributes += unmarshalResult
                        xmlEventParser.pop()
                    }
                    attributeStack.push(extractedAttributes)
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

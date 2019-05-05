package com.sudzhaev.parzher

import java.util.*
import javax.xml.stream.events.StartElement

class DataExtractor(private val parzherStaxReader: ParzherStaxReader, private val xmlEventParser: XmlEventParser) {

    private val dataStack = Stack<Map<String, Any?>>()

    fun next(): Map<String, Any?>? {
        for (xmlEvent in parzherStaxReader) {
            when (val wrappedTag = xmlEventParser.accept(xmlEvent)) {
                is StartTag -> {
                    val tag = wrappedTag.tag
                    val startElement = xmlEvent as StartElement
                    val extractedData = getData(tag, startElement)
                    dataStack.push(extractedData)
                    if (tag.terminate) {
                        return dataStack.toMap()
                    }
                }
                EndTag -> dataStack.pop()
            }
        }
        return null
    }

    private fun getData(tag: Tag, startElement: StartElement): Map<String, Any?> {
        val extractedAttributes = startElement.getAttributes(tag.extract).toMutableMap()
        if (tag.unmarshaller != null) {
            val unmarshalResult = tag.unmarshaller.unmarshal(parzherStaxReader)
            extractedAttributes += unmarshalResult
            xmlEventParser.pop()
        }
        return extractedAttributes
    }
}

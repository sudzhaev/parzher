package com.sudzhaev.parzher

import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class FilteredXMLExtractReader(reader: XMLEventReader, xmlFilters: List<XMLFilter>) {

    private val staxFilter = StaxFilter(xmlFilters) { tag -> attributes = tag.extract }
    private val filteredXMLReader = FilteredXMLReader(reader, staxFilter)
    private var attributes = emptyList<String>()

    fun hasNext() = filteredXMLReader.hasNext()

    fun nextEvent(): ExtractResult {
        val element = filteredXMLReader.nextEvent() as StartElement
        return ExtractResult(element.localname(), element.extract(attributes))
    }
}

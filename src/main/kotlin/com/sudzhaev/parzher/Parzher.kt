package com.sudzhaev.parzher

import javax.xml.stream.XMLEventReader

class Parzher<T>(val clazz: Class<T>, xmlEventReader: XMLEventReader, filters: List<XMLFilter>) {
    // TODO: implements Iterable

    private val xmlEventParser = XmlEventParser(filters)
    private val dataExtractor = DataExtractor(xmlEventReader, xmlEventParser)
    private val objectExtractor = ObjectExtractor(clazz)

    fun next(): T? {
        val attrs = dataExtractor.next() ?: return null
        return objectExtractor.get(attrs);
    }
}

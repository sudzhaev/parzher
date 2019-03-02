package com.sudzhaev.parzher

import javax.xml.stream.XMLEventReader

class Parzher<T>(clazz: Class<T>, xmlEventReader: XMLEventReader, filters: List<XMLFilter>) : Iterable<T> {

    private val xmlEventParser = XmlEventParser(filters)
    private val dataExtractor = DataExtractor(xmlEventReader, xmlEventParser)
    private val objectExtractor = ObjectExtractor(clazz)

    override fun iterator() = ParzherIterator<T>(this)

    fun next(): T? {
        val attrs = dataExtractor.next() ?: return null
        return objectExtractor.get(attrs);
    }
}

class ParzherIterator<T>(private val parzher: Parzher<T>) : Iterator<T> {

    private var cache: T? = null

    override fun hasNext(): Boolean {
        if (cache != null) {
            return true
        }
        cache = parzher.next()
        return cache != null
    }

    override fun next(): T {
        if (cache == null && !hasNext()) {
            throw NoSuchElementException()
        }
        val item = cache!!
        cache = null
        return item
    }
}

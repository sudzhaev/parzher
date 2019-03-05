package com.sudzhaev.parzher

@DslMarker
annotation class XmlFilterDsl

@XmlFilterDsl
fun filters(block: XMLFilterListBuilder.() -> Unit): MutableList<XMLFilter> {
    val xmlFilters = XMLFilterListBuilder().apply(block).build()
    val terminates = countTerminates(xmlFilters)
    if (terminates == 0) {
        throw InvalidFilterException("Specify one terminate tag")
    }
    if (terminates > 1) {
        throw InvalidFilterException("There can be only one terminate tag")
    }
    return xmlFilters
}

/**
 * Count terminate tags in List<XmlFilter>
 * @return 0 if no terminates found; 1 if only one terminate found; 2 if more than one terminate found
 */
private fun countTerminates(xmlFilters: List<XMLFilter>): Int {
    return traverseFilters(xmlFilters, 0);
}

private fun traverseFilters(xmlFilters: List<XMLFilter>, upperCounter: Int): Int {
    var terminateCount = 0
    for (xmlFilter in xmlFilters) {
        if (xmlFilter.tag.terminate) {
            terminateCount += 1
            if (terminateCount + upperCounter > 1) {
                return terminateCount
            }
        }
        terminateCount += traverseFilters(xmlFilter.nestedFilters, terminateCount)
        if (terminateCount + upperCounter > 1) {
            return terminateCount
        }
    }
    return terminateCount
}

@XmlFilterDsl
class XMLFilterListBuilder {

    private val xmlFilters = mutableListOf<XMLFilter>()

    fun tag(name: String, block: XMLFilterBuilder.() -> Unit) {
        xmlFilters.add(XMLFilterBuilder(name).apply(block).build())
    }

    fun build(): MutableList<XMLFilter> {
        if (xmlFilters.isEmpty()) throw InvalidFilterException("Filter list cannot be empty")
        return xmlFilters
    }
}

@XmlFilterDsl
class XMLFilterBuilder(val name: String) {

    private var terminate = false
    private val attributes = mutableListOf<Attribute>()
    private val nestedTags = mutableListOf<XMLFilter>()
    private val extract = mutableListOf<Extract<Any>>()

    fun attributes(block: AttributeListBuilder.() -> Unit) {
        attributes.addAll(AttributeListBuilder().apply(block).build())
    }

    fun nested(block: XMLFilterListBuilder.() -> Unit) {
        nestedTags.addAll(XMLFilterListBuilder().apply(block).build())
    }

    fun extract(block: ExtractBuilder.() -> Unit) {
        extract.addAll(ExtractBuilder().apply(block).build())
    }

    fun terminate() {
        terminate = true
    }

    fun build(): XMLFilter {
        if (name.isEmpty()) throw InvalidFilterException("Tag name cannot be empty")
        return XMLFilter(Tag(name, attributes, extract, terminate), nestedTags)
    }
}

@XmlFilterDsl
class ExtractBuilder {

    private val attributes = mutableListOf<Extract<Any>>()

    fun attribute(attributeName: String, newName: String = attributeName) {
        attributes.add(Extract(attributeName, newName) { it })
    }

    fun <T : Any> attribute(attributeName: String, propertyName: String = attributeName, extractor: (String?) -> T?) {
        attributes.add(Extract(attributeName, propertyName, extractor))
    }

    fun build(): MutableList<Extract<Any>> {
        if (attributes.isEmpty()) throw InvalidFilterException(
            """
            Extract attributes cannot be empty:
            specify at least one attribute or
            remove extract block""".simplify()
        )
        return attributes
    }
}

@XmlFilterDsl
class AttributeListBuilder {

    private val attributes = mutableListOf<Attribute>()

    fun attribute(name: String, value: String) {
        attributes.add(Attribute(name, value))
    }

    fun build(): MutableList<Attribute> {
        if (attributes.isEmpty()) throw InvalidFilterException(
            """
            Attribute list cannot be empty:
            specify at least one attribute or
            remove attribute block""".simplify()
        )
        return attributes
    }
}

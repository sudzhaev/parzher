package com.sudzhaev.parzher

@DslMarker
annotation class XmlFilterDsl

@XmlFilterDsl
fun filters(block: XMLFilterListBuilder.() -> Unit) = XMLFilterListBuilder().apply(block).build()

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

    fun attribute(attribute: String) {
        attributes.add(Extract(attribute) { it ?: "null" })
    }

    fun <T : Any> attribute(attribute: String, extractor: (String?) -> T) {
        attributes.add(Extract(attribute, extractor))
    }

    fun build(): MutableList<Extract<Any>> {
        if (attributes.isEmpty()) throw InvalidFilterException(
            """
            Extract attributes cannot be empty:
            specify at least one attribute or
            remove extract block""".trimIndent()
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
            remove attribute block""".trimIndent()
        )
        return attributes
    }
}

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


    fun build() = xmlFilters
}

@XmlFilterDsl
class XMLFilterBuilder(val name: String) {

    var terminate = false
    private val attributes = mutableListOf<Attribute>()
    private val nestedTags = mutableListOf<XMLFilter>()
    private val extract = mutableListOf<String>()

    fun attributes(block: AttributeListBuilder.() -> Unit) {
        attributes.addAll(AttributeListBuilder().apply(block).build())
    }

    fun nested(block: XMLFilterListBuilder.() -> Unit) {
        nestedTags.addAll(XMLFilterListBuilder().apply(block).build())
    }

    fun extract(block: ExtractBuilder.() -> Unit) {
        extract.addAll(ExtractBuilder().apply(block).build());
    }

    fun build() = XMLFilter(Tag(name, attributes, extract, terminate), nestedTags)
}

@XmlFilterDsl
class ExtractBuilder {

    private val attributes = mutableListOf<String>()

    fun attribute(attribute: String) {
        attributes.add(attribute)
    }

    fun build() = attributes
}

@XmlFilterDsl
class AttributeListBuilder {

    private val attributes = mutableListOf<Attribute>()

    fun attribute(name: String, value: String) {
        attributes.add(Attribute(name, value))
    }

    fun build() = attributes
}

@XmlFilterDsl
class AttributeBuilder {

    var name = ""
    var value = ""

    fun build() = Attribute(name, value)
}
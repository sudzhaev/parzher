package com.sudzhaev.parzher

@DslMarker
annotation class XmlFilterDsl

@XmlFilterDsl
fun filters(block: XMLFilterListBuilder.() -> Unit) = XMLFilterListBuilder().apply(block).build()

@XmlFilterDsl
class XMLFilterListBuilder {

    private val xmlFilters = mutableListOf<XMLFilter>()

    fun tag(block: XMLFilterBuilder.() -> Unit) {
        xmlFilters.add(XMLFilterBuilder().apply(block).build())
    }


    fun build() = xmlFilters
}

@XmlFilterDsl
class XMLFilterBuilder {

    var name: String = ""
    private val attributes = mutableListOf<Attribute>()
    private val nestedTags = mutableListOf<XMLFilter>()

    fun attributes(block: AttributeListBuilder.() -> Unit) {
        attributes.addAll(AttributeListBuilder().apply(block).build())
    }

    fun nested(block: XMLFilterListBuilder.() -> Unit) {
        nestedTags.addAll(XMLFilterListBuilder().apply(block).build())
    }

    fun build() = XMLFilter(Tag(name, attributes), nestedTags)
}

@XmlFilterDsl
class AttributeListBuilder {

    private val attributes = mutableListOf<Attribute>()

    fun attribute(block: AttributeBuilder.() -> Unit) {
        attributes.add(AttributeBuilder().apply(block).build())
    }

    fun build() = attributes
}

@XmlFilterDsl
class AttributeBuilder {

    var name = ""
    var value = ""

    fun build() = Attribute(name, value)
}

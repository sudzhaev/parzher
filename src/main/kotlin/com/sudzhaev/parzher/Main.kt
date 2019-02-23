package com.sudzhaev.parzher

import java.io.FileInputStream
import java.util.*
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement

fun main() {
    val xmlInputFactory = XMLInputFactory.newInstance()
    val xmlEventReader = xmlInputFactory.createXMLEventReader(FileInputStream("src/main/resources/somexml.xml"))
    val filters = buildFilterDsl()
    val xmlEventParser = XmlEventParser(filters)
    val attributeStack = Stack<Pair<Tag, Map<String, String?>>>()
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
                    println(attributeStack.map { it.second })
                }
            }
            EndTag -> attributeStack.pop()
        }
    }
}

fun buildFilterDsl() = filters {
    tag("div") {
        attributes {
            attribute(name = "class", value = "name")
        }
        nested {
            tag("elements") {
                attributes {
                    attribute(name = "val", value = "2")
                }
                extract {
                    attribute("param")
                }
                nested {
                    tag("element") {
                        terminate = true
                        attributes {
                            attribute(name = "size", value = "s")
                        }
                        extract {
                            attribute("size")
                        }
                    }
                }
            }
        }
    }
}

package com.sudzhaev.parzher

import java.io.FileInputStream
import javax.xml.stream.XMLInputFactory

data class ValueDto(val param: String, val size: String)

fun main() {
    val xmlInputFactory = XMLInputFactory.newInstance()
    val xmlEventReader = xmlInputFactory.createXMLEventReader(FileInputStream("src/main/resources/somexml.xml"))
    val filters = buildFilterDsl()
    val xmlEventParser = XmlEventParser(filters)
    val extractor = DataExtractor(xmlEventReader, xmlEventParser)
    val objectRetriever = ObjectExtractor(ValueDto::class.java)
    var attrs = extractor.next()
    while (attrs != null) {
        println(objectRetriever.convert(attrs))
        attrs = extractor.next()
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
